import com.example.healthassistant.data.remote.assessment.dto.ReportResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatReportWrapperDto(
    val report_id: String,
    val generated_at: String,
    val is_main: Boolean,
    val report_data: ReportResponseDto
)