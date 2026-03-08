package com.example.healthassistant

import NewsApiImpl
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.network.AppConfig
import com.example.healthassistant.designsystem.HealthAssistantTheme
import com.example.healthassistant.presentation.navigation.AppScreen
import com.example.healthassistant.presentation.navigation.BottomNavBar
import com.example.healthassistant.presentation.assessment.AssessmentScreen
import com.example.healthassistant.presentation.history.*

import com.example.healthassistant.presentation.news.NewsScreen
import com.example.healthassistant.presentation.assessment.AssessmentViewModel
import com.example.healthassistant.data.remote.assessment.AssessmentApiImpl
import com.example.healthassistant.data.repository.AssessmentRepositoryImpl
import com.example.healthassistant.core.network.NetworkClient
import com.example.healthassistant.core.platform.PlatformBackHandler

import com.example.healthassistant.core.stt.SpeechToTextManager
import com.example.healthassistant.core.tts.TextToSpeechManager
import com.example.healthassistant.data.local.chat.ChatLocalDataSourceImpl
import com.example.healthassistant.data.local.profile.GeneralProfileLocalDataSourceImpl
import com.example.healthassistant.data.local.profile.MedicalProfileLocalDataSourceImpl
import com.example.healthassistant.data.local.profile.ProfileLocalDataSourceImpl
import com.example.healthassistant.data.local.report.ReportLocalDataSourceImpl
import com.example.healthassistant.data.remote.auth.AuthApiImpl
import com.example.healthassistant.data.remote.bootstrap.BootstrapApiImpl
import com.example.healthassistant.data.remote.chat.ChatApiImpl
import com.example.healthassistant.data.remote.profile.ProfileApiImpl
import com.example.healthassistant.data.repository.AuthRepositoryImpl
import com.example.healthassistant.data.repository.ChatRepositoryImpl
import com.example.healthassistant.data.repository.NewsRepositoryImpl
import com.example.healthassistant.data.repository.ProfileRepositoryImpl
import com.example.healthassistant.db.HealthDatabase
import com.example.healthassistant.presentation.assessment.AssessmentCauseDetailScreen
import com.example.healthassistant.presentation.assessment.AssessmentReportScreen
import com.example.healthassistant.presentation.auth.AuthViewModel
import com.example.healthassistant.presentation.auth.LoginScreen
import com.example.healthassistant.presentation.auth.OnboardingMedicalScreen
import com.example.healthassistant.presentation.auth.OnboardingMedicalViewModel
import com.example.healthassistant.presentation.auth.OnboardingProfileScreen
import com.example.healthassistant.presentation.auth.OnboardingProfileViewModel
import com.example.healthassistant.presentation.auth.SignupScreen
import com.example.healthassistant.presentation.chat.ChatScreen
import com.example.healthassistant.presentation.chat.ChatViewModel
import com.example.healthassistant.presentation.home.EmergencyAction
import com.example.healthassistant.presentation.home.HomeScreen
import com.example.healthassistant.presentation.home.HomeViewModel
import com.example.healthassistant.presentation.news.NewsViewModel
import com.example.healthassistant.presentation.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun App(
    speechToTextManager: SpeechToTextManager,
    ttsManager: TextToSpeechManager,
    database: HealthDatabase,
    newsApiKey: String,
    onEmergencyAction: (EmergencyAction, String?) -> Unit
) {

    HealthAssistantTheme {

        var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Login) }

        // ✅ API
        val api = remember {
            AssessmentApiImpl(
                client = NetworkClient.httpClient,
                baseUrl = AppConfig.BASE_URL
            )
        }

        val bootstrapApi = remember {
            BootstrapApiImpl(
                client = NetworkClient.httpClient,
                baseUrl = AppConfig.BASE_URL
            )
        }


        val profileLocal = remember {
            ProfileLocalDataSourceImpl(database)
        }

        val reportLocal = remember {
            ReportLocalDataSourceImpl(database)
        }

        val generalProfileLocal = remember {
            GeneralProfileLocalDataSourceImpl(database)
        }

        val medicalProfileLocal = remember {
            MedicalProfileLocalDataSourceImpl(database)
        }


        val repository = remember {
            AssessmentRepositoryImpl(
                api = api,
                bootstrapApi = bootstrapApi,
                profileLocal = profileLocal,
                reportLocal = reportLocal,
                generalProfileLocal = generalProfileLocal,
                medicalProfileLocal = medicalProfileLocal
            )
        }

        val chatApi = remember {
            ChatApiImpl(
                client = NetworkClient.httpClient,
                baseUrl = AppConfig.BASE_URL
            )
        }

        val chatLocal = remember {
            ChatLocalDataSourceImpl(database)
        }

        val chatRepository = remember {
            ChatRepositoryImpl(
                api = chatApi,
                local = chatLocal
            )
        }

        val chatViewModel = remember {
            ChatViewModel(
                repository = chatRepository,
                speechToTextManager = speechToTextManager,
                ttsManager = ttsManager
            )
        }

        val authApi = remember {
            AuthApiImpl(
                client = NetworkClient.httpClient,
                baseUrl = AppConfig.BASE_URL
            )
        }

        val authRepository = remember {
            AuthRepositoryImpl(authApi)
        }

        val authViewModel = remember {
            AuthViewModel(
                repository = authRepository,
                assessmentRepository = repository   // 🔥 inject here
            )
        }

        val profileApi = remember {
            ProfileApiImpl(
                client = NetworkClient.httpClient,
                baseUrl = AppConfig.BASE_URL
            )
        }

        val profileRepository = remember {
            ProfileRepositoryImpl(profileApi)
        }

        val token = authViewModel.state.value.token ?: ""
        val onboardingProfileViewModel = remember {
            OnboardingProfileViewModel(
                repository = profileRepository
            )
        }

        val onboardingMedicalViewModel = remember(token) {
            OnboardingMedicalViewModel(
                repository = profileRepository
            )
        }

        // ✅ ViewModel
        val assessmentViewModel = remember {
            AssessmentViewModel(
                repository = repository,
                speechToTextManager = speechToTextManager,
                ttsManager = ttsManager
            )
        }

        // News
        val newsApi = remember {
//            AppLogger.d("APP", "News API Key: $newsApiKey")
            NewsApiImpl(
                client = NetworkClient.httpClient,
                apiKey = newsApiKey
            )
        }

        val newsRepository = remember {
            NewsRepositoryImpl(newsApi)
        }

        val newsViewModel = remember {
            NewsViewModel(newsRepository)
        }

        PlatformBackHandler {

            currentScreen = when (currentScreen) {
                AppScreen.OnboardingProfile ->
                    AppScreen.Signup

                AppScreen.OnboardingMedical ->
                    AppScreen.OnboardingProfile

                is AppScreen.Chat -> currentScreen

                is AppScreen.AssessmentCauseDetail ->
                    AppScreen.AssessmentReport

                AppScreen.AssessmentReport ->
                    AppScreen.Assessment

                AppScreen.Assessment ->
                    AppScreen.Home

                is AppScreen.CauseDetail ->
                    AppScreen.HistoryDetail

                AppScreen.HistoryDetail ->
                    AppScreen.History

                AppScreen.History ->
                    AppScreen.Home

                AppScreen.News ->
                    AppScreen.Home

                AppScreen.Home ->
                    AppScreen.Home

                else ->
                    AppScreen.Home
            }
        }

        // Temporary hardcoded emergency contact (will replace later)
        val emergencyContactNumber = "6374102550"

        Column {
            Box(modifier = Modifier.weight(1f)) {
                when (currentScreen) {

                    AppScreen.Login -> LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToSignup = {
                            currentScreen = AppScreen.Signup
                            AppLogger.d("NAVIGATION", "Navigated to Signup")
                        },
                        onLoginSuccess = {
                            currentScreen = AppScreen.Home
                            AppLogger.d("NAVIGATION", "Login Success → Home")
                        }
                    )
                    AppScreen.OnboardingProfile -> OnboardingProfileScreen(
                        viewModel = onboardingProfileViewModel,
                        onProfileCompleted = {
                            currentScreen = AppScreen.OnboardingMedical
                            AppLogger.d("NAVIGATION", "Profile Saved → MedicalData")
                        }
                    )

                    AppScreen.OnboardingMedical -> OnboardingMedicalScreen(
                        viewModel = onboardingMedicalViewModel,
                        onMedicalCompleted = {
                            currentScreen = AppScreen.Home
                        }
                    )

                    AppScreen.Signup -> SignupScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = {
                            currentScreen = AppScreen.Login
                            AppLogger.d("NAVIGATION", "User Logged Out → Login Screen")
                        },
                        onSignupSuccess = {
                            currentScreen = AppScreen.OnboardingProfile
                            AppLogger.d("NAVIGATION", "Signup Success → OnboardingProfile")
                        }
                    )

                    AppScreen.Settings -> SettingsScreen(
                        onBack = {
                            currentScreen = AppScreen.Home
                        },
                        onProfileClick = {
                            AppLogger.d("SETTINGS", "Profile clicked")
                        },
                        onMedicalClick = {
                            AppLogger.d("SETTINGS", "Medical clicked")
                        },
                        onLanguageClick = {
                            // open language dialog later
                        },
                        onLogoutClick = {
                            currentScreen = AppScreen.Login
                        }
                    )



                    AppScreen.Home -> HomeScreen(
                        viewModel = HomeViewModel(
                            onStartAssessment = {
                                currentScreen = AppScreen.Assessment
                            },
                            onOpenChat = {
                                currentScreen = AppScreen.Chat(reportId = null)
                            },
                            onOpenSettings = {
                                currentScreen = AppScreen.Settings
                            }
                        ),
                        onEmergencyAction = { action ->
                            AppLogger.d("EMERGENCY", "App.kt received action: $action")
                            AppLogger.d("EMERGENCY", "Using phone number: $emergencyContactNumber")
                            onEmergencyAction(action, emergencyContactNumber)
                        }
                    )


                    AppScreen.Assessment -> AssessmentScreen(
                        viewModel = assessmentViewModel,
                        onExit = { currentScreen = AppScreen.Home },
                        onReportGenerated = {
                            currentScreen = AppScreen.AssessmentReport
                        }
                    )

                    AppScreen.AssessmentReport -> {

                        val state = assessmentViewModel.state.collectAsState().value
                        val report = state.report

                        report?.let {
                            AssessmentReportScreen(
                                report = it,
                                onBack = { currentScreen = AppScreen.Home },
                                onCauseClick = { cause ->
                                    currentScreen = AppScreen.AssessmentCauseDetail(cause)
                                },
                                onGoHome = {
                                    assessmentViewModel.endAssessment {
                                        currentScreen = AppScreen.Home
                                    }
                                },
                                onAskChatbot = {
                                    assessmentViewModel.endAssessment {
                                        currentScreen = AppScreen.Chat(reportId = report.reportId)
                                    }
                                }
                            )


                        }
                    }


                    is AppScreen.Chat -> ChatScreen(
                        viewModel = chatViewModel,
                        currentReportId = (currentScreen as AppScreen.Chat).reportId,
                        onBack = {
                            currentScreen = AppScreen.Home
                        }
                    )



                    is AppScreen.AssessmentCauseDetail ->
                        AssessmentCauseDetailScreen(
                            cause = (currentScreen as AppScreen.AssessmentCauseDetail).cause,
                            onBack = { currentScreen = AppScreen.AssessmentReport }
                        )



                    AppScreen.History -> HistoryScreen(
                        onItemClick = {
                            currentScreen = AppScreen.HistoryDetail
                        }
                    )

                    AppScreen.News -> NewsScreen(
                        viewModel = newsViewModel
                    )


                    AppScreen.HistoryDetail -> HistoryDetailScreen(
                        onBack = { currentScreen = AppScreen.History },
                        onCauseClick = { cause ->
                            currentScreen = AppScreen.CauseDetail(cause)
                        }
                    )

                    is AppScreen.CauseDetail -> CauseDetailScreen(
                        title = (currentScreen as AppScreen.CauseDetail).title,
                        onBack = { currentScreen = AppScreen.HistoryDetail }
                    )
                }
            }

            if (
                currentScreen != AppScreen.Assessment &&
                currentScreen !is AppScreen.Chat &&
                currentScreen != AppScreen.Login &&
                currentScreen != AppScreen.Signup &&
                currentScreen != AppScreen.OnboardingProfile &&
                currentScreen != AppScreen.OnboardingMedical &&
                currentScreen != AppScreen.Settings
            ) {
                BottomNavBar(
                    selected = currentScreen,
                    onHomeClick = { currentScreen = AppScreen.Home },
                    onHistoryClick = { currentScreen = AppScreen.History },
                    onNewsClick = { currentScreen = AppScreen.News }
                )
            }
        }
    }
}
