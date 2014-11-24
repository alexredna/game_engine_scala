all: install run

install: java scala

java:
	javac drawings/*.java

scala:
	scalac *.scala -deprecation -feature


clean:
	rm -f drawings/*.class
	rm -f *.class

run:
	scala BrickBreaker
