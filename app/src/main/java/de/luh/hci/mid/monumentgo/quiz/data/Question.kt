package de.luh.hci.mid.monumentgo.quiz.data

data class Question (
    val text: String,
    val answers: List<String>,
    val correctIndex: Int
)