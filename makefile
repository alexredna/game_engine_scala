java:
	javac drawings/*.java

scala:
	scalac GameEnvironment.scala -deprecation -feature
	scalac BrickBreaker.scala -deprecation -feature

clean:
	rm -f drawings/*.class
	rm -f *.class

run:
	scala BrickBreaker