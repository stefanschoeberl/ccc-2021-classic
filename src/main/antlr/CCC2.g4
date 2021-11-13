grammar CCC2;

program: INT function;

function: 'start' statementList 'end';

statementList: statement*;

statement: printStatement | ifElseStatement | returnStatement;

printStatement: 'print' expr;
ifElseStatement: 'if' condition=(TRUE|FALSE) thenBranch=statementList 'end' 'else' elseBranch=statementList 'end';
returnStatement: 'return' expr;

expr: INT
    | STRING
    | value=(TRUE|FALSE)
    ;

TRUE: 'true';
FALSE: 'false';
INT: [0-9]+;
STRING: [_a-zA-Z0-9]+;
WS: [ \t\r\n]+ -> skip;
