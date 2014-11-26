import java.awt.event.KeyEvent
import java.awt.geom.Line2D
import scala.language.postfixOps
import math.Pi

object BrickBreaker extends JazzFramework
{
  val screen_width = 400
  val screen_height = 600

  var left_score = 0
  var right_score = 0
  var is_paused = false

  def main(args: Array[String])
  {
    // define all shapes
    Create circle 'c1 having
      a location ((screen_width-24)/2, screen_height-100) and
      a radius 12 and
      a color GameCons.blue and
      a borderColor GameCons.black and
      a velocity (GameCons.north, GameCons.medium) and
      an active true

    Create circle 'c2 having
      a location ((screen_width-24)/2, screen_height-100) and
      a radius 12 and
      a color GameCons.cyan and
      a borderColor GameCons.black and
      a velocity (75, GameCons.medium) and
      an active true

    Create rectangle 'p1 having
      a location ((screen_width-80)/2, screen_height-50) and
      a size (80, 20) and
      an active true

    Create rectangle 'p2 having
      a location ((screen_width-80)/2, screen_height-50) and
      a size (80, 20) and
      an active true

    Create rectangle 'wr1 having
      a location (screen_width, 0) and
      a size (50, screen_height)

    Create rectangle 'wl1 having
      a location (-50, 0) and
      a size (50, screen_height)

    Create rectangle 'wt1 having
      a location (0, -50) and
      a size (screen_width, 50)

    Create rectangle 'wb1 having
      a location (0, screen_height) and
      a size (screen_width, 50)

    Create rectangle 'wr2 having
      a location (screen_width, 0) and
      a size (50, screen_height)

    Create rectangle 'wl2 having
      a location (-50, 0) and
      a size (50, screen_height)

    Create rectangle 'wt2 having
      a location (0, -50) and
      a size (screen_width, 50)

    Create rectangle 'wb2 having
      a location (0, screen_height) and
      a size (screen_width, 50)

    // interactions between already defined shapes

    'c1 interaction ('p1, bounceWithDeflection _)
    'c1 interaction ('wr1, bounce _)
    'c1 interaction ('wl1, bounce _)
    'c1 interaction ('wt1, bounce _)
    'c1 interaction ('wb1, gameOverP1 _)

    'c2 interaction ('p2, bounceWithDeflection _)
    'c2 interaction ('wr2, bounce _)
    'c2 interaction ('wl2, bounce _)
    'c2 interaction ('wt2, bounce _)
    'c2 interaction ('wb2, gameOverP2 _)

    // define all environments

    Create environment 'e1 having
      a size (screen_width, screen_height) and
      an onKeyPress   (KeyEvent.VK_A, move_left _, 'p1) and
      an onKeyRelease (KeyEvent.VK_A, stop_moving _, 'p1) and
      an onKeyPress   (KeyEvent.VK_D, move_right _, 'p1) and
      an onKeyRelease (KeyEvent.VK_D, stop_moving _, 'p1) and
      an add 'c1 and
      an add 'p1 and
      an add 'wr1 and
      an add 'wl1 and
      an add 'wt1 and
      an add 'wb1

    Create environment 'e2 having
      a size (screen_width, screen_height) and
      an onKeyPress   (KeyEvent.VK_LEFT, move_left _, 'p2) and
      an onKeyRelease (KeyEvent.VK_LEFT, stop_moving _, 'p2) and
      an onKeyPress   (KeyEvent.VK_RIGHT, move_right _, 'p2) and
      an onKeyRelease (KeyEvent.VK_RIGHT, stop_moving _, 'p2) and
      an add 'c2 and
      an add 'p2 and
      an add 'wr2 and
      an add 'wl2 and
      an add 'wt2 and
      an add 'wb2


    for (row <- 0 until 4) {
      for (col <- 0 until 10) {
        var rr = Symbol("brick1" + row + col)
        Create roundRectangle rr and
          a size (35, 15) and
          a arcSize (5, 3) and
          a borderColor GameCons.black
        row match {
          case 0 => rr color GameCons.red
          case 1 => rr color GameCons.blue
          case 2 => rr color GameCons.green
          case 3 => rr color GameCons.yellow
        }
        'e1 add rr at (25 + col * 35, 25 + row * 15)
        'c1 interaction (rr, destroyAndBouncePlayerLeft _)
      }
    }

    for (row <- 0 until 4) {
      for (col <- 0 until 10) {
        var rr = Symbol("brick2" + row + col)
        Create roundRectangle rr and
          a size (35, 15) and
          a arcSize (5, 3) and
          a borderColor GameCons.black
        row match {
          case 0 => rr color GameCons.red
          case 1 => rr color GameCons.blue
          case 2 => rr color GameCons.green
          case 3 => rr color GameCons.yellow
        }
        'e2 add rr at (25 + col * 35, 25 + row * 15)
        'c2 interaction (rr, destroyAndBouncePlayerRight _)
      }
    }

    // define the frame

    ScalaFrame vertical split
    ScalaFrame add (Create hPanel 'menu_bar having
      a color GameCons.blue)
    ScalaFrame add (Create hPanel 'center_panel having
      a color GameCons.green)

    Create button 'pause_button having
      a text "Pause" and
      an onClick pause
    'menu_bar add 'pause_button
    'menu_bar add (Create button 'options_button text "Options")
    'menu_bar add (Create button 'help_button text "Help")

    'center_panel add 'e1
    'center_panel add 'e2
    'center_panel add (Create vPanel 'side_panel color GameCons.red)

    'side_panel add (Create label 'high_scores_label)

    'side_panel add (Create label 'credits_label having
      a text "Copyright your mom")
    'side_panel add (Create label 'left_score having
      a text "Left player score: " + left_score)
    'side_panel add (Create label 'right_score having
      a text "Right player score: " + right_score)

    Run
  }

  def destroyAndBouncePlayerLeft(actor: Shape, actee: Shape) {
    destroy(actor, actee)
    bounce(actor, actee)
    left_score += 1
    'left_score text "Left player score: " + left_score
  }

  def destroyAndBouncePlayerRight(actor: Shape, actee: Shape) {
    destroy(actor, actee)
    bounce(actor, actee)
    right_score += 1
    'right_score text "Right player score: " + right_score
  }

  def bounceWithDeflection(actor: Shape, actee: Shape) {
    bounce(actor, actee)
    val (ax, ay, aw, ah) = actor.bounds
    val (bx, by, bw, bh) = actee.bounds
    val posA = ax + aw / 2.0
    val posB = bx + bw / 2.0

    val delta = posA - posB

    val (ad: Double, av: Double) = actor.velocity
    
    if (ad < 180) {
      val angle = ad - Math.toDegrees(delta / bw * Pi)
      val angleMod = angle % 360
      val a = Math.min(Math.max(angleMod, 15), 165)
      actor velocity (a, av)
    }
  }

  def bounce(actor: Shape, actee: Shape) {
    //println(actor + " bounces " + actee)
    val (ax, ay, aw, ah) = actor.bounds
    val minA = (ax, ay)
    val maxA = (ax + aw, ay + ah)
    val (bx, by, bw, bh) = actee.bounds
    val minB = (bx, by)
    val maxB = (bx + bw, by + bh)

    if (minA._1 > maxB._1 || maxA._1 < minB._1 || minA._2 > maxB._2 || maxA._2 < minB._2) {
      // separating axis theorem says there's no collision here
      return
    }

    var projection = (0.0, 0.0)
    var size = 0.0

    if (minA._1 < maxB._1) {
      val overlap =  maxB._1 - minA._1
      if (size == 0.0 || overlap < size) {
        projection = (overlap, 0.0)
        size = overlap
      }
    }
    if (maxA._1 > minB._1) {
      val overlap =  maxA._1 - minB._1
      if (size == 0.0 || overlap < size) {
        projection = (-overlap, 0.0)
        size = overlap
      }
    }
    if (minA._2 < maxB._2) {
      val overlap =  maxB._2 - minA._2
      if (size == 0.0 || overlap < size) {
        projection = (0.0, overlap)
        size = overlap
      }
    }
    if (maxA._2 > minB._2) {
      val overlap =  maxA._2 - minB._2
      if (size == 0.0 || overlap < size) {
        projection = (0.0, -overlap)
        size = overlap
      }
    }

    val pos = actor.location
    val newPos = (pos._1 + projection._1, pos._2 + projection._2)
    actor location (newPos._1, newPos._2)

    val (ad: Double, av: Double) = actor.velocity
    val velocity = (av * Math.cos(Math.toRadians(ad)), av * Math.sin(Math.toRadians(ad)))
    val newVelocity = if (Math.abs(projection._1) > Math.abs(projection._2)) (-velocity._1, velocity._2) else (velocity._1, -velocity._2)

    val newDir = Math.toDegrees(Math.atan2(newVelocity._2, newVelocity._1))
    val newSpeed = Math.hypot(newVelocity._1, newVelocity._2)

    val newDirection = if (newDir < 0) (360 + newDir) % 360 else newDir % 360
    actor velocity (newDirection, newSpeed)
  }

  def destroy(actor: Shape, actee: Shape) {
    actee visible false
  }

  def move_left(actor: Shape) {
    actor velocity (GameCons.west, 10)
  }

  def move_right(actor: Shape) {
    actor velocity (GameCons.east, 10)
  }

  def stop_moving(actor: Shape) {
    actor velocity (0, 0)
  }

  def pause() {
    val (directionC1: Double, oldVelocityC1: Double) = 'c1 velocity;
    val (directionC2: Double, oldVelocityC2: Double) = 'c2 velocity;
    if (is_paused) {
      'c1 active true//velocity (directionC1, GameCons.medium)
      'c2 active true//velocity (directionC2, GameCons.medium)
      'pause_button text "Pause"
    } else {
      'c1 active false //velocity (directionC1, 0)
      'c2 active false //velocity (directionC2, 0)
      'pause_button text "UnPause"
    }
    is_paused = !is_paused
  }

  def gameOverP1(ball: Shape, bottomWall: Shape) {
    val (dir, speed) = ball velocity
    val (x, y) = ball location;
    val dy = screen_height-24-y
    val dx = dy / Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(dir))
    ball location (x + dx, screen_height-24)

    ball active false
  }

  def gameOverP2(ball: Shape, bottomWall: Shape) {
    val (x, y) = ball location;
    ball location (x, screen_height-24)
    ball active false
  }
}
