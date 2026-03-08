//package com.example.healthassistant.presentation.assessment.visual
//
//data class VisualSymptom(
//    val id: String,
//    val label: String
//)
//
//data class BodyPart(
//    val id: String,
//    val label: String,
//    val subParts: List<BodyPart> = emptyList(),
//    val symptoms: List<VisualSymptom> = emptyList()
//)
//
//object BodyStructureProvider {
//
//    val bodyParts = listOf(
//        BodyPart(
//            id = "head",
//            label = "Head",
//            subParts = listOf(
//                BodyPart(
//                    id = "eyes",
//                    label = "Eyes",
//                    symptoms = listOf(
//                        VisualSymptom("eye_pain", "Eye pain"),
//                        VisualSymptom("blurred_vision", "Blurred vision")
//                    )
//                ),
//                BodyPart(
//                    id = "nose",
//                    label = "Nose",
//                    symptoms = listOf(
//                        VisualSymptom("nose_pain", "Nose pain"),
//                        VisualSymptom("sinus_pressure", "Sinus pressure")
//                    )
//                )
//            )
//        ),
//        BodyPart(
//            id = "forearm",
//            label = "Forearm",
//            symptoms = listOf(
//                VisualSymptom("forearm_pain", "Pain in forearm"),
//                VisualSymptom("swelling_forearm", "Swelling of forearm")
//            )
//        )
//    )
//}