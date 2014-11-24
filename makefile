all: install run

install: java scala

java:
	javac -classpath /usr/share/scala/lib/scala-library.jar:target/classes \
	       drawings/*.java

scala:
	scalac *.scala -deprecation -feature


clean:
	rm -f drawings/*.class
	rm -f *.class

run:
	scala BrickBreaker
