/*

YOU WILL NEED TO CODE SOME OF THE METHODS IN THIS CLASS.
Basically, all the methods for the grammar variables except the method for
<S>.


This is the class that provides the parsing service.

It is passed a CFScanner to use in the constructor and that is
the CFScanner it will use while it exists.

The only externally exposed method that is called  is parseNext, which either
returns a CFExp object based on the parse of a prefix of the file used by the
CFScanner, or throws an exception.  If it returns a CFExp object, the scanner
is left at the EOS token ($).

You should not modify the S method, but you will need to code all of the
methods for the other grammar variables.

Generally, the rules for a variable will be of the form

<VAR> ::= rhs1 | rhs2 | ... | rhsn

and your method for VAR will have the structure

private returnTypeForVAR VAR(){
   
   if (lookahead is not in the set for VAR)
      throw an exception w a message for that set using the
      private static String getErrorMessage(String methodName, Set<Integer> tokenIds, CFScanner sc)
      method, where tokenIds is the set of tokens for VAR
   else
      if (lookahead is one of the ones in the lookahead set for rhs1)
         process rhs1;
      else if (lookahead is one of the ones in rhs2)
         process rhs2;
      ...
      else // must be in the last lookahead set
         process rhsn;
         
}

The sets for each grammar variable are given in CFToken.  If the variable's
name is NAME, then CFToken.NAMESet will contain the lookaheads.

If rhsj is of the form

X1 X2 ... Xm

where each Xi is either a variable or a token, then they are dealt with
sequentially.  Each Xi will have a scrap of code.  If Xi is a variable, then
there will be a call to the method for the variable which should return some 
result to be used.

If Xi is a token then the lookahead should be checked for a match with the 
token.  If it does not match, an exception should be thrown with the message from
a call to

private static String getErrorMessage(String methodName, int id, CFScanner sc){
   
where methodName is the name of this variable's method (itself),int id is the 
id for the expected token, and CFScanner is the CFScanner object being used.

If it matches, and If Xi is an information bearing token, such as a NAT or
an ID, or a specific operator, the information it carries should be
noted (there is a getTokenString method for obtaining the actual string of
the NAT or ID instances).  Punctuation tokens are usually just to help with
the parse.  All tokens should be consumed by their code fragment, that is,
the CFScanner's consume method should be called to advance the lookahead
to the next token.

The overall control structure of the parser is based on the grammar, which 
we give in full here with lookahead sets for each rhs.  The rules for each variable
are also given below with the method for the variable.  The tokens, which are
given as upper case identifiers , are described in CFToken.java
Generally speaking, for an input token sequence that is in the language
generated by the grammar, the parser will excecute code according to a
preorder traversal of the parse tree, but the conversion of some 
recursive replacement rules to iterations does violate that.

<S> is the start symbol.

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


**********************************************************************************/
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
public class CFExpParser{
   // this scanner object will be used repeatedly;
   // it does not appear to work consistently in between calls when it is bound to 
   // standard in by the driver;
   private static CFScanner lex;
   
      
   public CFExpParser(CFScanner sc) throws Exception{
      
      if (sc == null)
         throw new Exception("error in CFExpParser constructor : scanner sc is null");
      else
         lex = sc;
   }
      
   /*
   
   Builds the error message when there is a set of tokens that are expected.
   
   
      
   ***********************************************************************/ 
   private static String getErrorMessage(String methodName, Set<Integer> tokenIds, CFScanner sc){
   
      StringBuilder bld = new StringBuilder();
      CFToken 
         curr = sc.lookahead(),
         prev = sc.getPrevToken();
         
         
      bld.append("error in method " + methodName + " : unexpected token " + curr);
      
      bld.append("\nis token number " + sc.getCurrTokenNumber());
      if (prev == null)
         bld.append("\nNo previous token.");
      else
         bld.append("\nprevious token was " + prev);
      bld.append("\ncurrent line number is " + sc.getCurrLineNum());
      bld.append("\nposition after the current token is " + sc.getCurrPos());
      bld.append(         "\nExpected tokens are : ");
      for (Integer i : tokenIds){
         bld.append(CFToken.TOKEN_LABELS[i]);
         bld.append(' ');
      }
      return bld.toString();
   }
   
   /*
   
   Builds the error message when there is a single token that is expected.   
      
   ***********************************************************************/ 
   private static String getErrorMessage(String methodName, int id, CFScanner sc){
   
      StringBuilder bld = new StringBuilder();
      CFToken 
         curr = sc.lookahead(),
         prev = sc.getPrevToken();
         
         
      bld.append("error in method " + methodName + " : unexpected token " +
      curr);
      bld.append("\nis token number " + sc.getCurrTokenNumber());
      if (prev == null)
         bld.append("\nNo previous token.");
      else
         bld.append("\nprevious token was " + prev);
      bld.append("\ncurrent line number is " + sc.getCurrLineNum());
      bld.append("\nposition after the current token is " + sc.getCurrPos());
      bld.append(         "\nExpected token is " + CFToken.TOKEN_LABELS[id]);
      return bld.toString();
   }
   /*
   
   parse the next expression and return it or throw an exception
   
   */
   public CFExp parseNext() throws Exception{
      if (lex.getAtEOF())
         throw new Exception("error in perseNext : scanner is at end of file at start of parse");
      else
         return S();
   }
   /*
   
   The grammar rule is
   
   <S> ::= <E> EOS
   
   but we will not consume the EOS symbol so we can put all the
   expressions in a single file.
     
   */
   
   private CFExp S()throws Exception{
      CFToken tk = lex.lookahead();
      int tkT = tk.getTokenType();
      
      if (CFToken.SSet.contains(tkT)){
         CFExp result = E();
         tk = lex.lookahead();
         tkT = tk.getTokenType();
         if (tkT != CFToken.EOS)
            throw new Exception("error in S method : expression is not follows by $");
         else
            return result;
      }
      else
         throw new Exception(getErrorMessage("S", CFToken.SSet, lex));
      
      
   }
   /*
   
   YOU MUST CODE THE REMAINING METHODS FOR THE GRAMMAR VARIABLES.
   
   The general scheme is based on the replacement rules for the variable
   and is described above.
   
   **************************************************************************/
   
   /*
   
   YOU MUST CODE THIS
   
   The grammar rule is
   
   
   <E> ::= LET <BLIST> IN <E> ENDLET |
           IF <TEST> THEN <E> ELSE <E> ENDIF |
           <E> SYMMETRICDIFF <A> | <A>
   convert the last two righthand sides to   <A>(SYMMETRICDIFF <A>)*
   Lookahead sets
   LET | IF | LEFTPAREN, COMPLEMENT, CMP, LEFTBRACE, ID
   
   return the appropriate CFExp object or throw an exception.
   
   *******************************************************************************/
   private CFExp E() throws Exception{
      return null;
      
            
   }
   
   /*
   
   YOU MUST CODE THIS
   
   <A> ::= <A> SETDIFF <B> | <B>
   convert to  <B> (SETDIFF <B>)*


   return the appropriate CFExp object or throw an exception.
   
   *************************************************************************/
   private CFExp A() throws Exception{
      
      return null;
      
      
   }

   /*
   
   YOU MUST CODE THIS
   
   <B> ::= <B> UNION <C> | <C>
   convert to  <C> (UNION <C>)*


   return the appropriate CFExp object or throw an exception.
   
   *************************************************************************/
   private CFExp B() throws Exception{
      
      return null;
      
      
   }


   /*
   
   YOU MUST CODE THIS
   
   <C> ::= <D> INTERSECTION <C> | <D>
   by Arden's lemma, this means <C> = (<D> INTERSECTION)*<D>
   but intersection is associative, so we can convert to
   <D>(INTERSECTION <D>)*


   return the appropriate CFExp object or throw an exception.
   
   *************************************************************************/
   private CFExp C() throws Exception{
      
      return null;
      
      
   }


   /*
   
   YOU MUST CODE THIS
   
   <D> ::= COMPLEMENT <D> | ID | <CONST> | LEFTPAREN <E> RIGHTPAREN
   Lookahead sets
   COMPLEMENT | ID | CMP, LEFTBRACE | LEFTPAREN
   
   You can convert this to
   
   (COMPLEMENT)*( ID | CMP, LEFTBRACE | LEFTPAREN <E> RIGHTPAREN)
   
   to eliminate the recursive call to D.
   
   Since two complements in a row cancel, it's okay to just count
   the number of complements reduce the number you actually use
   to the remainder on division by 2.
   
   
   return the appropriate CFExp object or throw an exception.

   *************************************************************************/
   private CFExp D() throws Exception{
      
      return null;
            
   }

   /*
   
   YOU MUST CODE THIS
   
   <CONST> ::= LEFTBRACE <SET INTERIOR> RIGHTBRACE | CMP LEFTBRACE <SET INTERIOR> RIGHTBRACE
   Lookahead sets
   LEFTBRACE | CMP


   return the appropriate CofinFin object or throw an exception.
   
   *************************************************************************/
   private CofinFin CONST() throws Exception{
      
      return null;
            
   }

   /*
   
   YOU MUST CODE THIS
   
   <SET INTERIOR> ::= "" | <NE SET INTERIOR>
   Lookahead sets
   RIGHTBRACE | NAT
   
   
   return the appropriate int[]  object or throw an exception.
   
   Note
   
   new int[0]
   
   is fine for the empty string alternative.

   *************************************************************************/
   private int[] SETINTERIOR() throws Exception{
      return null;
      
   }

   /*
   
   YOU MUST CODE THIS
   
   <NE SET INTERIOR ::= NAT | NAT COMMA <NE SET INTERIOR>
                       
   which converts to  NAT (COMMA NAT)*
   Lookahead sets
   NAT


   return the appropriate List<Integer> object for the sequence of NAT instances
   or throw an exception.
   
   *************************************************************************/
   private List<Integer> NESETINTERIOR() throws Exception{
      return null;
      
   }

   /*
   
   YOU MUST CODE THIS
   
   <TEST> ::= <E> <TEST SUFFIX>
   Lookahead sets
   (union of the lookahead sets for rhs's of <E>)
   
   We'll have it return an Object[] res of size 3
   
   res[0] is the first expression, for <E>, of type CFExp
   res[1] is Integer, token type of the relational operator in <TEST SUFFIX>
   res[2] is the second expression of the test, which comes from <TEST SUFFIX>
   

   *************************************************************************/
   private Object[] TEST() throws Exception{
      return null;
      
   }

   /*
   
   YOU MUST CODE THIS
   
   <TEST SUFFIX> ::= SUBSETOF <E> | EQUALS <E>
   Lookahead sets
   SUBSETOF | EQUALS
   
   We'll have it return Object[] res of size 2
   
   res[0] is Integer, the token type of the relational operator
   res[1] is the second expression of the test, the value given in <E>, of type CFExp
   

   *************************************************************************/
   private Object[] TESTSUFFIX() throws Exception{
 
      return null;
      
   }

   /*
   
   YOU MUST CODE THIS
   
   This one is a little trickier.
   
   <BLIST> ::= "" | ID EQUALS <E> SEMICOLON <BLIST>
   Lookahead sets
   IN | ID   
   
   Convert to  (ID EQUALS <E> SEMICOLON)*, which amounts to a
   list of single bindings
   
   ID EQUALS <E> SEMICOLON
   
   Initialize a Map object to the empty (but not null) map.
   
   process each
   
   
   ID EQUALS <E> SEMICOLON
   
   in the list as follows.  Obtain the string of the ID, s, and
   the CFExp the E returns, call it exp.  Then use the current value of
   the Map object to obtain a substituted version of exp (it's okay
   to store that in exp itself), where the substituted version is
   constructed from exp's substitute method using the current value 
   for the map object.   Then install (s, substituted version of exp)
   in the map object.
   
   When you have processed all the single bindings in the list, return
   the final map object.
   
   Of course, throw an exception if you encounter a wrong token.
   
   Note, you might overwrite an earlier binding, for example

   let
      x = {1};
      x =  x U {2};
   in
      x U {3}
   endlet

   would first install (x, {1}) in the map, and then replace it with
   {1} U {2} because the susbtitution would replace the x in x U {2} with
   {1} and then replace the (x, {1}) in the map with (x, {1} U {2}).
   
   
   The reason we have to do a deep copy of what the map has for a
   variable when we perform the substitutions using the map can be
   illiustrated by the expression
   
   let 
      x = x U x;
      y = let
             z = x @ x;
          in 
             z \ z
          endlet;
   in
      y
   endlet
   
   The nested 
   
   let
      z = x @ x
   in 
      z \ z
  endlet
  
  should return the expression
  
  (x @ x) \ (x @ x)
  
  but because of sharing of reference types, if we did not do a
  deep copy of the map entry for z, x @ x, we would actually have
  
  (pointer to a (x @ x) CFExp) \ (pointer to the same (x @ x) CFExp)
  
  and in the recursive substitute method we would encounter that same object
  twice, with a side effect introducing MORE occurrences of x that would be 
  be replaced.  The final result should be
  
  ((x U x) @ (x U x)) \ ((x U x) @ (x U x))
  
  but would instead be
  
  ((x U x) @ (x U x)) \ (((x U x) U (x U x)) @ ((x U x) U (x U x)))
  
  By making a deep copy of the map binding for a variable, we are 
  ensured that there will be no shared subexpressions within an
  expression, so as we substitute at a leaf variable, the effect of 
  replacing it will be local to that leaf position.
  
   *************************************************************************/
   private Map<String, CFExp> BLIST() throws Exception{
      return null;
      
   }
      
}
