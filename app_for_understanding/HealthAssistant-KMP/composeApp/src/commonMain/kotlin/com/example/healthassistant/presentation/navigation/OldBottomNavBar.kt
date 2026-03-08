//package com.example.healthassistant.presentation.navigation
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//
//@Composable
//fun BottomNavBar(
//    selected: AppScreen,
//    onHomeClick: () -> Unit,
//    onHistoryClick: () -> Unit,
//    onNewsClick: () -> Unit
//) {
//    Column {
//
//        // Indicator line
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(2.dp)
//        ) {
//            Indicator(selected is AppScreen.Home)
//            Indicator(selected is AppScreen.History)
//            Indicator(selected is AppScreen.News)
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            horizontalArrangement = Arrangement.SpaceAround,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            BottomNavItem("Home", selected is AppScreen.Home, onHomeClick)
//            BottomNavItem("History", selected is AppScreen.History, onHistoryClick)
//            BottomNavItem("News", selected is AppScreen.News, onNewsClick)
//        }
//    }
//}
//
//@Composable
//fun RowScope.Indicator(active: Boolean) {
//    Box(
//        modifier = Modifier
//            .weight(1f)
//            .fillMaxHeight()
//            .background(
//                if (active)
//                    MaterialTheme.colorScheme.primary
//                else
//                    MaterialTheme.colorScheme.background
//            )
//    )
//}
//
//@Composable
//private fun BottomNavItem(
//    label: String,
//    selected: Boolean,
//    onClick: () -> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .padding(8.dp)
//            .clickable { onClick() }
//    ) {
//        Box(
//            modifier = Modifier
//                .size(22.dp)
//                .background(
//                    MaterialTheme.colorScheme.onBackground.copy(
//                        alpha = if (selected) 1f else 0.4f
//                    ),
//                    CircleShape
//                )
//        )
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        Text(
//            text = label,
//            color = MaterialTheme.colorScheme.onBackground.copy(
//                alpha = if (selected) 1f else 0.4f
//            ),
//            style = MaterialTheme.typography.bodySmall
//        )
//    }
//}
