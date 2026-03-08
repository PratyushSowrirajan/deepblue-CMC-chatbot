package com.example.healthassistant.presentation.history


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

@Composable
fun CauseDetailScreen(
    title: String,
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )

        // ───── Content ─────
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // Illustration placeholders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.large
                        )
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.large
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ───── About this ─────
            SectionTitle("About this")

            Bullet("Often linked to stress, tiredness, or long working hours.")
            Bullet("Pain may feel like pressure or tightness around the head.")
            Bullet("Can affect both sides of the head or the neck area.")
            Bullet("Usually not caused by a serious health problem.")

            Spacer(modifier = Modifier.height(24.dp))

            // ───── How common ─────
            SectionTitle("How common this is ?")

            Text(
                text = "6 out of 10 people with similar symptoms had this problem",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(6) {
                    PersonDot(active = true)
                }
                repeat(4) {
                    PersonDot(active = false)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ───── What you can do now ─────
            SectionTitle("What you can do now")

            Bullet("Take rest and avoid over-exertion.")
            Bullet("Drink enough water during the day.")
            Bullet("Relax in a quiet and comfortable place.")
            Bullet("Reduce screen use for some time.")

            Spacer(modifier = Modifier.height(32.dp))

            // Warning
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠",
                    color = Color.Red,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Seek medical help if pain is severe",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PersonDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(
                if (active)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.small
            )
    )
}
