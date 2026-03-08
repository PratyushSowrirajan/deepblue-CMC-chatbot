package com.example.healthassistant.data.local.profile

import com.example.healthassistant.db.HealthDatabase

class GeneralProfileLocalDataSourceImpl(
    private val database: HealthDatabase
) : GeneralProfileLocalDataSource {

    override suspend fun insert(
        questionId: String,
        questionText: String,
        answerJson: String
    ) {

        database.generalProfileQueries.insertOrReplace(
            question_id = questionId,
            question_text = questionText,
            answer_json = answerJson
        )
    }

    override suspend fun clearAll() {
        database.generalProfileQueries.deleteAll()
    }
}