import CytusNote.NoteType.*
import com.github.nwillc.ksvg.RenderMode
import com.github.nwillc.ksvg.elements.Container
import com.github.nwillc.ksvg.elements.SVG
import java.io.File
import java.lang.StringBuilder
import kotlin.math.roundToInt
import kotlin.system.exitProcess

fun main(args: Array<String>) {
	val path = args.getOrElse(0) {
		println("Please provide chart json path as argument.")
		exitProcess(2)
	}
	
	val outputPath = args.getOrElse(1) {
		println("Output path not given. Will output to ./output/")
		"./output/"
	}
	
	val outputDir = File(outputPath).apply {
		mkdir()
	}
	
	val chart = JsonCytusChart.parseChartFromFile(path).toCytusChart()
	val allNotes = chart.pages.flatMap { it.notes }
	
	chart.pages.forEachNeighboured { index, _, current, next ->
		val svg = SVG.svg {
			height = "400"
			width = "550"
			
			val paddingX = 25
			val paddingY = 50
			
			rect {
				x = paddingX.toString()
				y = paddingY.toString()
				width = "500"
				height = "300"
				fill = "black"
				stroke = "white"
				strokeWidth = "4"
			}
			
			fun calculateY(note: CytusNote) =
				((300 * (note.tick - current.startTick).toFloat() / (current.endTick - current.startTick))).let {
					if (current.direction == CytusPage.ScanlineDirection.Up) 300 - it else it
				} + paddingY
			
			fun calculateX(note: CytusNote) = note.x * 500 + paddingX
			
			val dashes = g {}
			val heads = g {}
			
			current.notes.forEach { note ->
				val y = calculateY(note)
				val x = calculateX(note)
				
				when (note.type) {
					DragHead, DragChild, ClickDragHead, ClickDragChild -> {
						if (listOf(-1, 0).contains(note.nextId).not()) {
							dashes.line {
								x1 = x.toString()
								y1 = y.toString()
								
								val nextNote = allNotes.find { it.id == note.nextId }!!
								x2 = calculateX(nextNote).toString()
								y2 = calculateY(nextNote).toString()
								
								strokeWidth = "5"
								stroke = "#FFFFFFB4"
								strokeDashArray = "3,3"
							}
						}
					}
					
					Hold -> {
					
					}
					
					LongHold -> {
					
					}
					
					else -> {
					}
				}
				heads.note(x, y, note.type, current.direction)
				
			}
		}
		
		val svgString = StringBuilder().apply { svg.render(this, RenderMode.FILE) }.toString()
		outputDir.resolve("page${index}.svg").writeText(svgString)
	}
}

fun Container.note(
	x: Float, y: Float,
	type: CytusNote.NoteType,
	direction: CytusPage.ScanlineDirection,
) = type.svg(this, x, y, direction)

inline fun <T> List<T>.forEachNeighboured(action: (index: Int, previous: T?, current: T, next: T?) -> Unit) {
	forEachIndexed { index, t -> action(index, this.getOrNull(index - 1), t, this.getOrNull(index + 1)) }
}