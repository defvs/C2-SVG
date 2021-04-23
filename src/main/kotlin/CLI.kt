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
	
	chart.pages.forEachNeighboured { index, previous, current, next ->
		val svg = SVG.svg {
			height = "450"
			width = "550"
			
			rect {
				x = "25"
				y = "125"
				width = "500"
				height = "300"
				fill = "black"
				stroke = "white"
				strokeWidth = "4"
			}
			
			current.notes.forEach {
				val relativeTick = it.tick - current.startTick
				val floatTick = relativeTick.toFloat() / (current.endTick - current.startTick)
				var y = (300 * floatTick).roundToInt()
				if (current.direction == CytusPage.ScanlineDirection.Up) y = 300 - y
				val x = (it.x * 500).roundToInt()
				
				note(x = x + 25, y = y + 125, it.type, current.direction)
			}
		}
		
		val svgString = StringBuilder().apply { svg.render(this, RenderMode.FILE) }.toString()
		outputDir.resolve("page${index}.svg").writeText(svgString)
	}
}

fun Container.note(
	x: Int, y: Int,
	type: CytusNote.NoteType,
	direction: CytusPage.ScanlineDirection,
) = type.svg(this, x, y, direction)

inline fun <T> List<T>.forEachNeighboured(action: (index: Int, previous: T?, current: T, next: T?) -> Unit) {
	forEachIndexed { index, t -> action(index, this.getOrNull(index - 1), t, this.getOrNull(index + 1)) }
}