import drawings.Circle
import drawings.AnimatingChild
import drawings.AnimatingPanel
import drawings.Frame
import drawings.Rectangle
import drawings.SplitPanel
import drawings.RoundRectangle

import scala.collection.mutable.Map
import scala.language.implicitConversions
import scala.runtime._

import java.awt.event._
import java.awt._
import java.beans._
import javax.swing._

class JazzFramework extends KeyListener with PropertyChangeListener {
    private val frame = new drawings.Frame()
    frame.addKeyListener(this)
    frame.addPropertyChangeListener(this)

    private var shape_bindings = Map[Symbol, AnimatingChild]()
    private var animating_child_to_symbol = Map[AnimatingChild, Symbol]()
    private var environment_bindings = Map[Symbol, AnimatingPanel]()
    private var key_press_bindings   = Map[Int, Map[Shape, Shape => Unit]]()
    private var key_release_bindings = Map[Int, Map[Shape, Shape => Unit]]()
    private var interaction_bindings = Map[Shape, Map[Shape, Set[(Shape, Shape) => Unit]]]()

    private var panel_bindings = Map[Symbol, SplitPanel]()
    private var button_bindings = Map[Symbol, JButton]()
    private var label_bindings = Map[Symbol, JLabel]()

    private def isBound(s: Symbol): Boolean =
        shape_bindings.contains(s) || environment_bindings.contains(s) ||
        panel_bindings.contains(s) || button_bindings.contains(s) ||
        label_bindings.contains(s)

    private def assertNotBound(s: Symbol) {
        if (isBound(s))
            sys.error("Variable " + s + " is already bound.")
    }

    private def assertShape(s: Symbol) {
        if (!shape_bindings.contains(s))
            sys.error("Symbol " + s + " is not a shape")
    }

    object Create {
        def environment(s: Symbol): Environment = {
            assertNotBound(s)
            val ap = new AnimatingPanel(frame)
            environment_bindings += (s -> ap)
            Environment(s)
        }

        def circle(s: Symbol): Shape = {
            assertNotBound(s)
            val c = new Circle()
            shape_bindings += (s -> c)
            animating_child_to_symbol += (c -> s)
            Shape(s)
        }

        def rectangle(s: Symbol): Shape = {
            assertNotBound(s)
            val r = new Rectangle()
            shape_bindings += (s -> r)
            animating_child_to_symbol += (r -> s)
            Shape(s)
        }
        
        def roundRectangle(s: Symbol): Shape = {
            assertNotBound(s)
            val rr = new RoundRectangle()
            shape_bindings += (s -> rr)
            animating_child_to_symbol += (rr -> s)
            Shape(s)
        }

        def hPanel(s: Symbol, n: Int): ScalaPanel = {
            assertNotBound(s)
            val p = new SplitPanel(n, true)
            panel_bindings += (s -> p)
            ScalaPanel(s)
        }

        def vPanel(s: Symbol, n: Int): ScalaPanel = {
            assertNotBound(s)
            val p = new SplitPanel(n, false)
            panel_bindings += (s -> p)
            ScalaPanel(s)
        }

        def button(s: Symbol): ScalaButton = {
            assertNotBound(s)
            val b = new JButton()
            b.setFocusable(false)
            button_bindings += (s -> b)
            ScalaButton(s)
        }

        def label(s: Symbol): ScalaLabel = {
            assertNotBound(s)
            val b = new JLabel()
            label_bindings += (s -> b)
            ScalaLabel(s)
        }
    }

    class JazzElement(s: Symbol) {
        private def methodError(methodName: String) {
            sys.error(methodName + " is not a valid action for " + s)
        }

        def getSymbol(): Symbol = s

        override def toString(): String = this match {
            case j: Environment => j._toString()
            case j: Shape => j._toString()
            case j: ScalaPanel => j._toString()
            case j: ScalaButton => j._toString()
            case j: ScalaLabel => j._toString()
        }

        def active(active: Boolean): JazzElement = this match {
            case j: Shape => j._active(active)
            case default => methodError("active"); this
        }

        def add(s: Symbol): JazzElement = { assertShape(s); add(Shape(s)) }
        def add(s: Shape): JazzElement = this match {
            case j: Environment => j._add(s)
            case default => methodError("addShape"); this
        }

        def arcSize(width: Double, height: Double): JazzElement = this match {
            case j: Shape => j._arcSize(width, height)
            case default => methodError("arcSize"); this
        }

        def at(x: Int, y: Int): JazzElement = this match {
            case j: Environment => j._at(x, y)
            case default => methodError("at"); this
        }

        def color(color: Color): JazzElement = this match {
            case j: Shape => j._color(color)
            case j: ScalaPanel => j._color(color)
            case default => methodError("color"); this
        }

        def interaction(s: Symbol, func: (Shape, Shape) => Unit): JazzElement =
            { assertShape(s); interaction(Shape(s), func) }
        def interaction(s: Shape, func: (Shape, Shape) => Unit): JazzElement = this match {
            case j: Shape => j._interaction(s, func)
            case default => methodError("interaction"); this
        }

        def location(x: Double, y: Double): JazzElement = this match {
            case j: Shape => j._location(x, y)
            case default => methodError("location"); this
        }

        def onKeyPress(key: Int, action: Shape => Unit, s: Symbol): JazzElement =
            { assertShape(s); onKeyPress(key, action, Shape(s)) }
        def onKeyPress(key: Int, action: Shape => Unit, child: Shape): JazzElement = this match {
            case j: Environment => j._onKeyPress(key, action, child)
            case default => methodError("onKeyPress"); this
        }

        def onKeyRelease(key: Int, action: Shape => Unit, s: Symbol): JazzElement =
            { assertShape(s); onKeyRelease(key, action, Shape(s)) }
        def onKeyRelease(key: Int, action: Shape => Unit, child: Shape): JazzElement = this match {
            case j: Environment => j._onKeyRelease(key, action, child)
            case default => methodError("onKeyRelease"); this
        }

        def radius(radius: Double): JazzElement = this match {
            case j: Shape => j._radius(radius)
            case default => methodError("radius"); this
        }

        def size(width: Double, height: Double): JazzElement = this match {
            case j: Environment => j._size(width.asInstanceOf[Int], height.asInstanceOf[Int])
            case j: Shape => j._size(width, height)
            case default => methodError("size"); this
        }

        def update(index: Int, comp: JazzElement) {
            if (panel_bindings.contains(comp.getSymbol()))
                update(index, comp.asInstanceOf[ScalaPanel])
            else if (button_bindings.contains(comp.getSymbol()))
                update(index, comp.asInstanceOf[ScalaButton])
            else if (label_bindings.contains(comp.getSymbol()))
                update(index, comp.asInstanceOf[ScalaLabel])
            else
                methodError("update/()")
        }
        def update(index: Int, comp: ScalaComponent) {this match {
            case j: ScalaPanel => j._update(index, comp)
            case default => methodError("update")
        }}
        def update(index: Int, compSymbol: Symbol) {this match {
            case j: ScalaPanel => j._update(index, compSymbol)
            case default => methodError("update")
        }}

        def velocity(direction: Double, speed: Double): JazzElement = this match {
            case j: Shape => j._velocity(direction, speed)
            case default => methodError("velocity"); this
        }

        def visible(visible: Boolean): JazzElement = this match {
            case j: Shape => j._visible(visible)
            case default => methodError("visible"); this
        }

        def text(str: String): JazzElement = this match {
            case j: ScalaButton => j._text(str)
            case j: ScalaLabel => j._text(str)
            case default => methodError("text"); this
        }

        def mit(t: Article): JazzElement = this
        def und(t: Article): JazzElement = this
    }

    case class Environment(s: Symbol) extends JazzElement(s) {
        private def fetch(): AnimatingPanel = environment_bindings.get(s).get

        var lastAdded: Shape = null

        def _add(s: Shape): Environment = {
            fetch().addChild(s.fetch())
            lastAdded = s
            this
        }

        def _at(x: Int, y: Int): Environment = {
            if (lastAdded == null)
                sys.error("at called on Environment without previous add")
            lastAdded.location(x, y)
            lastAdded = null
            this
        }

        def _size(width: Int, height: Int): Environment = {
            fetch().setPreferredSize(new Dimension(width, height))
            this
        }

        def _onKeyPress(key: Int, action: Shape => Unit, child: Shape): Environment = {
            if (!key_press_bindings.contains(key)) {
                var action_set = Map[Shape, Shape => Unit]()
                action_set += (child -> action)
                key_press_bindings += (key -> action_set)
            } else {
                key_press_bindings.get(key).get += (child -> action)
            }
            this
        }

        def _onKeyRelease(key: Int, action: Shape => Unit, child: Shape): Environment = {
            if (!key_release_bindings.contains(key)) {
                var action_set = Map[Shape, Shape => Unit]()
                action_set += (child -> action)
                key_release_bindings += (key -> action_set)
            } else {
                key_release_bindings.get(key).get += (child -> action)
            }
            this
        }

        //def mit(t: Article): Environment = this
        //def und(t: Article): Environment = this
        def _toString(): String = "Environment " + s
    }

    case class Shape(s: Symbol) extends JazzElement(s) {
        def fetch(): AnimatingChild = shape_bindings.get(s).get

        def _location(x: Double, y: Double): Shape = {
            fetch().setLocation(x, y)
            this
        }

        def _color(color: Color): Shape = {
            fetch().setColor(color)
            this
        }

        def _velocity(direction: Double, speed: Double): Shape = {
            if (direction < 0 || direction > 360)
                sys.error("Invalid direction for velocity")
            fetch().setVelocity(direction, speed)
            this
        }

        def _radius(radius: Double): Shape = {
            fetch() match {
                case c: Circle => c.setRadius(radius)
                case default => sys.error("Can only change the radius on a circle")
            }
            this
        }

        def _size(width: Double, height: Double): Shape = {
            fetch() match {
                case r: Rectangle => r.setSize(width, height)
                case rr: RoundRectangle => rr.setSize(width, height)
                case default => sys.error("Can only set width on shapes with a width")
            }
            this
        }

        def _arcSize(width: Double, height: Double): Shape = {
            fetch() match {
                case rr: RoundRectangle => rr.setArcSize(width, height)
                case default => sys.error("Can only change the arcSize on a round rectangle")
            }
            this
        }

        def _active(active: Boolean): Shape = {
            fetch().setActive(active)
            this
        }

        def _visible(visible: Boolean): Shape = {
            fetch().setVisible(visible)
            this
        }

        def _interaction(s: Shape, func: (Shape, Shape) => Unit): Shape = {
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

        //def mit(t: Article): Shape = this
        //def und(t: Article): Shape = this
        def _toString(): String = fetch().toString()
    }

    object ScalaFrame {
        private var tooLateToSplit: Boolean = false
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

        def update(index: Int, comp: JazzElement) {
            if (panel_bindings.contains(comp.getSymbol()))
                update(index, comp.asInstanceOf[ScalaPanel])
            else if (button_bindings.contains(comp.getSymbol()))
                update(index, comp.asInstanceOf[ScalaButton])
            else if (label_bindings.contains(comp.getSymbol()))
                update(index, comp.asInstanceOf[ScalaLabel])
            else
                sys.error("Cannot add this component to the frame")
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

    abstract class ScalaComponent(s: Symbol) extends JazzElement(s) {
        def fetch(): JComponent
    }
    case class ScalaPanel(s: Symbol) extends ScalaComponent(s) {
        override def fetch(): SplitPanel = panel_bindings.get(s).get

        def _color(color: Color): ScalaPanel = {
            fetch().setBackground(color)
            this
        }

        def _update(index: Int, comp: ScalaComponent) {
            fetch().setChild(index, comp.fetch())
        }

        def _update(index: Int, compSymbol: Symbol) {
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

        //def mit(t: Article): ScalaPanel = this
        //def und(t: Article): ScalaPanel = this
        def _toString(): String = "ScalaPanel()"
    }

    case class ScalaButton(s: Symbol) extends ScalaComponent(s) {
        override def fetch(): JButton = button_bindings.get(s).get

        def _text(str: String): ScalaButton = {
            fetch().setText(str)
            this
        }

        //def mit(t: Article): ScalaButton = this
        //def und(t: Article): ScalaButton = this
        def _toString(): String = "ScalaButton(" + fetch().getText() + ")"
    }

    case class ScalaLabel(s: Symbol) extends ScalaComponent(s) {
        override def fetch(): JLabel = label_bindings.get(s).get

        def _text(str: String): ScalaLabel = {
            fetch().setText(str)
            this
        }

        //def mit(t: Article): ScalaLabel = this
        //def und(t: Article): ScalaLabel = this
        def _toString(): String = "ScalaLabel(" + fetch().getText() + ")"
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

    // need to add for all classes/objectsScala
    def about(s: Symbol) {
        if (shape_bindings.contains(s))
            println(Shape(s))
        else if (environment_bindings.contains(s))
            println(Environment(s))
        else
            println("Unbound variable")
    }

    implicit def symbol2JazzElement(s: Symbol): JazzElement = {
        var element: JazzElement = new JazzElement(s)
        if (environment_bindings.contains(s))
            element = Environment(s)
        else if (shape_bindings.contains(s))
            element = Shape(s)
        else if (panel_bindings.contains(s))
            element = ScalaPanel(s)
        else if (button_bindings.contains(s))
            element = ScalaButton(s)
        else if (label_bindings.contains(s))
            element = ScalaLabel(s)
        else
            sys.error("Variable " + s + " not found")
        element
    }


    //implicit def symbol2JazzElement(s: Symbol) = new JazzElement(s)

    //implicit def symbol2Shape(s: Symbol) = Shape(s)
    //implicit def symbol2Environment(s: Symbol) = Environment(s)
    //implicit def symbol2Panel(s: Symbol) = ScalaPanel(s)
    //implicit def symbol2Button(s: Symbol) = ScalaButton(s)
    //implicit def symbol2Label(s: Symbol) = ScalaLabel(s)

    def keyPressed(e: KeyEvent) {
        var key: Int = e.getKeyCode()
        if (key_press_bindings.contains(key)) {
            var shape_list = key_press_bindings.get(key).get
            shape_list.foreach { case(shape, func) => func(shape) }
        }
    }

    def keyReleased(e: KeyEvent) {
        var key: Int = e.getKeyCode()
        if (key_release_bindings.contains(key)) {
            var shape_list = key_release_bindings.get(key).get
            shape_list.foreach { case(shape, func) => func(shape) }
        }
    }

    def keyTyped(e: KeyEvent) { }

    def propertyChange(event: PropertyChangeEvent) {
        if (event.getPropertyName() != "Interaction")
            return
        val actorChild: AnimatingChild = event.getOldValue().asInstanceOf[AnimatingChild]
        val acteeChild: AnimatingChild = event.getNewValue().asInstanceOf[AnimatingChild]
        val actor: Shape = Shape(animating_child_to_symbol.get(actorChild).get)
        val actee: Shape = Shape(animating_child_to_symbol.get(acteeChild).get)

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
