package com.example.healthassistant.presentation.assessment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.healthassistant.core.utils.t
import com.example.healthassistant.domain.model.assessment.PossibleCause
import com.example.healthassistant.presentation.history.Bullet
import com.example.healthassistant.presentation.history.SectionTitle

@Composable
fun AssessmentCauseDetailScreen(
    cause: PossibleCause,
    onBack: () -> Unit
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
                text = "←",
                modifier = Modifier.clickable { onBack() },
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = t(cause.title),
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

            Spacer(modifier = Modifier.height(16.dp))

            // ───── About This ─────
            SectionTitle(t("About this"))

            cause.detail.aboutThis.forEach {
                Bullet(it)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ───── How Common ─────
            SectionTitle(t("How common this is?"))

            Text(
                text = t(cause.detail.commonDescription),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ───── What You Can Do ─────
            SectionTitle(t("What you can do"))

            cause.detail.whatYouCanDoNow.forEach {
                Bullet(it)
            }

            // ───── Warning (Optional) ─────
            cause.detail.warning?.let {

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = "⚠",
                        color = Color.Red,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = t(it),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
