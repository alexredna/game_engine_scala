import drawings.Circle
import drawings.AnimatingChild
import drawings.AnimatingPanel
import drawings.Frame
import drawings.Rectangle

import scala.collection.mutable.Map
import scala.language.implicitConversions

import java.awt.Color;

class GameEnvironment {

	val frame = new drawings.Frame()
	var bindings = Map[Symbol, AnimatingChild]()
	var enviros = Map[Symbol, AnimatingPanel]()

	val red = Color.RED
	val blue = Color.BLUE
	val green = Color.GREEN
	val black = Color.BLACK
	val cyan = Color.CYAN
	val gray = Color.GRAY
	val magenta = Color.MAGENTA
	val orange = Color.ORANGE
	val pink = Color.PINK
	val yellow = Color.YELLOW
	val white = Color.WHITE
	val burnt_orange = new Color(191, 87, 0)

	case class Environment(s: Symbol) {
		def fetch(): AnimatingPanel = {
			enviros.get(s).get
		}

		var lastAdded: Shape = null

		def add(s: Shape): Environment = {
			fetch().addChild(s.fetch())
			lastAdded = s
			this
		}
		def at(pos: (Int, Int)) {
			if (lastAdded == null)
				sys.error("BAD")
			lastAdded.location(pos._1, pos._2)
			lastAdded = null
		}
	}

	case class Shape(s: Symbol) {
		def fetch(): AnimatingChild = {
			bindings.get(s).get
		}

		def location(x: Int, y: Int): Shape = {
			fetch().setLocation(x, y)
			this
		}

		def color(c: Color): Shape = {
			fetch().setColor(c)
			this
		}
	}


	object Create {
		def environment(s: Symbol): Environment = {
			val ap = new AnimatingPanel(frame)
			frame.setPanel(ap)
			enviros += (s -> ap)
			Environment(s)
		}

		def circle(s: Symbol): Shape = {
			val c = new Circle()
			bindings += (s -> c)
			Shape(s)
		}

		def rectangle(s: Symbol): Shape = {
			val r = new Rectangle()
			bindings += (s -> r)
			Shape(s)
		}
	}


	implicit def symbol2Shape(s: Symbol) = Shape(s)
	implicit def symbol2Environment(s: Symbol) = Environment(s)
}