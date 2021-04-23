import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.SerialName as Name

@Serializable
data class JsonCytusChart(
	@Name("format_version") val formatVersion: Int,
	@Name("time_base") val timeBase: Int,
	@Name("start_offset_time") val startOffset: Float,
	@Name("page_list") val pages: List<Page>,
	@Name("tempo_list") val tempos: List<Tempo>,
	@Name("note_list") val notes: List<Note>,
	@Name("event_order_list") val events: List<Events>,
) {
	companion object {
		fun parseChart(jsonString: String) = Json {
			ignoreUnknownKeys = true
			isLenient = true
		}.decodeFromString<JsonCytusChart>(jsonString)
		
		fun parseChartFromFile(path: String) = parseChart(File(path).readText())
	}
	
	@Serializable
	data class Page(
		@Name("start_tick") val startTick: Int,
		@Name("end_tick") val endTick: Int,
		@Name("scan_line_direction") val scanlineDirection: Int,
	)
	
	@Serializable
	data class Tempo(
		val tick: Int,
		val value: Int,
	) {
		val bpm
			get() = 60000000f / value
	}
	
	@Serializable
	data class Note(
		@Name("page_index") val page: Int,
		val type: Int,
		val id: Int,
		val tick: Int,
		val x: Float,
		@Name("has_sibling") val hasSibling: Boolean,
		@Name("hold_tick") val holdTick: Int,
		@Name("next_id") val nextId: Int,
		@Name("is_forward") val isForward: Boolean,
	)
	
	@Serializable
	data class Events(
		val tick: Int,
		@Name("event_list") val eventList: List<EventType>,
	) {
		@Serializable
		data class EventType(
			val type: Int,
			val args: String,
		)
	}
}

