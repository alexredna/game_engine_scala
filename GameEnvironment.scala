import drawings.Circle
import drawings.AnimatingChild
import drawings.AnimatingPanel
import drawings.Frame
import drawings.Rectangle

import scala.collection.mutable.Map
import scala.language.implicitConversions

import java.awt.Color

class GameEnvironment {

	val frame = new drawings.Frame()
	var bindings = Map[Symbol, AnimatingChild]()
	var enviros = Map[Symbol, AnimatingPanel]()

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

		override def toString(): String = {
			"Environment " + s
		}
	}

	case class Shape(s: Symbol) {
		var interactions = Map[Shape, Int]()
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

		def velocity(direction: Int, speed: Int) = {
			if (direction < 0 || direction > 360)
				sys.error("Invalid direction for velocity")
			fetch().setVelocity(direction, speed)
			this
		}
/*
		def interaction(inter: (Shape, Int)) = {
			var (other, action) = inter
			var ac: AnimatingChild = fetch()
			switch(action) {
				case GameCons.bounces:
					
					
				case GameCons.destroys:
			}
			this
		}
*/
		def mit(t: Article): Shape = {
			this
		}
		def und(t: Article): Shape = {
			this
		}

		override def toString(): String = fetch().toString()
	}

	def start(s: Shape) {
		s.fetch().setActive(true)
	}

	def stop(s: Shape) {
		s.fetch().setActive(false)
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

	class Article
	val a = new Article()
	val an = new Article()

	def about(s: Symbol) {
		if (bindings.contains(s))
			println(Shape(s))
		else if (enviros.contains(s))
			println(Environment(s))
		else
			println("Unbound variable")
	}

	implicit def symbol2Shape(s: Symbol) = Shape(s)
	implicit def symbol2Environment(s: Symbol) = Environment(s)
}