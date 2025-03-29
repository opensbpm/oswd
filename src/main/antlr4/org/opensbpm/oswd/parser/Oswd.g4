grammar Oswd;

definition  : process EOF;
process     : 'process' processName version descriptionDef? subject+;
processName : SPACE NAME CRLF;
version     : SPACE* 'version' SPACE INT CRLF;
descriptionDef : SPACE* 'description' SPACE description CRLF;
description : name;

subject     : SPACE* subjectName 'with role' roleName task+;
subjectName : SPACE name SPACE;
roleName    : SPACE name CRLF;

task        : taskName (show | send | receive);
taskName    : SPACE* name SPACE;
show        : 'show' object SPACE* proceed CRLF;
send        : 'send' objectNameReference 'to' subjectNameReference SPACE* proceed CRLF;
objectNameReference : SPACE name SPACE;
subjectNameReference : SPACE name (SPACE+|CRLF);
receive     : 'receive' message+;
message     : objectNameReference proceed CRLF;

object      : objectName attribute+;
objectName  : SPACE name CRLF;

attribute   : SPACE* 'with' attributeName 'as' attributeType required? readonly? CRLF;
attributeName: SPACE name SPACE;
attributeType : SPACE ('bool' | 'number' | 'date' | 'text');
required    : SPACE 'required';
readonly    : SPACE 'readonly';

proceed : 'proceed to' taskNameReference;
taskNameReference : SPACE name SPACE*;

name        : NAME | '"' text '"';
text       : (INT | NAME | SPACE)+;

INT     : [0-9]+ ;
NAME    : [-_0-9a-zA-ZÄÖÜẞäöüß]+ ;
SPACE   : [ ]+ ;
CRLF    : '\r'? '\n' | '\r';
