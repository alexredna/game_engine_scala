object ErrorMessage extends JazzFramework {
	def main(args: Array[String]) {
		ScalaFrame vertical split
		ScalaFrame add (Create label 'message text "You fail at life.")
		ScalaFrame add (Create hPanel 'buttons)
		'buttons add (Create button 'yes text "I'm sorry" onClick yes)
		'buttons add (Create button 'no text "I disagree" onClick no)
		Run
	}

	def yes() {
		System.exit(0)
	}

	def no() {
		'message text "Disagree all you want."
	}
}