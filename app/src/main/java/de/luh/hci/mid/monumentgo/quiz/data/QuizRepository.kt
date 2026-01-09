package de.luh.hci.mid.monumentgo.quiz.data

object QuizRepository {
    // save question temporary after API req
    var currentQuestions: List<Question> = emptyList()

    var currentScore: Int = 0
}