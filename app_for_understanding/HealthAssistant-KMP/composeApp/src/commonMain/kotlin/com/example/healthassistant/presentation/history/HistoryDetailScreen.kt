    package com.example.healthassistant.presentation.history

    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import com.example.healthassistant.core.utils.t


    @Composable
    fun HistoryDetailScreen(
        onBack: () -> Unit,
        onCauseClick: (String) -> Unit
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
                    modifier = Modifier
                        .clickable { onBack() },
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Head Pain",
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

                // Meta info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "27 December 2025\nGowtham A, Male, 20 years old",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "11:55 AM",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ───── Summary ─────
                SectionTitle("Summary")

                Bullet("You reported pain in the head area.")
                Bullet("The pain started recently.")
                Bullet("The pain intensity was moderate.")
                Bullet("You did not report fever, abdominal, vomiting, or vision problems.")

                Spacer(modifier = Modifier.height(20.dp))

                // ───── Possible Causes ─────
                SectionTitle("Possible Causes")

                CauseItem(
                    title = "1. Tension headache",
                    description = "This type of head pain is often linked to stress, tiredness, or long periods of work.",
                    onTellMore = { onCauseClick("Tension headache") }
                )

                CauseItem(
                    title = "2. Dehydration",
                    description = "Not drinking enough water can lead to head pain, especially in hot weather.",
                    onTellMore = { onCauseClick("Dehydration") }
                )

                CauseItem(
                    title = "3. Lack of sleep",
                    description = "Poor or irregular sleep can trigger headaches and a feeling of heaviness in the head.",
                    onTellMore = { onCauseClick("Lack of sleep") }
                )


                Spacer(modifier = Modifier.height(20.dp))

                // ───── Advice ─────
                SectionTitle("What you were advised")

                Bullet("Sleep for minimum of 8 hours.")
                Bullet("Increase fluid intake.")
                Bullet("Take frequent breaks to avoid tension.")
                Bullet("Recommended to consult a doctor within the next 2–3 days.")

                Spacer(modifier = Modifier.height(32.dp))

                // ───── Download Button ─────
                Button(
                    onClick = { /* later */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("Download Health Report")
                }
            }
        }
    }


    @Composable
    fun SectionTitle(text: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    @Composable
    fun Bullet(text: String) {
        Row(
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text("• ")
            Text(
                text = t(text),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    @Composable
    fun CauseItem(
        title: String,
        description: String,
        onTellMore: () -> Unit
    ) {
        Text(
            text = t(title),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

//        Text(
//            text = "Can usually be managed at home",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.primary
//        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = t(description),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onTellMore,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(t("Tell me more"), style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
