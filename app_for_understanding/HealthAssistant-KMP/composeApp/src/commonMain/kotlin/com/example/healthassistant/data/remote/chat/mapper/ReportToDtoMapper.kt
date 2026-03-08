package com.example.healthassistant.data.remote.chat.mapper

import com.example.healthassistant.data.remote.assessment.dto.*
import com.example.healthassistant.domain.model.assessment.*

fun Report.toReportResponseDto(): ReportResponseDto {

    return ReportResponseDto(
        report_id = reportId,
        assessment_topic = topic,
        generated_at = generatedAt,
        patient_info = patientInfo?.toDto()
            ?: PatientInfoDto(
                name = "",
                age = 0,
                gender = ""
            ),
        summary = summary,
        possible_causes = possibleCauses.map { it.toDto() },
        advice = advice,
        urgency_level = urgencyLevel
    )
}

fun PatientInfo.toDto(): PatientInfoDto {
    return PatientInfoDto(
        name = name,
        age = age,
        gender = gender
    )
}

fun PossibleCause.toDto(): PossibleCauseDto {
    return PossibleCauseDto(
        id = id,
        title = title,
        short_description = shortDescription,
        subtitle = subtitle,
        severity = severity,
        probability = probability,
        detail = detail.toDto()
    )
}

fun CauseDetail.toDto(): CauseDetailDto {
    return CauseDetailDto(
        about_this = aboutThis,
        how_common = HowCommonDto(
            percentage = percentage,
            description = commonDescription
        ),
        what_you_can_do_now = whatYouCanDoNow,
        warning = warning
    )
}