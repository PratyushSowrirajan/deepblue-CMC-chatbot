package com.example.healthassistant.presentation.assessment

sealed class AssessmentEvent {

    object MyselfSelected : AssessmentEvent()
    object SomeoneElseSelected : AssessmentEvent()
    data class OptionSelected(
        val optionId: String
    ) : AssessmentEvent()

    data class TextChanged(val text: String) : AssessmentEvent()

    object SendText : AssessmentEvent()
    object SendImage : AssessmentEvent()
    object MicClicked : AssessmentEvent()
    object StopListening : AssessmentEvent()

    data class SpeechRecognized(val text: String) : AssessmentEvent()
    object VolumeClicked : AssessmentEvent()


    object ExitClicked : AssessmentEvent()

    object OpenVisualMode : AssessmentEvent()
    object CloseVisualMode : AssessmentEvent()
    data class BodyPartSelected(val partId: String) : AssessmentEvent()
    object VisualBackPressed : AssessmentEvent()
    data class VisualSymptomSelected(
        val bodyPath: List<String>,
        val symptomId: String,
        val symptomLabel: String
    ) : AssessmentEvent()

}
