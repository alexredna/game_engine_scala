import java.awt.event.KeyEvent;

object BrickBreaker extends GameEnvironment
{
	def main(args: Array[String])
	{
		// define all shapes

		Create circle 'c1 mit
			a location (12, 12) und
			a color GameCons.blue und
			a velocity (GameCons.east, GameCons.slow)

		Create circle 'c2 mit
			a location (300, 12) und
			a color GameCons.cyan und
			a velocity (GameCons.west, GameCons.slow)

		Create rectangle 'r mit
			a color GameCons.burnt_orange

		// define all environments

		Create environment 'e1 mit
			a size (400, 600) und
			an addShape 'c1 und
			an addShape 'c2 und
			an onKeyPress (KeyEvent.VK_SPACE, change_color, 'c2)

		Create environment 'e2 mit
			a size (400, 600) und
			an addShape 'r at (200, 200) und
			an onKeyPress   (KeyEvent.VK_LEFT, move_left, 'r) und
			an onKeyRelease (KeyEvent.VK_LEFT, stop_moving, 'r) und
			an onKeyPress   (KeyEvent.VK_RIGHT, move_right, 'r) und
			an onKeyRelease (KeyEvent.VK_RIGHT, stop_moving, 'r)

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

		'c1 interaction ('c2, destroys)

		start('c1)
		start('c2)
		start('r)

		Run
	}

	def bounces(actor: Shape, actee: Shape) {
		println(actor + " bounces " + actee)
	}

	def destroys(actor: Shape, actee: Shape) {
		println(actor + " destroys " + actee)
		actor color GameCons.red
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
