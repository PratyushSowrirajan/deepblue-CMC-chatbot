package com.example.healthassistant.data.local.profile

import com.example.healthassistant.db.HealthDatabase

class MedicalProfileLocalDataSourceImpl(
    private val database: HealthDatabase
) : MedicalProfileLocalDataSource {

    override suspend fun insert(
        questionId: String,
        questionText: String,
        answerJson: String
    ) {

        database.medicalProfileQueries.insertOrReplace(
            question_id = questionId,
            question_text = questionText,
            answer_json = answerJson
        )
    }

    override suspend fun clearAll() {
        database.medicalProfileQueries.deleteAll()
    }
}