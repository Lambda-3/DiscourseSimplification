[![Build Status](https://travis-ci.org/Lambda-3/DiscourseSimplification.svg?branch=master)](https://travis-ci.com/Lambda-3/DiscourseSimplification)

# Discourse Simplification

A project for simplifying sentences wrt. discourse/rhetorical structures.

This is the core component of the [Graphene](https://github.com/Lambda-3/Graphene) project.

## Setup

    mvn clean install -DskipTests

### Run the program
Create a new text file with the input

    vim input.txt
     
Run program

    mvn clean compile exec:java
    
Inspect output

    cat output_default.txt
    cat output_flat.txt

## Use as library
Check `App.java`. 
Or its usage in the [Graphene](https://github.com/Lambda-3/Graphene) project.
    
   
## Contributors (alphabetical order)
- Andre Freitas
- Bernhard Bermeitinger
- Christina Niklaus
- Matthias Cetto
- Siegfried Handschuh
