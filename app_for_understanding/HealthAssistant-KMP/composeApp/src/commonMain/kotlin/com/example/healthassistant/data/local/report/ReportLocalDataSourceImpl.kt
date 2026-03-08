package com.example.healthassistant.data.local.report

import com.example.healthassistant.db.HealthDatabase
import com.example.healthassistant.domain.model.assessment.Report
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReportLocalDataSourceImpl(
    private val database: HealthDatabase
) : ReportLocalDataSource {

    override suspend fun insert(report: Report) {

        val json = Json.encodeToString(report)

        database.reportsQueries.insertOrReplace(
            id = report.reportId,
            report_json = json,
            date = report.generatedAt
        )
    }

    override suspend fun getAll(): List<Report> {

        return database.reportsQueries
            .getAll()
            .executeAsList()
            .map {
                Json.decodeFromString<Report>(it.report_json)
            }
    }

    override suspend fun getById(id: String): Report? {

        val row = database.reportsQueries
            .getById(id)
            .executeAsOneOrNull()

        return row?.let {
            Json.decodeFromString<Report>(it.report_json)
        }
    }

    override suspend fun clearAll() {
        database.reportsQueries.deleteAll()
    }
}
