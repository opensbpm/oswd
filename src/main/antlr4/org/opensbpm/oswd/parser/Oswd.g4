grammar Oswd;

definition  : process EOF;
process     : 'process' processName version descriptionDef? subject+;
processName : SPACE IDENTIFIER CRLF;
version     : SPACE* 'version' SPACE INT CRLF;
descriptionDef : SPACE* 'description' SPACE description CRLF;
description : IDENTIFIER;


subject     : SPACE* subjectName 'with role' roleName task+;
subjectName : SPACE IDENTIFIER SPACE;
roleName    : SPACE IDENTIFIER CRLF;

task        : taskName (show | send | receive);
taskName    : SPACE* IDENTIFIER SPACE;
show        : 'show' object SPACE* proceed CRLF;
send        : 'send' objectNameReference 'to' subjectNameReference SPACE* proceed CRLF;
objectNameReference : SPACE IDENTIFIER SPACE;
subjectNameReference : SPACE IDENTIFIER (SPACE+|CRLF);
receive     : 'receive' message+;
message     : objectNameReference proceed CRLF;

object      : objectName attribute+;
objectName  : SPACE IDENTIFIER CRLF;

attribute   : SPACE* 'with' attributeName 'as' attributeType required? readonly? CRLF;
attributeName: SPACE IDENTIFIER SPACE;
attributeType : SPACE ('bool' | 'number' | 'date' | 'text');
required    : SPACE 'required';
readonly    : SPACE 'readonly';

proceed : 'proceed to' taskNameReference;
taskNameReference : SPACE IDENTIFIER SPACE*;

INT         : [0-9]+ ;
IDENTIFIER  : [a-zA-Z0-9]+ ;
SPACE       : [ ]+ ;
CRLF : '\r'? '\n' | '\r';
