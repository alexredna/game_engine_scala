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
declaration\<type\> ::= __Create__ type _id_\<unbound\> [glue \\n article property\<type\>]  
declaration\<shape\> ::= __Copy__ _id_\<shape\> _id_\<unbound\>  
assignment\<type\> ::= _id_\<type\> property\<type\> [glue \\n article property\<type\>]  
frameLine ::= __ScalaFrame__ property\<frame\>  
type ::= component | shape  
component ::=  button | label | textField | hPanel | vPanel | environment  
container ::= hPanel | vPanel | frame  
shape ::= circle | rectangle | roundRectangle   
glue ::=  has | having | and | mit | und  
article ::= a | an  

## API

property\<container\> ::= __add__ _id_\<component\> | __add__ declaration\<component\>  
property\<frame\> ::= __vertical__ __split__ | __horizontal__ __split__  
property\<environment\> ::= __add__ _id_\<shape\> [__at__ (_int_, _int_)] // (x, y)  
property\<roundRectangle\> ::= __arcSize__ (_double_, _double_) // (width, height)  
property\<shape\> ::= __borderColor__ (_color_) | __color__ (_color_)  
property\<container\> ::= __color__ (_color_)
property\<interaction\> ::= __interaction__ (_id_\<shape\>, _function_) | __interaction__ (declaration\<shape\>)

| Tables   |      Are      |  Cool |
|----------|:-------------:|------:|
| col 1 is |  left-aligned | $1600 |
| col 2 is |    centered   |   $12 |
| col 3 is | right-aligned |    $1 |
