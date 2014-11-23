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

		// environment 1
		Create environment 'e1
		Frame addEnvironment 'e1

		Create circle 'c1 mit
			a location (12, 12) und
			a color GameCons.blue und
			a velocity (GameCons.east, GameCons.slow)
		'e1 add 'c1

		Create circle 'c2 mit
			a location (700, 12) und
			a color GameCons.cyan und
			a velocity (GameCons.west, GameCons.slow)
		'e1 add 'c2

		'e1 onKeyPress (KeyEvent.VK_SPACE, 'change_color, 'c2)

		// environment 2
		Create environment 'e2
		Frame addEnvironment 'e2

		Create rectangle 'r mit
			a color GameCons.burnt_orange
		'e2 add 'r at (400, 400)
		
		'e2 onKeyPress (KeyEvent.VK_LEFT, 'move_left, 'r)
		'e2 onKeyPress (KeyEvent.VK_RIGHT, 'move_right, 'r)		

		/*Create button 'new_game mit
			a text "New Game"
		Frame addToMenuBar 'new_game

		Create button 'high_scores mit
			a text "High Scores"
		Frame addToMenuBar 'high_scores*/

		start('c1)
		start('c2)
		start('r)

		Run
	}
}