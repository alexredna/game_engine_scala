
object BrickBreaker extends GameEnvironment
{
	def main(args: Array[String])
	{
		Create environment 'e
		Create circle 'c1 mit
			a location (12, 12) und
			a color GameCons.blue und
			a velocity (GameCons.east, GameCons.medium)
		Create circle 'c2 mit
			a location (700, 12) und
			a color GameCons.cyan und
			a velocity (GameCons.west, GameCons.medium)
		'e add 'c1
		'e add 'c2
		start('c1) 
		Thread.sleep(3000)
		start('c2)
		about('c)
		about('e)
		Create rectangle 'r mit
			a color GameCons.burnt_orange und
			a velocity (315, 5)
		'e add 'r at (400, 400)
		about('r)
	}
}