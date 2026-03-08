package com.example.healthassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthassistant.core.database.DatabaseDriverFactory
import com.example.healthassistant.db.HealthDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            // 1️⃣ Create database driver
            val driverFactory = DatabaseDriverFactory(this)
            val driver = driverFactory.createDriver()

            // 2️⃣ Create database
            val database = HealthDatabase(driver)

            // 3️⃣ Call AndroidApp and PASS database
            AndroidApp(database = database)
        }
    }
}
//@Preview(
//    showBackground = true,
//    device = "spec:width=411dp,height=891dp"
//)
//@Composable
//fun AppPreview() {
//    AndroidApp()
//}
