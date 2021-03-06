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

import java.awt._
import java.awt.event._
import java.awt.geom._
import java.beans._
import javax.swing._

class JazzFramework extends KeyListener with PropertyChangeListener {
  private val frame = new drawings.Frame("My Program")
  frame.addKeyListener(this)
  frame.addPropertyChangeListener(this)

  private var shape_bindings = Map[Symbol, AnimatingChild]()
  private var animating_child_to_symbol = Map[AnimatingChild, Symbol]()
  private var mouse_click_bindings = Map[Shape, Shape => Unit]()
  private var interaction_bindings = Map[Shape, Map[Shape, Set[(Shape, Shape) => Unit]]]()

  private var environment_bindings = Map[Symbol, AnimatingPanel]()
  private var environment_click_bindings = Map[AnimatingPanel, (Int, Int) => Unit]()

  private var key_press_bindings   = Map[Int, Map[Shape, Shape => Unit]]()
  private var key_release_bindings = Map[Int, Map[Shape, Shape => Unit]]()

  private var panel_bindings = Map[Symbol, SplitPanel]()
  private var component_bindings = Map[Symbol, JComponent]()
  private var button_press_bindings = Map[JButton, () => Unit]()

  private def isBound(s: Symbol): Boolean =
    shape_bindings.contains(s) || environment_bindings.contains(s) ||
    panel_bindings.contains(s) || component_bindings.contains(s)

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
      ap.addMouseListener(new MouseAdapter() {
          override def mouseClicked(e: MouseEvent) {
            var foundShape = false
            for ((shape, func) <- mouse_click_bindings) {
              if (ap.containsChild(shape.fetch()) &&
                shape.fetch().contains(e.getPoint())) {
                func(shape)
                foundShape = true
              }
            }
            if (!foundShape && environment_click_bindings.contains(ap)) {
              val func: (Int, Int) => Unit = environment_click_bindings.get(ap).get
              func(e.getPoint().x, e.getPoint().y)
            }
          }
        })
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

    def hPanel(s: Symbol): ScalaPanel = {
      assertNotBound(s)
      val p = new SplitPanel(true)
      panel_bindings += (s -> p)
      ScalaPanel(s)
    }

    def vPanel(s: Symbol): ScalaPanel = {
      assertNotBound(s)
      val p = new SplitPanel(false)
      panel_bindings += (s -> p)
      ScalaPanel(s)
    }

    def button(s: Symbol): ScalaComponent = {
      assertNotBound(s)
      val b = new JButton()
      b.addActionListener(new ActionListener() {
          def actionPerformed(e: ActionEvent) {
            if (button_press_bindings.contains(b)) {
              val func: () => Unit = button_press_bindings.get(b).get
              func()
            }
          }
        });
      b.setForeground(Color.BLACK);
      b.setFocusable(false)
      component_bindings += (s -> b)
      ScalaComponent(s)
    }

    def label(s: Symbol): ScalaComponent = {
      assertNotBound(s)
      val l = new JLabel()
      l.setForeground(Color.BLACK)
      component_bindings += (s -> l)
      ScalaComponent(s)
    }

    def textField(s: Symbol): ScalaComponent = {
      assertNotBound(s)
      val t = new JTextField(10)
      t.setForeground(Color.BLACK)
      component_bindings += (s -> t)
      ScalaComponent(s)
    }
  }

  def Copy (s1: Symbol, s2: Symbol) {
    assertShape(s1)
    assertNotBound(s2)
    val ac1: AnimatingChild = shape_bindings.get(s1).get
    var ac2: AnimatingChild = null
    ac1 match {
      case c: Circle =>
        ac2 = new Circle(c)
      case r: Rectangle =>
        ac2 = new Rectangle(r)
      case rr: RoundRectangle =>
        ac2 = new RoundRectangle(rr)
    }

    shape_bindings += (s2 -> ac2)
    animating_child_to_symbol += (ac2 -> s2)
    if (mouse_click_bindings.contains(Shape(s1))) {
      val func = mouse_click_bindings.get(Shape(s1)).get
      mouse_click_bindings += (Shape(s2) -> func)
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
      case j: ScalaComponent => j._toString()
    }

    def active(active: Boolean): JazzElement = this match {
      case j: Shape => j._active(active)
      case default => methodError("active"); this
    }

    def add(s: Symbol): JazzElement = {
      if (environment_bindings.contains(s))
        add(Environment(s))
      else if (shape_bindings.contains(s))
        add(Shape(s))
      else if (panel_bindings.contains(s))
        add(ScalaPanel(s))
      else if (component_bindings.contains(s))
        add(ScalaComponent(s))
      else
        sys.error(s + " is not a valid symbol")
      this
    }
    def add(e: JazzElement): JazzElement = this match {
      case j: Environment => j._add(e)
      case j: ScalaPanel => j._add(e)
      case default => methodError("add"); this
    }

    def arcSize(width: Double, height: Double): JazzElement = this match {
      case j: Shape => j._arcSize(width, height)
      case default => methodError("arcSize"); this
    }

    def at(x: Int, y: Int): JazzElement = this match {
      case j: Environment => j._at(x, y)
      case default => methodError("at"); this
    }

    def borderColor(color: Color): JazzElement = this match {
      case j: Shape => j._borderColor(color)
      case default => methodError("borderColor"); this
    }

    def bounds(): (Double, Double, Double, Double) = this match {
      case j: Shape => j.g_bounds()
      case default => methodError("location"); (0, 0, 0, 0)
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
    def location(): (Double, Double) = this match {
      case j: Shape => j.g_location()
      case default => methodError("location"); (0, 0)
    }

    def onClick(func: () => Unit): JazzElement = this match {
      case j: ScalaComponent => j._onClick(func)
      case default => methodError("onClick"); this
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
    
    def onMouseClick(func: Shape => Unit): JazzElement = this match {
      case j: Shape => j._onClick(func)
      case default => methodError("onClick"); this
    }
    def onMouseClick(func: (Int, Int) => Unit): JazzElement = this match {
      case j: Environment => j._onClick(func)
      case default => methodError("onClick"); this
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

    def text(str: String): JazzElement = this match {
      case j: ScalaComponent => j._text(str)
      case default => methodError("text"); this
    }

    def velocity(direction: Double, speed: Double): JazzElement = this match {
      case j: Shape => j._velocity(direction, speed)
      case default => methodError("velocity"); this
    }
    def velocity(): (Double, Double) = this match {
      case j: Shape => j.g_velocity()
      case default => methodError("velocity"); (0, 0)
    }

    def visible(visible: Boolean): JazzElement = this match {
      case j: Shape => j._visible(visible)
      case default => methodError("visible"); this
    }

    def has(t: Article): JazzElement = this
    def having(t: Article): JazzElement = this
    def and(t: Article): JazzElement = this
    def mit(t: Article): JazzElement = this
    def und(t: Article): JazzElement = this
  }

  case class Environment(s: Symbol) extends JazzElement(s) {
    def fetch(): AnimatingPanel = environment_bindings.get(s).get

    private def assertShapeNotInAnyEnvironment(shape: Shape) {
      for ((eSymbol, animatingPanel) <- environment_bindings) {
        if (animatingPanel.containsChild(shape.fetch()))
          sys.error(shape + " already added to an environment")
      }
    }

    var lastAdded: Shape = null

    def _add(e: JazzElement): Environment = {
      e match {
        case s: Shape =>
          assertShapeNotInAnyEnvironment(s)
          fetch().addChild(s.fetch())
          lastAdded = s
        case default => sys.error("Cannot add " + e + " to environment: it is not a shape")
      }
      this
    }

    def _at(x: Int, y: Int): Environment = {
      if (lastAdded == null)
        sys.error("at called on Environment without previous add")
      lastAdded.location(x, y)
      lastAdded = null
      this
    }

    def _onClick(func: (Int, Int) => Unit): Environment = {
      val ap: AnimatingPanel = fetch()
      if (environment_click_bindings.contains(ap))
        sys.error("onClick already called for environment " + s)
      environment_click_bindings += (ap -> func)
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

    def _size(width: Int, height: Int): Environment = {
      fetch().setPreferredSize(new Dimension(width, height))
      this
    }

    def _toString(): String = "Environment " + s
  }

  case class Shape(s: Symbol) extends JazzElement(s) {
    def fetch(): AnimatingChild = shape_bindings.get(s).get

    def _active(active: Boolean): Shape = {
      fetch().setActive(active)
      this
    }

    def _arcSize(width: Double, height: Double): Shape = {
      fetch() match {
        case rr: RoundRectangle => rr.setArcSize(width, height)
        case default => sys.error("Can only change the arcSize on a round rectangle")
      }
      this
    }

    def _borderColor(color: Color): Shape = {
      fetch().setBorderColor(color)
      this
    }

    def g_bounds(): (Double, Double, Double, Double) = {
      var r: Rectangle2D.Double = fetch().getBounds()
      (r.x, r.y, r.width, r.height)
    }

    def _color(color: Color): Shape = {
      fetch().setColor(color)
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

    def _location(x: Double, y: Double): Shape = {
      fetch().setLocation(x, y)
      this
    }
    def g_location(): (Double, Double) = {
      var p: Point2D.Double = fetch().getLocation()
      (p.x, p.y)
    }

    def _onClick(func: Shape => Unit): Shape = {
      if (mouse_click_bindings.contains(this))
        sys.error("onClick already called on shape " + s)
      mouse_click_bindings += (this -> func)
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

    def _velocity(direction: Double, speed: Double): Shape = {
      if (direction < 0 || direction > 360)
        sys.error("Invalid direction for velocity")
      fetch().setVelocity(direction, speed)
      this
    }
    def g_velocity(): (Double, Double) = (fetch().getDirection(), fetch().getSpeed())

    def _visible(visible: Boolean): Shape = {
      fetch().setVisible(visible)
      this
    }

    def _toString(): String = fetch().toString()
  }

  object ScalaFrame {
    private var tooLateToSplit: Boolean = false
    def horizontal(s: Split) = {
      if (tooLateToSplit)
        sys.error("Already called split on frame")
      tooLateToSplit = true
      frame.setContentPane(new SplitPanel(true));
      this
    }

    def vertical(s: Split) = {
      if (tooLateToSplit)
        sys.error("Already called split on frame")
      tooLateToSplit = true
      frame.setContentPane(new SplitPanel(false));
      this
    }

    def color(color: Color) = {
      frame.getContentPane().setBackground(color)
      frame.getContentPane().asInstanceOf[JPanel].setOpaque(true)
      this
    }

    def add(either: JazzElement) = {
      either match {
        case e: Environment =>
          frame.getContentPane().asInstanceOf[SplitPanel].addChild(e.fetch())
        case s: ScalaAbstractComponent =>
          frame.getContentPane().asInstanceOf[SplitPanel].addChild(s.fetch())
        case default => sys.error("Cannot add " + either + " to frame.")
      }
      this
    }
  }

  abstract class ScalaAbstractComponent(s: Symbol) extends JazzElement(s) {
    def fetch(): JComponent
  }
  case class ScalaPanel(s: Symbol) extends ScalaAbstractComponent(s) {
    override def fetch(): SplitPanel = panel_bindings.get(s).get

    def _color(color: Color): ScalaPanel = {
      fetch().setBackground(color)
      fetch().setOpaque(true)
      this
    }

    def _add(either: JazzElement): ScalaPanel = {
      either match {
        case e: Environment =>
          fetch().asInstanceOf[SplitPanel].addChild(e.fetch())
        case s: ScalaAbstractComponent =>
          fetch().asInstanceOf[SplitPanel].addChild(s.fetch())
        case default => sys.error("Cannot add " + either + " to panel.")
      }
      this
    }

    def _toString(): String = "ScalaPanel()"
  }

  case class ScalaComponent(s: Symbol) extends ScalaAbstractComponent(s) {
    override def fetch(): JComponent = component_bindings.get(s).get

    private def methodError(methodName: String) {
      sys.error(methodName + " is not a valid action for " + s)
    }

    def _onClick(func: () => Unit): ScalaComponent = {
      fetch() match {
        case b: JButton =>
          if (button_press_bindings.contains(b))
            sys.error("onClick already called on button " + s)
          button_press_bindings += (b -> func)
        case default => methodError("onClick")
      }
      this
    }

    def _text(str: String): ScalaComponent = {
      fetch() match {
        case b: JButton    => b.setText(str)
        case l: JLabel     => l.setText(str)
        case t: JTextField => t.setText(str)
        case default => methodError("text")
      }
      this
    }

    def _toString(): String = fetch() match {
        case b: JButton => "Button(" + b.getText() + ")"
        case l: JLabel => "Label(" + l.getText() + ")"
        case t: JTextField => "TextField(" + t.getText() + ")"
        case default => methodError("toString"); ""
    }
  }

  def Run() {
    frame.getContentPane().asInstanceOf[SplitPanel].initLayout()
    frame.run()
    frame.getContentPane().asInstanceOf[SplitPanel].startAnimation()
  }

  class Article
  val a = new Article()
  val an = new Article()
  class Split
  val split = new Split()

  // need to add for all classes/objectsScala
  def about(s: Symbol) {
    if (shape_bindings.contains(s))
      println(Shape(s))
    else if (environment_bindings.contains(s))
      println(Environment(s))
    else if (panel_bindings.contains(s))
      println(ScalaPanel(s))
    else if (component_bindings.contains(s))
      println(ScalaComponent(s))
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
    else if (component_bindings.contains(s))
      element = ScalaComponent(s)
    else
      sys.error("Variable " + s + " not found")
    element
  }

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
