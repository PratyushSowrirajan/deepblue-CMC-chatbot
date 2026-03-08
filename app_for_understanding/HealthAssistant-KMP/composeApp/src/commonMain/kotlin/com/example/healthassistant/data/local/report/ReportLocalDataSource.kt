package com.example.healthassistant.data.local.report

import com.example.healthassistant.domain.model.assessment.Report

interface ReportLocalDataSource {

    suspend fun insert(report: Report)

    suspend fun getAll(): List<Report>

    suspend fun getById(id: String): Report?

    suspend fun clearAll()

}
