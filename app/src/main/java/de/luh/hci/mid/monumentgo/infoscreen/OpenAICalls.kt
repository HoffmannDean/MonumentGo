package de.luh.hci.mid.monumentgo.infoscreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64

fun describeImage(imageFile: File, apiKey: String, callback: (String?) -> Unit) {
    val client = OkHttpClient()

    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
    val scaled = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)
    val stream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val base64Image = Base64.getEncoder().encodeToString(stream.toByteArray())

    val contentArray = JSONArray()
        .put(JSONObject().put("type", "text").put("text", "What is depicted in the given image? Create a short summary of information about this with some interesting facts in plain text."))
        .put(JSONObject().put("type", "image_url").put("image_url", JSONObject().put("url", "data:image/jpeg;base64,$base64Image")))

    val message = JSONObject()
        .put("role", "user")
        .put("content", contentArray)

    val jsonBody = JSONObject()
        .put("model", "gpt-4o-mini")
        .put("messages", JSONArray().put(message))

    val requestBody = RequestBody.create(
        "application/json".toMediaType(),
        jsonBody.toString()
    )

    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback("Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback("API Error: ${response.code}")
                } else {
                    val json = JSONObject(response.body!!.string())
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
    description: String,
    apiKey: String,
    callback: (List<Triple<String, String, List<String>>>?) -> Unit
) {
    val client = OkHttpClient()

    val prompt = """
        You are creating a short quiz based ONLY on the following image description.

        Rules:
        - Create exactly 3 questions
        - Each question must be 1–2 sentences
        - Each question must have exactly 3 answer options:
          - "correct" for the correct answer (1–4 words)
          - "incorrect" for each of the two incorrect answers (1–4 words each)
        - Answers must be unambiguous
        - Do NOT include explanations
        - Output MUST be strictly valid JSON and nothing else
        - JSON format:
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
        
        Image description:

        $description
    """.trimIndent()

    val message = JSONObject()
        .put("role", "user")
        .put("content", JSONArray().put(JSONObject().put("type", "text").put("text", prompt)))

    val jsonBody = JSONObject()
        .put("model", "gpt-4o-mini")
        .put("messages", JSONArray().put(message))

    val requestBody = RequestBody.create(
        "application/json".toMediaType(),
        jsonBody.toString()
    )

    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

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

                val content = JSONObject(response.body!!.string())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

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

fun extractMonumentName(
    description: String,
    apiKey: String,
    callback: (String?) -> Unit
) {
    val client = OkHttpClient()

    val prompt = """
        Extract the name of the monument from the following text.
        Output MUST be strictly valid JSON and nothing else.
        JSON format:
        { "monument": "Name of the monument" }

        Text:
        $description
    """.trimIndent()

    val message = JSONObject()
        .put("role", "user")
        .put("content", JSONArray().put(JSONObject().put("type", "text").put("text", prompt)))

    val jsonBody = JSONObject()
        .put("model", "gpt-4o-mini")
        .put("messages", JSONArray().put(message))

    val requestBody = RequestBody.create(
        "application/json".toMediaType(),
        jsonBody.toString()
    )

    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

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

                val content = JSONObject(response.body!!.string())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                try {
                    val json = JSONObject(content)
                    callback(json.getString("monument"))
                } catch (e: Exception) {
                    callback(null)
                }
            }
        }
    })
}

