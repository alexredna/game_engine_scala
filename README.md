# Scala Jazz
## Authors
* Nick Walther
* Andrew Cook
* Michael Gregory
* Matt Allen

## Description

Scala Jazz is a Scala DSL meant for fast and easy GUI and animation creation. To do this, Scala Jazz uses Java Swing. Java Swing is running the animator and maintaining status of the visualized objects, while Scala processes the language, and performs all of the computations necessary for the visualization. The DSL itself is an original design by the authors.

## Grammar/Syntax

program ::= __Run__ | statement \\n program  
statement ::= declaration | assignment | frameLine  
declaration ::= __Create__ type _id_ propertyList  
declaration ::= __Copy__ _id_ _id_  
assignment ::= _id_ propertyList  
propertyList ::= [glue \\n article] property [propertyList]  
frameLine ::= __ScalaFrame__ property  
type ::= component | shape  
component ::=  button | label | textField | hPanel | vPanel | environment  
container ::= hPanel | vPanel | frame  
shape ::= circle | rectangle | roundRectangle   
glue ::=  has | having | and | mit | und  
article ::= a | an  
property ::= (\*Functions defined for objects in API\*)

## API (In progress)

property\<frame\> ::= __vertical__ __split__ | __horizontal__ __split__  

property\<container\> ::= __add__ _id_\<component\> | __add__ declaration\<component\>  
property\<container\> ::= __color__ (_color_)  // sets background color

property\<button\> ::= __onClick__ (_function_) // function: (int, int) => ()  
property\<button | label | textField\> ::= __text__ (_String_)  

property\<environment\> ::= __add__ _id_\<shape\> [__at__ (_int_, _int_)] // (x, y)  
property\<environment\> ::= __onKeyPress__ (_key_, _function_, _id_\<shape\> | decleration\<shape\>)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// key: int (see java.awt.KeyEvent), function: (Shape) => Unit  
property\<environment\> ::= __onKeyRelease__ (_key_, _function_, _id_\<shape\> | decleration\<shape\>)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// key: int (see java.awt.KeyEvent), function: (Shape) => Unit  
property\<environment\> ::= __onMouseClick__ (_function_) // function: (int, int) => Unit  
property\<environment\> ::= __size__ (_double_, _double_) // (width, height)  

property\<shape\> ::= __active__ (_boolean_)  
property\<shape\> ::= __borderColor__ (_color_)  
property\<shape\> ::= __color__ (_color_)  
property\<shape\> ::= __interaction__ (_id_\<shape\> | declaration\<shape\>, _function_) // function: (Shape, Shape) => Unit  
property\<shape\> ::= __location__ (_double_, _double_) // (x, y)  
property\<shape\> ::= __onMouseClick__ (_function_) // function: (Shape) => Unit  
property\<shape\> ::= __velocity__ (_double_, _double_) // (direction, speed)  
property\<shape\> ::= __visible__ (_boolean_)  

property\<circle\> ::= __radius__ (_double_)  
property\<rectangle | roundRectangle\> ::= __size__ (_double_, _double_) // (width, height)  
property\<roundRectangle\> ::= __arcSize__ (_double_, _double_) // (width, height)  



## GETTERS:
bounds
location
velocity
