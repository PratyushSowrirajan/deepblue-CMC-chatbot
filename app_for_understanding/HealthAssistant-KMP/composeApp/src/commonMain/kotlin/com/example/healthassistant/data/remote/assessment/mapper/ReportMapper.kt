package com.example.healthassistant.data.remote.assessment.mapper

import com.example.healthassistant.data.remote.assessment.dto.ReportDto
import com.example.healthassistant.domain.model.assessment.CauseDetail
import com.example.healthassistant.domain.model.assessment.PatientInfo
import com.example.healthassistant.domain.model.assessment.PossibleCause
import com.example.healthassistant.domain.model.assessment.Report

fun ReportDto.toDomain(): Report {

    return Report(
        reportId = report_id,
        topic = assessment_topic,
        generatedAt = generated_at,
        summary = summary,
        possibleCauses = possible_causes.map { cause ->

            PossibleCause(
                id = cause.id,
                title = cause.title,
                shortDescription = cause.short_description,
                subtitle = cause.subtitle,
                severity = cause.severity,
                probability = cause.probability,
                detail = CauseDetail(
                    aboutThis = cause.detail.about_this,
                    percentage = cause.detail.how_common.percentage,
                    commonDescription = cause.detail.how_common.description,
                    whatYouCanDoNow = cause.detail.what_you_can_do_now,
                    warning = cause.detail.warning
                )
            )
        },
        advice = advice,
        urgencyLevel = urgency_level,
        patientInfo = patient_info?.let {
            PatientInfo(
                name = it.name,
                age = it.age,
                gender = it.gender
            )
        }
    )
}