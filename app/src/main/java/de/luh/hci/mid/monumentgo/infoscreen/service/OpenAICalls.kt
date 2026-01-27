package de.luh.hci.mid.monumentgo.infoscreen.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.luh.hci.mid.monumentgo.core.data.model.MonumentWithDetails
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64
import androidx.core.graphics.scale
import de.luh.hci.mid.monumentgo.infoscreen.service.OKHttp.client
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

const val userAgent = "MonumentGo/1.0 (https://github.com/HoffmannDean/MonumentGo; john.klein@stud.uni-hannover.de)"
const val maxArticleLength = 6_000

object OKHttp {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

private fun buildMonumentList(monuments: List<MonumentWithDetails>): String {
    return monuments.mapIndexed { index, details ->
        val metadata = details.osmTags.map { entry ->
            "${entry.key}: ${entry.value}"
        }.joinToString()
        "${index + 1}. ${details.name} (region: \"${details.region}\", ${metadata})"
    }.joinToString(separator = "\n")
}

fun matchMonument(
    imageFile: File,
    apiKey: String,
    monuments: List<MonumentWithDetails>,
    callback: (MonumentWithDetails?) -> Unit
) {

    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
    val scaled = bitmap.scale(1024, 1024)
    val stream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val base64Image = Base64.getEncoder().encodeToString(stream.toByteArray())
    val monumentList = buildMonumentList(monuments)

    val prompt = """
        
        You are given an image and a fixed list of monuments with their names and a list of metadata tags, gathered from OpenStreetMaps.
        Your task is to determine whether the image depicts one and only one monument from the provided list.
        
        Instructions:
        - Carefully analyze the visual features of the image (architecture, materials, shape, surroundings, etc.)
        - Compare these features only against the monuments in the given list
        
        Constraints:
        - Do not invent new monuments
        - There can be some uncertainty in the guess, but it should not be completely off
        
        Output rules:
        - If the image matches exactly one monument from the list, return the number of the monument in the list
        - If the image does not match any monuments from the list, return 0
        - If the guess is too uncertain, return 0 
        - Output MUST be just a single integer number ranging from 0 to ${monuments.size} and nothing else
        
        List of monuments:
        $monumentList
        
        """.trimIndent()

    val contentArray = JSONArray().put(
            JSONObject().put("type", "text").put(
                    "text", prompt
                )
        ).put(
            JSONObject().put("type", "image_url")
                .put("image_url", JSONObject().put("url", "data:image/jpeg;base64,$base64Image"))
        )

    val message = JSONObject().put("role", "user").put("content", contentArray)

    val jsonBody =
        JSONObject().put("model", "gpt-4o-mini").put("messages", JSONArray().put(message))

    val requestBody = RequestBody.create(
        "application/json".toMediaType(), jsonBody.toString()
    )

    val request = Request.Builder().url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey").post(requestBody).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("openai", "Error: ${e.message}")
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (response.isSuccessful) {
                    try {
                        val json = JSONObject(response.body.string())
                        val content =
                            json.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                                .getString("content")
                        val mIndex = content.toInt() - 1
                        val monument = if (mIndex in monuments.indices) monuments[mIndex] else null
                        callback(monument)
                    } catch (e: Exception) {
                        callback(null)
                    }
                } else {
                    Log.e("openai", "API Error: ${response.code}")
                    callback(null)
                }
            }
        }
    })
}

fun getWikipediaArticle(
    monument: MonumentWithDetails, apiKey: String, callback: (String?) -> Unit
) {
    // Extract the article title from the URL
    val title = monument.wikiUrl.substringAfter("/wiki/")

    // Wikipedia API endpoint
    val apiUrl = "https://de.wikipedia.org/w/api.php" +
            "?action=query" +
            "&prop=extracts" +
            "&explaintext=true" +  // get plain text
            "&format=json" +
            "&titles=$title"

    val request = Request.Builder()
        .url(apiUrl)
        .addHeader("User-Agent", userAgent)
        .build()

    client.newCall(request).execute().use { response ->
        if (response.isSuccessful) {
            val body = response.body.string()
            val json = JSONObject(body)
            val pages = json.getJSONObject("query").getJSONObject("pages")
            val page = pages.keys().asSequence().firstOrNull()?.let { pages.getJSONObject(it) }
            val result = page?.optString("extract")?.take(maxArticleLength)
            callback(result)
        } else {
            callback(null)
        }
    }
}

fun generateTTS(
    description: String, apiKey: String, outputFile: File, callback: (Boolean) -> Unit
) {
    val jsonBody =
        JSONObject().put("model", "gpt-4o-mini-tts").put("voice", "alloy").put("format", "mp3")
            .put("input", description)

    val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder().url("https://api.openai.com/v1/audio/speech")
        .addHeader("Authorization", "Bearer $apiKey").post(requestBody).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(false)
        }

        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                callback(false)
                return
            }

            response.body?.byteStream()?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            callback(true)
        }
    })
}


fun generateSummary(
    monument: MonumentWithDetails,
    article: String,
    apiKey: String,
    callback: (String?) -> Unit
) {
    Log.e("openai", "Article length = ${article.length}")
    val prompt = """
        You are given a German wikipedia article about ${monument.name} in ${monument.region}.
        Create a concise summary of information in this article with some interesting facts (in english).
        You can use markdown to stylize the summary.
        The title of the summary should just be "${monument.name}".
        
        Wikipedia article about ${monument.name}:

        $article
    """.trimIndent()

    val message = JSONObject().put("role", "user")
        .put("content", JSONArray().put(JSONObject().put("type", "text").put("text", prompt)))

    val jsonBody =
        JSONObject().put("model", "gpt-4o-mini").put("messages", JSONArray().put(message))

    val requestBody = RequestBody.create(
        "application/json".toMediaType(), jsonBody.toString()
    )

    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("User-Agent", userAgent)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("openai", "Error: ${e.message}")
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    val errorBody = response.body.string()
                    Log.e("openai", "OPENAI ERROR: $errorBody")
                    callback(null)
                } else {
                    val json = JSONObject(response.body.string())
                    val content = json
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    callback(content)
                }
            }
        }
    })
}

fun generateQuiz(
    monument: MonumentWithDetails,
    summary: String,
    apiKey: String,
    callback: (List<Triple<String, String, List<String>>>?) -> Unit
) {
    val prompt = """
        You are given a summary of the wikipedia article about ${monument.name} in ${monument.region}.
        Create a quiz with 3 questions that can be answered by reading the summary.

        Rules:
        - Create exactly 3 questions
        - Each question must be 1–2 sentences
        - Each question must have exactly 3 answer options:
          - "correct" for the correct answer (1–4 words)
          - "incorrect" for each of the two incorrect answers (1–4 words each)
        - Answers must be unambiguous
        - Someone should be able to answer the quiz by reading the summary
        - Do NOT include explanations
        - Output of the quiz MUST be strictly valid JSON and nothing else
        
        Output Format:
        {
            "questions": [
                {
                    "question": "text",
                    "correct": "text",
                    "incorrect": ["text", "text"]
                },
                {
                    "question": "text",
                    "correct": "text",
                    "incorrect": ["text", "text"]
                },
                {
                    "question": "text",
                    "correct": "text",
                    "incorrect": ["text", "text"]
                }
            ]
        }
        
        Summary about ${monument.name}:

        $summary
    """.trimIndent()

    val message = JSONObject().put("role", "user")
        .put("content", JSONArray().put(JSONObject().put("type", "text").put("text", prompt)))

    val jsonBody =
        JSONObject().put("model", "gpt-4o-mini").put("messages", JSONArray().put(message))

    val requestBody = RequestBody.create(
        "application/json".toMediaType(), jsonBody.toString()
    )

    val request = Request.Builder().url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey").post(requestBody).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback(null)
                    return
                }

                val content =
                    JSONObject(response.body.string()).getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content")

                try {
                    val quizJson = JSONObject(content)
                    val questionsArray = quizJson.getJSONArray("questions")

                    val result = mutableListOf<Triple<String, String, List<String>>>()
                    for (i in 0 until questionsArray.length()) {
                        val q = questionsArray.getJSONObject(i)
                        val question = q.getString("question")
                        val correct = q.getString("correct")
                        val incorrect = q.getJSONArray("incorrect").let { arr ->
                            List(arr.length()) { idx -> arr.getString(idx) }
                        }
                        result.add(Triple(question, correct, incorrect))
                    }

                    callback(result)
                } catch (e: Exception) {
                    // In case GPT returns invalid JSON
                    callback(null)
                }
            }
        }
    })
}

