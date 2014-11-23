import drawings.Circle
import drawings.AnimatingChild
import drawings.AnimatingPanel
import drawings.Frame
import drawings.Rectangle
import drawings.JavaAction

import scala.collection.mutable.Map
import scala.language.implicitConversions

import java.awt._
import javax.swing._

class GameEnvironment {

	val frame = new drawings.Frame()
	frame.getContentPane().setBackground(GameCons.blue)

	var shape_bindings = Map[Symbol, AnimatingChild]()
	var environment_bindings = Map[Symbol, AnimatingPanel]()
	var action_bindings = Map[Symbol, JavaAction]()

	var panel_bindings = Map[Symbol, JPanel]()
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
			Shape(s)
		}

		def rectangle(s: Symbol): Shape = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val r = new Rectangle()
			shape_bindings += (s -> r)
			Shape(s)
		}

		def panel(s: Symbol): ScalaPanel = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val p = new JPanel()
			p.setOpaque(false)
			panel_bindings += (s -> p)
			ScalaPanel(s)
		}

		def button(s: Symbol): ScalaButton = {
			if (isBound(s))
				sys.error("Variable " + s + " is already bound.")
			val b = new JButton()
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

		def onKeyPress(key: Int, action: Action, child: Shape): Environment = {
			fetch().addKeyBinding(key, action.fetch(), child.fetch());
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
		var horizontal: Boolean = false
		def hsplit(n: Int) = {
			frame.getContentPane().setLayout(new GridLayout(1, n, 5, 5))
			horizontal = true
			this
		}

		def vsplit(n: Int) = {
			frame.getContentPane().setLayout(new GridLayout(n, 1, 5, 5))
			horizontal = false
			this
		}

		def color(color: Color) = {
			frame.getContentPane().setBackground(color)
			this
		}

		/*def addComponent(comp: ScalaComponent): ScalaFrame = {


			this
		}*/

		def update(index: Int, comp: ScalaComponent) {
			if (!frame.getContentPane().getLayout().isInstanceOf[GridLayout])
				sys.error("Attempted to add at invalid index")
			val layout: GridLayout = frame.getContentPane().getLayout().asInstanceOf[GridLayout]
			if (horizontal)
				if (layout.getColumns() < index)
					sys.error("Attempted to add at invalid index")
			else
				if (layout.getRows() < index)
					sys.error("Attempted to add at invalid index")
			frame.getContentPane().add(comp.fetch(), index)
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

			val content: Container = frame.getContentPane()
			if (!content.getLayout().isInstanceOf[GridLayout])
				sys.error("Attempted to add at invalid index")
			val layout: GridLayout = content.getLayout().asInstanceOf[GridLayout]
			if (horizontal)
				if (layout.getColumns() < index)
					sys.error("Attempted to add at invalid index")
			else
				if (layout.getRows() < index)
					sys.error("Attempted to add at invalid index")
			content.add(comp, index)
		}
	}

	abstract class ScalaComponent {
		def fetch(): JComponent
	}
	case class ScalaPanel(s: Symbol) extends ScalaComponent {
		var horizontal: Boolean = false
		override def fetch(): JPanel = panel_bindings.get(s).get

		def hsplit(n: Int): ScalaPanel = {
			fetch().setLayout(new GridLayout(1, n, 5, 5))
			horizontal = true
			this
		}

		def vsplit(n: Int): ScalaPanel = {
			fetch().setLayout(new GridLayout(n, 1, 5, 5))
			horizontal = false
			this
		}

		def color(color: Color): ScalaPanel = {
			fetch().setBackground(color)
			this
		}

		/*def addComponent(comp: ScalaComponent): ScalaPanel = {


			this
		}*/

		def update(index: Int, comp: ScalaComponent) {
			if (!fetch().getLayout().isInstanceOf[GridLayout])
				sys.error("Attempted to add at invalid index")
			val layout: GridLayout = fetch().getLayout().asInstanceOf[GridLayout]
			if (horizontal)
				if (layout.getColumns() < index)
					sys.error("Attempted to add at invalid index")
			else
				if (layout.getRows() < index) 
					sys.error("Attempted to add at invalid index")
			fetch().add(comp.fetch(), index)
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

			if (!fetch().getLayout().isInstanceOf[GridLayout])
				sys.error("Attempted to add at invalid index")
			val layout: GridLayout = fetch().getLayout().asInstanceOf[GridLayout]
			if (horizontal)
				if (layout.getColumns() < index)
					sys.error("Attempted to add at invalid index")
			else
				if (layout.getRows() < index)
					sys.error("Attempted to add at invalid index")
			fetch().add(comp, index)
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
}