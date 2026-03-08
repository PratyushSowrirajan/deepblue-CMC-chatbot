package com.example.healthassistant.presentation.assessment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.domain.model.assessment.PossibleCause
import com.example.healthassistant.domain.model.assessment.Report
import com.example.healthassistant.presentation.history.Bullet
import com.example.healthassistant.presentation.history.CauseItem
import com.example.healthassistant.presentation.history.SectionTitle

@Composable
fun AssessmentReportScreen(
    report: Report,
    onBack: () -> Unit,
    onCauseClick: (PossibleCause) -> Unit,
    onGoHome: () -> Unit,
    onAskChatbot: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ───── Top Bar ─────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = t("Assessment Report"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ───── Meta Info ─────
            Text(
                text = t("Report ID: ${report.reportId}"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = t("Generated at: ${report.generatedAt}"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ✅ Patient Info (NEW)
            Text(
                text = t("${report.patientInfo?.name}, ${report.patientInfo?.gender}, ${report.patientInfo?.age} years old"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ───── Urgency ─────
            Text(
                text = t("Urgency: ${report.urgencyLevel.replace("_", " ").uppercase()}"),
                color = when (report.urgencyLevel) {
                    "emergency" -> Color.Red
                    "doctor_visit_recommended" -> Color(0xFFFFA000)
                    else -> Color(0xFF2E7D32)
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ───── Summary ─────
            SectionTitle(t("Summary"))

            report.summary.forEach {
                Bullet(it)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ───── Possible Causes ─────
            SectionTitle(t("Possible Causes"))

            report.possibleCauses.forEach { cause ->

                CauseItem(
                    title = t(cause.title),
                    description = cause.shortDescription,
                    onTellMore = {
                        onCauseClick(cause)
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = cause.probability.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = when (cause.severity) {
                        "severe" -> Color.Red
                        "moderate" -> Color(0xFFFFA000)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = t("${(cause.probability * 100).toInt()}% likelihood"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ───── Advice ─────
            SectionTitle(t("What you were advised"))

            report.advice.forEach {
                Bullet(it)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* PDF later */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(t("Download Health Report"))
            }


            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGoHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(t("Go to Home Screen"))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAskChatbot,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(t("Ask Remy Chatbot"))
            }


        }
    }
}
