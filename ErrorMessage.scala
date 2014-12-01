object ErrorMessage extends JazzFramework {
  def main(args: Array[String]) {
	ScalaFrame vertical split
	ScalaFrame add (Create label 'message text "You fail at life.")
	ScalaFrame add (Create hPanel 'buttons having
	  an add (Create button 'yes having
        a text "I'm sorry" and
        an onClick yes) and
	  an add (Create button 'no having
        a text "I disagree" and
        an onClick (() => 'message text "Dissagree all you want")))
	Run
  }

  def yes() { System.exit(0) }
}
