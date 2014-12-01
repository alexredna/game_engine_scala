# Scala Jazz
## About
Authors
* Nicholas Walther
* Andrew Cook
* Michael Gregory
* Matt Allen

## Description

Scala Jazz is a Scala DSL meant for fast and easy GUI and animation creation. To do this, Scala Jazz uses Java Swing. Java Swing is running the animator and maintaining status of the visualized objects, while Scala processes the language, and performs all of the computations necessary for the visualization. The DSL itself is an original design by the authors.

## Syntax
program ::= __Run__ | statement \\n program  
statement ::= declaration | assignment | frameLine  
declaration\<type\> ::= __Create__ type _id_ \[glue \\n article property\<type\>\]  
assignment\<type\> ::= _id_\<type\> property\<type\> \[glue \\n article property\<type\>\]  
frameLine ::= __ScalaFrame__ property<frame>  
property\<container\> ::= add _id_\<component\> | add declaration\<component\>  
property\<frame\> ::= vertical split | horizontal split | color _color_  
type ::= component | shape  
component ::=  button | label | textField | hPanel | vPanel | environment  
container ::= hPanel | vPanel | frame  
shape ::= circle | rectangle | roundrectangle   
glue ::=  has | having | and | mit | und  
article ::= a | an  

## API
