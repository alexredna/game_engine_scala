import java.awt.event.KeyEvent;

object BrickBreaker extends GameEnvironment
{
	def main(args: Array[String])
	{
		// define all actions
		Create action 'change_color mit
			onPress color GameCons.burnt_orange

		Create action 'move_left mit
			onPress velocity (GameCons.west, 10) und
			onRelease velocity (0, 0)

		Create action 'move_right mit
			onPress velocity (GameCons.east, 10) und
			onRelease velocity (0, 0)

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

		Create environment 'e1 und
			an addShape 'c1 und
			an addShape 'c2 und
			an onKeyPress (KeyEvent.VK_SPACE, 'change_color, 'c2)

		Create environment 'e2 und
			an addShape 'r at (200, 200) und
			an onKeyPress (KeyEvent.VK_LEFT, 'move_left, 'r) und
			an onKeyPress (KeyEvent.VK_RIGHT, 'move_right, 'r)

		// define the frame

		ScalaFrame vsplit 2
		ScalaFrame(0) = Create panel 'menu_bar mit
							a hsplit 7 und
							a color GameCons.blue
		ScalaFrame(1) = Create panel 'center_panel mit
							a hsplit 3 und
							a color GameCons.green

		Create button 'new_game_button mit
			a text "New Game"
		'menu_bar(0) = 'new_game_button

		'center_panel(0) = 'e1
		'center_panel(1) = 'e2
		'center_panel(2) = Create panel 'side_panel mit
								a vsplit 5 und
								a color GameCons.red

		'side_panel(0) = Create label 'high_scores_label

		Create label 'credits_label mit
			a text "Copyright your mom"

		'side_panel(1) = 'credits_label



		start('c1)
		start('c2)
		start('r)

		Run
	}
}