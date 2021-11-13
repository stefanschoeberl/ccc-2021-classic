grammar CCC4;

program: INT function+;

function: 'start' statementList 'end';

statementList: statement*;

statement: printStatement
         | ifElseStatement
         | returnStatement
         | varStatement
         | setStatement
         | postponeStatment
         ;

printStatement: 'print' expr;
ifElseStatement: 'if' condition=expr thenBranch=statementList 'end' 'else' elseBranch=statementList 'end';
returnStatement: 'return' expr;
varStatement: 'var' name=STRING expr;
setStatement: 'set' name=STRING expr;
postponeStatment: 'postpone' statementList 'end';

expr: INT                # IntExpr
    | STRING             # StringExpr
    | value=(TRUE|FALSE) # BoolExpr
    ;

TRUE: 'true';
FALSE: 'false';
INT: [0-9]+;
STRING: [_a-zA-Z0-9]+;
WS: [ \t\r\n]+ -> skip;
