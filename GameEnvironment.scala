import drawings.Circle
import drawings.AnimatingChild
import drawings.AnimatingPanel
import drawings.Frame
import drawings.Rectangle
import drawings.JavaAction

import scala.collection.mutable.Map
import scala.language.implicitConversions
import scala.runtime._

import java.awt.Color
import java.awt.event._

class GameEnvironment extends KeyListener {
	val frame = new drawings.Frame()
	var Shape_bindings = Map[Symbol, AnimatingChild]()
	var environment_bindings = Map[Symbol, AnimatingPanel]()
	var action_bindings = Map[Symbol, JavaAction]()
	var key_bindings = Map[Int, Map[AnimatingChild, JavaAction]]()

	frame.addKeyListener(this)

	object Create {
		def environment(s: Symbol): Environment = {
			val ap = new AnimatingPanel(frame)
			frame.setPanel(ap)
			environment_bindings += (s -> ap)
			Environment(s)
		}

		def action(s: Symbol): Action = {
			val a = new JavaAction(s.name);
			action_bindings += (s -> a)
			Action(s)
		}

		def circle(s: Symbol): Shape = {
			val c = new Circle()
			Shape_bindings += (s -> c)
			Shape(s)
		}

		def rectangle(s: Symbol): Shape = {
			val r = new Rectangle()
			Shape_bindings += (s -> r)
			Shape(s)
		}
	}

	case class Environment(s: Symbol) {
		def fetch(): AnimatingPanel = environment_bindings.get(s).get

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

		def onKeyPress(key: Int, action: Action, child: Shape): Environment = {
			if (!key_bindings.contains(key)) {
				var behavior = Map[AnimatingChild, JavaAction]()
				behavior += (child.fetch() -> action.fetch())
				key_bindings += (key -> behavior)
			} else {
				key_bindings.get(key).get += (child.fetch() -> action.fetch())
			}
			this
		}

		override def toString(): String = "Environment " + s
	}

	case class Shape(s: Symbol) {
		var interaction_bindings = Map[Shape, Set[Shape => Unit]]()
		fetch().setCallback(callback)

		def fetch(): AnimatingChild = Shape_bindings.get(s).get

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

		def interaction(inter: (Shape, Shape => Unit)) = {
			val (s, b) = inter

			if (!interaction_bindings.contains(s)) {
				val behaviors = Set(b)
				interaction_bindings += (s -> behaviors)
			} else {
				val behaviors = interaction_bindings.get(s).get
				interaction_bindings += (s -> (behaviors + b))
			}
			this
		}

		def callback(other: AnimatingChild) = {
			println("AHOY")
			true
		}

		def mit(t: Article): Shape = this
		def und(t: Article): Shape = this
		override def toString(): String = fetch().toString()
	}

	case class Action(s: Symbol) {
		var isOnPress: Boolean = false
		def fetch(): JavaAction = action_bindings.get(s).get

		def color(c: Color): Action = {
			fetch().activateColor(c, isOnPress)
			this
		}

		def velocity(direction: Int, speed: Int): Action = {
			fetch().activateVelocity(direction, speed, isOnPress)
			this
		}

		def mit(t: ActionTiming): Action = {
			isOnPress = t == onPress;
			this
		}
		def und(t: ActionTiming): Action = {
			isOnPress = t == onPress;
			this
		}
		override def toString(): String = "Action " + s
	}

	def bounces(s: Shape) {
		s.fetch().setVelocity(0, 0)
	}
	def destroys(s: Shape) { }

	def start(s: Shape) {
		s.fetch().setActive(true)
	}

	def stop(s: Shape) {
		s.fetch().setActive(false)
	}

	class Article
	val a = new Article()
	val an = new Article()
	class ActionTiming
	val onPress = new ActionTiming()
	val onRelease = new ActionTiming()

	def about(s: Symbol) {
		if (Shape_bindings.contains(s))
			println(Shape(s))
		else if (environment_bindings.contains(s))
			println(Environment(s))
		else if (action_bindings.contains(s))
			println(Action(s))
		else
			println("Unbound variable")
	}

	def keyPressed(e: KeyEvent) {
		var key: Int = e.getKeyCode()
		if (key_bindings.contains(key)) {
			var childList = key_bindings.get(key).get
			childList.foreach {case(key, value) => value.performPress(key)}
		}
	}

	def keyReleased(e: KeyEvent) {
		var key: Int = e.getKeyCode()
		if (key_bindings.contains(key)) {
			var childList = key_bindings.get(key).get
			childList.foreach {case(key, value) => value.performRelease(key)}
		}
	}

	def keyTyped(e: KeyEvent) { }

	def ActionPerformed(a: ActionEvent) { }

	implicit def symbol2Shape(s: Symbol) = Shape(s)
	implicit def symbol2Environment(s: Symbol) = Environment(s)
	implicit def symbol2Action(s: Symbol) = Action(s)
}