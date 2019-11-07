DISCUSSION OF THE COS 360 TEAM PARSER PROBLEM AND ITS FILES

Due date: 24 November 2019 @ 8 AM

IMPORTANT:

I want you to do the work on GitHub so I can see that everyone on the
team is making a contribution.  Every team has at least one member who
has taken Prof. MacLeod's COS 420 which uses GitHub for that purpose.  
The members with GitHub experience can explain to the others how it works
while I'm still figuring it out.

When you have completed all the coding and are satisfied that your parser
works correctly, please put all the source code for the classes you
defined methods for in an archive file and upload the archive file
to Blackboard.

These are the files for the team predictive parsing exercise which will read
text representations of CofinFin expressions, parse them into CFExp objects,
and then evaluate the results in an environment that associates some variables
to CofinFin values.


We will use operators (from high priority to low)

-  prefix, for absolute complement, that is -S = ({ 0, 1, 2, ... } \ S)

   (rest are all infix)
@  for intersection
U  for union
(+) for symmetric difference

These are comparison operators that can only appear in test
positions.

<=  for the is subset of relation
=   for set equality

We can inductively/recursively define CofinFin expressions as follows.

Basis
  a CofinFin constant is a CofinFin expression
  a variable is a CofinFin expression

If e1, e2, e3, and e4 are four CofinFin expressions then

(-e1)
(e1 @ e2)
(e1 U e2)
(e1 \ e2)
(e1 (+) e2)
(if e1 <= e2 then e2 else e4 endif)
(if e1 = e2 then e2 else e4 endif)

are each CofinFin expressions.

There will also be an expression in the input that gets translated away

let
  x1 = e1;
  x2 = e2;
  ...
  xn = en;
in
  mainExp
endlet

The initial list of xj = expj; items just allows variables to stand for
the expressions in later code.  During the parse, substitutions will be
performed to return an expression that does not have any let...in...endlet
expressions in it.

Java Classes

For the parsing activity

CFExpParser.java    - where the actual parsing takes place
                      YOU WILL NEED TO CODE MULTIPLE METHODS IN THIS CLASS

CFParserDriver.java - for reading the source file of expressions
                      to parse and calling the CFExpParser and
                      displaying the results of the call
CFScanner.java      - for converting the text of the input file into
                      a sequence of tokens
CFToken.java        - defines a class for representing the tokens(terminals)
                      for this application.


For CofinFin expression objects

CFBinary.java      - for expressions using binary operators
CFConditional.java - for if test then exp1 else exp2 endif expressions
CFConst.java       - for constant expressions, like {1,2,3}
CFExp.java         - the abstract class to cover the variations
CFUnary.java       - for the unary complement operaton
CFVar.java         - for expressions that are variables

The only coding you will need to do for is of the
eval, substitute, and deepCopy methods, and only in the classes

CFBinary
CFConditional
CFUnary
CFVar
 
All the methods you need to code are specified in more detail in the classes
where they sit. Some throw exceptions, and you should match the specified messages
perfectly.

There are some initial test cases and "correct" output in

parserInvalidTest.txt - invalid expressions
invalidOut.txt        - result of running those through the driver
parserValidTest.txt   - valid expressions
validOut.txt          - result of running those through the driver

But I will do much more extensive testing of both valid and invalid expressions
when I have time to generate the test cases.  I will certainly supply them to you
when I have them.

The tokens are defined in CFToken.java, but we reproduce that here.
Their types are coded as int values.

   public static final int // for the token types
      SYMMETRICDIFF = 0, //  "(+)"  an operator
      ID = 1, // [a-zA-Z]+[a-zA-Z0-9]* except for reserved words
      LET = 2, // "let"      a reserved word
      ENDLET = 3, // "endlet"    a reserved word
      IF = 4, //  "if"       a reserved word
      ELSE = 5, // "else"     a reserved word
      ENDIF = 6, // "endif"    a reserved word
      NAT = 7, // 0|[1-9][0-9]*  nonnegative integer constant
      LEFTBRACE = 8,    // '{'
      RIGHTBRACE = 9,    // '}'
      LEFTPAREN = 10,    // '('
      RIGHTPAREN = 11,    // ')'
      SEMICOLON = 12,    // ';'
      COMMA =  13,    // ','
      SUBSETOF = 14, //  "<="  for is subset of 
      EQUALS = 15,  // '='   for equality comparisons of sets
      INTERSECTION = 16,  // '@'   for set intersection  
      UNION = 17,  // 'U'   for set union; in effect a reserved word
      SETDIFF = 18, // '\'   for binary set difference
      COMPLEMENT = 19,  // '-'   for unary set complement
      IN  = 20, //  "in"  for let expressions; so "in" is reserved
      THEN = 21, // "then" a reserved word
      CMP = 22, // "CMP" a reserved word
      EOS = 23, // "$" to stand for reaching the end of the string
      EOF = 24, // to signal the end of the file/string has been reached
      UNRECOGNIZED = 25; // for anything else

Note that for NAT and ID and UNRECOGNIZED, the object will also include an
accessible tokentString data member with the actual string value covered by
the token.

The grammar, which is given in CFParser.java, is reproduced here.  The replacement
rules and lookahead sets are given.  The variables are

<S>
<A>
<B>
<C>
<D>
<E>
<CONST>
<SET INTERIOR>
<NE SET INTERIOR>
<TEST>
<TEST SUFFIX>
<BLIST>

<S> is the start symbol.

The replacement rules, with lookahead sets are as follows.  In some places
we use Arden's lemma to convert the recursive spec into an iterative one.

<S> ::= <E> EOS
Lookahead sets
union of the lookahead sets for <E>
LET, IF, LEFTPAREN, COMPLEMENT, CMP, LEFTBRACE, ID

<E> ::= LET <BLIST> IN <E> ENDLET |
        IF <TEST> THEN <E> ELSE <E> ENDIF |
        <E> SYMMETRICDIFF <A> | <A>
converting the last two righthand sides to   <A>(SYMMETRICDIFF <A>)*
so that they become just one rhs, the Lookahead sets are
LET | IF | LEFTPAREN, COMPLEMENT, CMP, LEFTBRACE, ID

Technically, the first sets of <A>, <B>, <C>, and <D> are all the same,
and none of these variables are nullable, so the lookaheads sets for each
of the variables is the same, specifically,

LEFTPAREN, COMPLEMENT, CMP, LEFTBRACE, ID

<D> has several different rhs's that will partition this set of
tokens, but the others all convert to a single rhs of the form

<VAR>(OPERATORTOKEN <VAR>)*

which you process with a loop, roughly

CFExp result = VAR();

while (lookahead is OPERATORTOKEN){
   consume();
   CFExp temp = VAR();
   result = result OPERATORTOKEN temp;
}
e


<A> ::= <A> SETDIFF <B> | <B>
convert to  <B> (SETDIFF <B>)*

<B> ::= <B> UNION <C> | <C>
convert to  <C> (UNION <C>)*


<C> ::= <D> INTERSECTION <C> | <D>
by Arden's lemma, this means <C> = (<D> INTERSECTION)*<D>
but intersection is associative, so we can convert to
<D>(INTERSECTION <D>)*

If the operation really needed to associate to the right,
it would be more complicated to process.


<D> ::= COMPLEMENT <D> | ID | <CONST> | LEFTPAREN <E> RIGHTPAREN
Lookahead sets
COMPLEMENT | ID | CMP, LEFTBRACE | LEFTPAREN

<CONST> ::= LEFTBRACE <SET INTERIOR> RIGHTBRACE | CMP LEFTBRACE <SET INTERIOR> RIGHTBRACE
Lookahead sets
LEFTBRACE | CMP

<SET INTERIOR> ::= "" | <NE SET INTERIOR>
Lookahead sets
RIGHTBRACE | NAT

<NE SET INTERIOR ::= NAT | NAT COMMA <NE SET INTERIOR>
converts to NAT (COMMA NAT)*
Lookahead sets
NAT

<TEST> ::= <E> <TEST SUFFIX>
Lookahead sets
(union of the lookahead sets for rhs's of <E>)

<TEST SUFFIX> ::= SUBSETOF <E> | EQUALS <E>
Lookahead sets
SUBSETOF | EQUALS

<BLIST> ::= "" | ID EQUALS <E> SEMICOLON <BLIST>
Lookahead sets
IN | ID
