[![Build Status](https://travis-ci.org/Lambda-3/DiscourseSimplification.svg?branch=master)](https://travis-ci.org/Lambda-3/DiscourseSimplification)

# Discourse Simplification

A project for simplifying sentences wrt. discourse/rhetorical structures.
This works as a wrapper for the [SentenceSimplification](https://github.com/Lambda-3/SentenceSimplification) project.

## Dependencies

### SentenceSimplification

Clone and install locally
    
    git clone --branch v5.0.0 https://github.com/Lambda-3/SentenceSimplification.git 
    cd SentenceSimplification
    mvn install

## Building and Running

    mvn package

### Run the program

    mvn clean compile exec:java

## Use as library
Check `App.java`. 
Or its usage in the [Graphene](https://github.com/Lambda-3/Graphene) project.
    
   
## Contributors (alphabetical order)
- Andre Freitas
- Bernhard Bermeitinger
- Christina Niklaus
- Matthias Cetto
- Siegfried Handschuh
