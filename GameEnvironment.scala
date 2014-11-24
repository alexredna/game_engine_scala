import drawings.Circle
import drawings.AnimatingChild
import drawings.AnimatingPanel
import drawings.Frame
import drawings.Rectangle
import drawings.JavaAction
import drawings.SplitPanel

import scala.collection.mutable.Map
import scala.language.implicitConversions
import scala.runtime._

import java.awt.event._
import java.awt._
import java.beans._
import javax.swing._

class GameEnvironment extends KeyListener with PropertyChangeListener {
	val frame = new drawings.Frame()
	frame.addKeyListener(this)
	frame.addPropertyChangeListener(this)

	var shape_bindings = Map[Symbol, AnimatingChild]()
	var animating_child_to_symbol = Map[AnimatingChild, Symbol]()
	var environment_bindings = Map[Symbol, AnimatingPanel]()
	var action_bindings = Map[Symbol, JavaAction]()
	var key_bindings = Map[Int, Map[AnimatingChild, JavaAction]]()
	var interaction_bindings = Map[Shape, Map[Shape, Set[(Shape, Shape) => Unit]]]()

	var panel_bindings = Map[Symbol, SplitPanel]()
	var button_bindings = Map[Symbol, JButton]()
	var label_bindings = Map[Symbol, JLabel]()

	def isBound(s: Symbol): Boolean =
		shape_bindings.contains(s) || environment_bindings.contains(s) ||
		action_bindings.contains(s) || panel_bindings.contains(s) ||
		button_bindings.contains(s) || label_bindings.contains(s)

	object Create {
		def environment(s: Symbol): Environment = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val ap = new AnimatingPanel(frame)
			environment_bindings += (s -> ap)
			Environment(s)
		}

		def action(s: Symbol): Action = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val a = new JavaAction(s.name);
			action_bindings += (s -> a)
			Action(s)
		}

		def circle(s: Symbol): Shape = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val c = new Circle()
			shape_bindings += (s -> c)
			animating_child_to_symbol += (c -> s)
			Shape(s)
		}

		def rectangle(s: Symbol): Shape = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val r = new Rectangle()
			shape_bindings += (s -> r)
			animating_child_to_symbol += (r -> s)
			Shape(s)
		}

		def hPanel(s: Symbol, n: Int): ScalaPanel = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val p = new SplitPanel(n, true)
			panel_bindings += (s -> p)
			ScalaPanel(s)
		}

		def vPanel(s: Symbol, n: Int): ScalaPanel = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val p = new SplitPanel(n, false)
			panel_bindings += (s -> p)
			ScalaPanel(s)
		}

		def button(s: Symbol): ScalaButton = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val b = new JButton()
			b.setFocusable(false)
			button_bindings += (s -> b)
			ScalaButton(s)
		}

		def label(s: Symbol): ScalaLabel = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val b = new JLabel()
			label_bindings += (s -> b)
			ScalaLabel(s)
		}
	}

	case class Environment(s: Symbol) {
		def fetch(): AnimatingPanel = environment_bindings.get(s).get

		var lastAdded: Shape = null

		def addShape(s: Shape): Environment = {
			fetch().addChild(s.fetch())
			lastAdded = s
			this
		}

		def at(pos: (Int, Int)): Environment = {
			if (lastAdded == null)
				sys.error("BAD")
			lastAdded.location(pos._1, pos._2)
			lastAdded = null
			this
		}

		def size(width: Int, height: Int): Environment = {
			fetch().setPreferredSize(new Dimension(width, height))
			this
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

		def mit(t: Article): Environment = this
		def und(t: Article): Environment = this
		override def toString(): String = "Environment " + s
	}

	case class Shape(s: Symbol) {
		def fetch(): AnimatingChild = shape_bindings.get(s).get

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

		def interaction(s: Shape, func: (Shape, Shape) => Unit) = {
			if (!interaction_bindings.contains(this))
				interaction_bindings += (this -> Map[Shape, Set[(Shape, Shape) => Unit]]())

			val my_bindings = interaction_bindings.get(this).get

			if (!my_bindings.contains(s)) {
				val behaviors = Set(func)
				my_bindings += (s -> behaviors)
			} else {
				val behaviors = my_bindings.get(s).get
				my_bindings += (s -> (behaviors + func))
			}
			interaction_bindings += (this -> my_bindings)
			this
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

	object ScalaFrame {
		var tooLateToSplit: Boolean = false
		def hsplit(n: Int) = {
			if (tooLateToSplit)
				sys.error("Already called split on frame")
			tooLateToSplit = true
			frame.setContentPane(new SplitPanel(n, true));
			this
		}

		def vsplit(n: Int) = {
			if (tooLateToSplit)
				sys.error("Already called split on frame")
			tooLateToSplit = true
			frame.setContentPane(new SplitPanel(n, false));
			this
		}

		def color(color: Color) = {
			frame.getContentPane().setBackground(color)
			this
		}

		def update(index: Int, comp: ScalaComponent) {
			if (!tooLateToSplit) {
				frame.setContentPane(new SplitPanel(1, true));
				tooLateToSplit = true
			}
			frame.getContentPane().asInstanceOf[SplitPanel].setChild(index, comp.fetch())
		}

		def update(index: Int, compSymbol: Symbol) {
			if (!tooLateToSplit) {
				frame.setContentPane(new SplitPanel(1, true));
				tooLateToSplit = true
			}
			if (!isBound(compSymbol))
				sys.error("Cannot find " + compSymbol)
			var comp: JComponent = null
			if (environment_bindings.contains(compSymbol))
				comp = environment_bindings.get(compSymbol).get
			if (panel_bindings.contains(compSymbol))
				comp = panel_bindings.get(compSymbol).get
			if (button_bindings.contains(compSymbol))
				comp = button_bindings.get(compSymbol).get
			if (label_bindings.contains(compSymbol))
				comp = label_bindings.get(compSymbol).get
			if (comp == null)
				sys.error("Cannot find " + compSymbol)

			frame.getContentPane().asInstanceOf[SplitPanel].setChild(index, comp)
		}
	}

	abstract class ScalaComponent {
		def fetch(): JComponent
	}
	case class ScalaPanel(s: Symbol) extends ScalaComponent {
		override def fetch(): SplitPanel = panel_bindings.get(s).get

		def color(color: Color): ScalaPanel = {
			fetch().setBackground(color)
			this
		}

		def update(index: Int, comp: ScalaComponent) {
			fetch().setChild(index, comp.fetch())
		}

		def update(index: Int, compSymbol: Symbol) {
			if (!isBound(compSymbol))
				sys.error("Cannot find " + compSymbol)
			var comp: JComponent = null
			if (environment_bindings.contains(compSymbol))
				comp = environment_bindings.get(compSymbol).get
			if (panel_bindings.contains(compSymbol))
				comp = panel_bindings.get(compSymbol).get
			if (button_bindings.contains(compSymbol))
				comp = button_bindings.get(compSymbol).get
			if (label_bindings.contains(compSymbol))
				comp = label_bindings.get(compSymbol).get
			if (comp == null)
				sys.error("Cannot find " + compSymbol)

			fetch().setChild(index, comp)
		}

		def mit(t: Article): ScalaPanel = this
		def und(t: Article): ScalaPanel = this
		override def toString(): String = "ScalaPanel()"
	}

	case class ScalaButton(s: Symbol) extends ScalaComponent {
		override def fetch(): JButton = button_bindings.get(s).get

		def text(str: String): ScalaButton = {
			fetch().setText(str)
			this
		}

		def mit(t: Article): ScalaButton = this
		def und(t: Article): ScalaButton = this
		override def toString(): String = "ScalaButton(" + fetch().getText() + ")"
	}

	case class ScalaLabel(s: Symbol) extends ScalaComponent {
		override def fetch(): JLabel = label_bindings.get(s).get

		def text(str: String): ScalaLabel = {
			fetch().setText(str)
			this
		}

		def mit(t: Article): ScalaLabel = this
		def und(t: Article): ScalaLabel = this
		override def toString(): String = "ScalaLabel(" + fetch().getText() + ")"
	}

	def start(s: Shape) {
		s.fetch().setActive(true)
	}

	def stop(s: Shape) {
		s.fetch().setActive(false)
	}

	def Run() {
		frame.run()

		environment_bindings.foreach {
			case (symbol, ap) =>
				ap.startAnimation()
		}
	}

	class Article
	val a = new Article()
	val an = new Article()
	class ActionTiming
	val onPress = new ActionTiming()
	val onRelease = new ActionTiming()

	// need to add for all classes/objectsScala
	def about(s: Symbol) {
		if (shape_bindings.contains(s))
			println(Shape(s))
		else if (environment_bindings.contains(s))
			println(Environment(s))
		else if (action_bindings.contains(s))
			println(Action(s))
		else
			println("Unbound variable")
	}

	implicit def symbol2Shape(s: Symbol) = Shape(s)
	implicit def symbol2Environment(s: Symbol) = Environment(s)
	implicit def symbol2Action(s: Symbol) = Action(s)
	implicit def symbol2Panel(s: Symbol) = ScalaPanel(s)
	implicit def symbol2Button(s: Symbol) = ScalaButton(s)
	implicit def symbol2Label(s: Symbol) = ScalaLabel(s)

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

	def propertyChange(event: PropertyChangeEvent) {
		if (event.getPropertyName() != "Interaction")
			return
		val actorChild: AnimatingChild = event.getOldValue().asInstanceOf[AnimatingChild]
		val acteeChild: AnimatingChild = event.getNewValue().asInstanceOf[AnimatingChild]
		val actor: Shape = animating_child_to_symbol.get(actorChild).get
		val actee: Shape = animating_child_to_symbol.get(acteeChild).get

		if (!interaction_bindings.contains(actor))
			return
		val actor_bindings = interaction_bindings.get(actor).get
		
		if (!actor_bindings.contains(actee))
			return
		val funcs: Set[(Shape, Shape) => Unit] = actor_bindings.get(actee).get
		for (func <- funcs) {
			func(actor, actee)
		}
	}
}
