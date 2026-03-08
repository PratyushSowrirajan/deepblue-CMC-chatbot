package com.example.healthassistant.presentation.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthassistant.designsystem.AppColors
import com.example.healthassistant.designsystem.AppShapes
import com.example.healthassistant.designsystem.AppTypography
import com.example.healthassistant.core.utils.shareNews
import com.example.healthassistant.core.utils.t
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.ic_settings
import healthassistant.composeapp.generated.resources.ic_share
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsItem(
    description: String,
    postedTime: String,
    imageUrl: String?,
    sourceName: String
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(AppColors.surface)
    ) {

        Spacer(modifier = Modifier.height(12.dp))
        
        // 🔹 News Image
        NewsImage(imageUrl)

        // 🔹 Content Section
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            // Description
            Text(
                text = t(description),
                style = AppTypography.poppinsRegular().copy(
                    fontSize = 12.sp
                ),
                color = AppColors.textPrimary,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (expanded) t("See less") else t("See more"),
                style = AppTypography.poppinsMedium().copy(
                    fontSize = 11.sp
                ),
                color = AppColors.dustyGray,
                modifier = Modifier.clickable {
                    expanded = !expanded
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = t(sourceName),
                        style = AppTypography.poppinsMedium().copy(
                            fontSize = 10.sp
                        ),
                        color = AppColors.blue
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = t(postedTime),
                        style = AppTypography.poppinsRegular().copy(
                            fontSize = 10.sp
                        ),
                        color = AppColors.dustyGray
                    )
                }

                // 🔹 Share Icon
                Icon(
                    painter = painterResource(Res.drawable.ic_share),
                    contentDescription = t("Share"),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            shareNews(
                                imageUrl = imageUrl,
                                appName = "Health Assistant",
                                description = description,
                                source = sourceName
                            )
                        }
                )
            }
        }
    }
}
