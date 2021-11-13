grammar CCC6;

program: INT (functions+=function)+;

function: 'start' statementList 'end';

statementList: statement*;

statement: printStatement
         | ifElseStatement
         | returnStatement
         | varStatement
         | setStatement
         | postponeStatment
         | callExpression
         | tryStatement
         ;

printStatement: 'print' expr;
ifElseStatement: 'if' condition=expr thenBranch=statementList 'end' 'else' elseBranch=statementList 'end';
returnStatement: 'return' expr;
varStatement: 'var' name=STRING expr;
setStatement: 'set' name=STRING expr;
postponeStatment: 'postpone' statementList 'end';
tryStatement: 'try' tryStatements=statementList 'end' 'catch' catchStatements=statementList 'end';

callExpression: 'call' expr;

expr: INT                # IntExpr
    | STRING             # StringExpr
    | value=(TRUE|FALSE) # BoolExpr
    | callExpression     # CallExpr
    ;

TRUE: 'true';
FALSE: 'false';
INT: [0-9]+;
STRING: [_a-zA-Z0-9]+;
WS: [ \t\r\n]+ -> skip;
