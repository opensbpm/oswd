grammar Oswd;

definition  : process EOF;
process     : 'process' processName version description? subject+;
processName : SPACE IDENTIFIER CRLF;
version     : SPACE* 'version' SPACE INT CRLF;
description : SPACE* 'description' SPACE IDENTIFIER CRLF;

subject     : SPACE* subjectName 'with role' roleName task+;
subjectName : SPACE IDENTIFIER SPACE;
roleName    : SPACE IDENTIFIER CRLF;

task        : taskName (show | send | receive);
taskName    : SPACE* IDENTIFIER SPACE;
show        : 'show' object proceed;
send        : 'send' SPACE IDENTIFIER SPACE 'to' SPACE IDENTIFIER CRLF proceed;
receive     : 'receive' SPACE message+;
message     : SPACE* IDENTIFIER SPACE proceed;

object      : objectName attribute+;
objectName  : SPACE IDENTIFIER CRLF;

attribute   : SPACE* 'with' attributeName 'as' attributeType required? readonly? CRLF;
attributeName: SPACE IDENTIFIER SPACE;
attributeType : SPACE ('bool' | 'number' | 'date' | 'text');
required    : SPACE 'required';
readonly    : SPACE 'readonly';

proceed : SPACE* 'proceed to' SPACE IDENTIFIER CRLF;

INT         : [0-9]+ ;
IDENTIFIER  : [a-zA-Z0-9]+ ;
SPACE       : [ ]+ ;
CRLF : '\r'? '\n' | '\r';
