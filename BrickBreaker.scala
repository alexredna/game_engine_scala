import java.awt.event.KeyEvent;

object BrickBreaker extends GameEnvironment
{
	def main(args: Array[String])
	{
		Create environment 'e

		Create action 'change_color mit
			onPress color GameCons.burnt_orange

		Create action 'move_left mit
			onPress velocity (GameCons.west, 10) und
			onRelease velocity (0, 0)
		Create action 'move_right mit
			onPress velocity (GameCons.east, 10) und
			onRelease velocity (0, 0)

		Create circle 'c1 mit
			a location (12, 12) und
			a color GameCons.blue und
			a velocity (GameCons.east, GameCons.slow)
		'e add 'c1

		Create circle 'c2 mit
			a location (700, 12) und
			a color GameCons.cyan und
			a velocity (GameCons.west, GameCons.slow)
		'e add 'c2

		Create rectangle 'r mit
			a color GameCons.burnt_orange
		'e add 'r at (400, 400)
		
		'e onKeyPress (KeyEvent.VK_LEFT, 'move_left, 'r)
		'e onKeyPress (KeyEvent.VK_RIGHT, 'move_right, 'r)
		'e onKeyPress (KeyEvent.VK_SPACE, 'change_color, 'c2)

		start('c1)
		start('c2)
		start('r)

		
	}
}