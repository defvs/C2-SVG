import com.github.nwillc.ksvg.elements.Container

data class CytusChart(
	val pages: List<CytusPage> = listOf(),
)

fun JsonCytusChart.toCytusChart(): CytusChart {
	return CytusChart(this.pages.mapIndexed { index, page ->
		CytusPage(
			page.startTick,
			page.endTick,
			page.scanlineDirection,
			this.notes.filter { it.page == index }.map { CytusNote.fromJsonNote(it) },
		)
	})
}

data class CytusPage(
	val startTick: Int,
	val endTick: Int,
	val direction: ScanlineDirection,
	val notes: List<CytusNote>,
) {
	constructor(
		startTick: Int,
		endTick: Int,
		direction: Int,
		notes: List<CytusNote>,
	): this(startTick, endTick, ScanlineDirection.fromInt(direction), notes)
	
	enum class ScanlineDirection(val value: Int) {
		Up(1),
		Down(-1),
		Stop(0),
		;
		
		companion object {
			fun fromInt(value: Int) = values().find { it.value == value } ?: Stop
		}
	}
}

data class CytusNote(
	val id: Int,
	val type: NoteType,
	val tick: Int,
	val x: Float,
	val holdLength: Int?,
	val nextId: Int?,
	val isForward: Boolean = false,
) {
	constructor(
		id: Int,
		type: Int,
		tick: Int,
		x: Float,
		holdLength: Int?,
		nextId: Int?,
		isForward: Boolean,
	): this(id, NoteType.fromInt(type), tick, x, holdLength, nextId, isForward)
	
	@Suppress("unused")
	enum class NoteType(val svg: (Container.(x: Float, y: Float, direction: CytusPage.ScanlineDirection) -> Unit) = { _, _, _ -> }) {
		Tap({ x, y, direction ->
			g {
				circle {
					fill = "#AFC6CE"
					stroke = "#171822"
					r = "30"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = if (direction == CytusPage.ScanlineDirection.Up) "#1D857A" else "#1D768F"
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#99FFE5" else "#99FFFF"
					r = "20"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#CCFFF2" else "#CCFFFF"
					r = "10"
					cx = x.toString()
					cy = y.toString()
				}
			}
		}),
		Hold({ x, y, direction ->
			g {
				circle {
					fill = "#FFFFFF"
					stroke = "#171822"
					r = "30"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = if (direction == CytusPage.ScanlineDirection.Up) "#C669A1" else "#C6697B"
					fill = "#FFFFFF"
					r = "20"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
			}
		}),
		LongHold({ x, y, direction ->
			g {
				circle {
					fill = "#FFFFFF"
					stroke = "#171822"
					r = "30"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					fill = "#FFCC66"
					r = "25"
					cx = x.toString()
					cy = y.toString()
				}
				rect {
					fill = "#FFFFFF"
					this.y = (y - 25).toString()
					this.x = (x - 8).toString()
					width = "16"
					height = "50"
				}
			}
		}),
		DragHead({ x, y, direction ->
			g {
				circle {
					fill = "#AFC6CE"
					stroke = "#171822"
					r = "26"
					strokeWidth = "4"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = "#171822"
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#AA66FF" else "#F666FF"
					r = "18"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
			}
		}),
		DragChild({ x, y, direction ->
			g {
				circle {
					fill = "#AFC6CE"
					stroke = "#171822"
					r = "10"
					strokeWidth = "2"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = "#171822"
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#AA66FF" else "#F666FF"
					r = "5"
					strokeWidth = "2"
					cx = x.toString()
					cy = y.toString()
				}
			}
		}),
		Flick({x, y, direction ->
			circle {
				fill = "#27f714"
				stroke = "#14660c"
				r = "30"
				strokeWidth = "5"
				cx = x.toString()
				cy = y.toString()
			}
		}),
		ClickDragHead({ x, y, direction ->
			g {
				circle {
					fill = "#AFC6CE"
					stroke = "#171822"
					r = "30"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = if (direction == CytusPage.ScanlineDirection.Up) "#1D857A" else "#1D768F"
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#99FFE5" else "#99FFFF"
					r = "20"
					strokeWidth = "5"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = "transparent"
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#CCFFF2" else "#CCFFFF"
					r = "10"
					cx = x.toString()
					cy = y.toString()
				}
			}
		}),
		ClickDragChild({ x, y, direction ->
			g {
				circle {
					fill = "#AFC6CE"
					stroke = "#171822"
					r = "10"
					strokeWidth = "2"
					cx = x.toString()
					cy = y.toString()
				}
				circle {
					stroke = "#171822"
					fill = if (direction == CytusPage.ScanlineDirection.Up) "#99FFE5" else "#99FFFF"
					r = "5"
					strokeWidth = "2"
					cx = x.toString()
					cy = y.toString()
				}
			}
		}),
		;
		
		companion object {
			fun fromInt(value: Int) = values()[value]
		}
	}
	
	companion object {
		fun fromJsonNote(note: JsonCytusChart.Note) =
			CytusNote(note.id, note.type, note.tick, note.x, note.holdTick, note.nextId, note.isForward)
	}
}
