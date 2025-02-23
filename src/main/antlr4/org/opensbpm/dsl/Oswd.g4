grammar Oswd;

definition  : process EOF;
process     : 'process' SPACE IDENTIFIER CRLF version description? subject+;
version     : SPACE* 'version' SPACE INT CRLF;
description : SPACE* 'description' SPACE IDENTIFIER CRLF;
subject     : SPACE* IDENTIFIER SPACE 'with role' SPACE IDENTIFIER CRLF task+;
task        : SPACE* IDENTIFIER SPACE (show | receive | send) CRLF;
show        : 'show' SPACE IDENTIFIER;
receive     : 'receive' SPACE IDENTIFIER;
send        : 'send' SPACE IDENTIFIER;


INT         : [0-9]+ ;
IDENTIFIER  : [a-zA-Z0-9]+ ;
SPACE       : [ ]+ ;
CRLF : '\r'? '\n' | '\r';
