# Cloudflight Coding Contest / Classic CCC - 05.11.2021

This repository contains my submissions for the [35th Cloudflight Coding Contest](https://register.codingcontest.org/contest/4094/results).

The task was to implement an interpreter for a given programming language, in each level the specification for this language became increasingly extensive.
The language contains well known features like print-statements, variables, if-else-blocks or function calls.
It also contains some interesting ones like a postpone-statement, which seems to be inspired by the defer-statement in Go or Swift, but had its own rules.

Each input file contains a set of functions, all of them have to be executed sequentially one after the other.
The outputs of these functions are written to the output file.

My solution is based on ANTLR 4 to generate the lexer and parser.
At runtime ANTLR builds the syntax tree of the inputs.
These trees are evaluated with visitors implemented in Kotlin.

Original assignments and inputs for each level are not included (rights for those belong to Cloudflight), but in the `inputs` folder there are made-up inputs for demonstration purposes.

Each level can be run individually using these commands:

```bash
$ ./gradlew runLevel1
$ ./gradlew runLevel2
$ ./gradlew runLevel3
$ ./gradlew runLevel4
$ ./gradlew runLevel5
$ ./gradlew runLevel6
```

Alternatively they can be run all at once:

```bash
$ ./gradlew runAll
```