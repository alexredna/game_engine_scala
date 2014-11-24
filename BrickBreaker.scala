import java.awt.event.KeyEvent

object BrickBreaker extends JazzFramework
{
    var left_score: Int = 0
    var right_score: Int = 0

    def main(args: Array[String])
    {
        // define all shapes

        Create circle 'c1 mit
            a location (188, 500) und
            a radius 25 und
            a color GameCons.blue und
            a velocity (GameCons.north, 10) und
            an active true

        Create circle 'c2 mit
            a location (188, 500) und
            a radius 25 und
            a color GameCons.cyan und
            a velocity (75, 10) und
            an active true

        Create rectangle 'p1 mit
            a location (160, 550) und
            a size (80, 20) und
            an active true

        Create rectangle 'p2 mit
            a location (160, 550) und
            a size (80, 20) und
            an active true

        // interactions between already defined shapes

        'c1 interaction ('p1, bounce _)
        'c2 interaction ('p2, bounce _)

        // define all environments

        Create environment 'e1 mit
            a size (400, 600) und
            an onKeyPress   (KeyEvent.VK_A, move_left _, 'p1) und
            an onKeyRelease (KeyEvent.VK_A, stop_moving _, 'p1) und
            an onKeyPress   (KeyEvent.VK_D, move_right _, 'p1) und
            an onKeyRelease (KeyEvent.VK_D, stop_moving _, 'p1)

        Create environment 'e2 mit
            a size (400, 600) und
            an onKeyPress   (KeyEvent.VK_LEFT, move_left _, 'p2) und
            an onKeyRelease (KeyEvent.VK_LEFT, stop_moving _, 'p2) und
            an onKeyPress   (KeyEvent.VK_RIGHT, move_right _, 'p2) und
            an onKeyRelease (KeyEvent.VK_RIGHT, stop_moving _, 'p2)

        for (row <- 0 until 4) {
            for (col <- 0 until 10) {
                var rr = Symbol("brick1" + row + col)
                Create roundRectangle rr und
                    a size (35, 15) und
                    a arcSize (5, 3)
                'e1 add rr at (25 + col * 35, 25 + row * 15)
                'c1 interaction (rr, destroyAndBouncePlayerLeft _)
            }
        }

        for (row <- 0 until 4) {
            for (col <- 0 until 10) {
                var rr = Symbol("brick2" + row + col)
                Create roundRectangle rr und
                    a size (35, 15) und
                    a arcSize (5, 3)
                'e2 add rr at (25 + col * 35, 25 + row * 15)
                'c2 interaction (rr, destroyAndBouncePlayerRight _)
            }
        }

        'e1 add 'c1 und
            an add 'p1

        'e2 add 'c2 und
            an add 'p2

        // define the frame

        ScalaFrame vsplit 2
        ScalaFrame(0) = Create hPanel ('menu_bar, 7) mit
            a color GameCons.blue
        ScalaFrame(1) = Create hPanel ('center_panel, 3) mit
            a color GameCons.green

        Create button 'new_game_button mit
            a text "New Game"
        'menu_bar(0) = 'new_game_button
        'menu_bar(1) = Create button 'options_button text "Options"
        'menu_bar(2) = Create button 'help_button text "Help"

        'center_panel(0) = 'e1
        'center_panel(1) = 'e2
        'center_panel(2) = Create vPanel ('side_panel, 5) mit
            a color GameCons.red

        'side_panel(0) = Create label 'high_scores_label

        Create label 'credits_label mit
            a text "Copyright your mom"
        'side_panel(1) = 'credits_label
        'side_panel(2) = Create label 'left_score mit
            a text "Left player score: " + left_score
        'side_panel(3) = Create label 'right_score mit
            a text "Right player score: " + right_score

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

    def bounce(actor: Shape, actee: Shape) {
        println(actor + " bounces " + actee)
    }

    def destroy(actor: Shape, actee: Shape) {
        println(actor + " destroys " + actee)
        actee visible false
    }

    def change_color(actor: Shape) {
        actor color GameCons.green
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
}
