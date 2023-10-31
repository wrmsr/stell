grammar Json;


json
    : val
    ;

val
    : obj
    | arr
    | prim
    ;

obj
    : '{' pair (',' pair)* '}'
    | '{' '}'
    ;

pair
    : STR ':' val
    ;

arr
    : '[' val (',' val)* ']'
    | '[' ']'
    ;

prim
    : STR
    | NUM
    | TRUE
    | FALSE
    | NULL
    ;

STR
    : '"' (ESC | SAFECODEPOINT)* '"'
    ;

fragment ESC
    : '\\' (["\\/bfnrt] | UNICODE)
    ;

fragment UNICODE
    : 'u' HEX HEX HEX HEX
    ;

fragment HEX
    : [0-9a-fA-F]
    ;

fragment SAFECODEPOINT
    : ~ ["\\\u0000-\u001F]
    ;

NUM
    : '-'? INT ('.' [0-9] +)? EXP?
    ;

fragment INT
    : '0'
    | [1-9] [0-9]*
    ;

fragment EXP
    : [Ee] [+\-]? INT
    ;

TRUE: 'true';
FALSE: 'false';
NULL: 'null';

WS
    : [ \t\n\r] + -> skip
    ;