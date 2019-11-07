import java.util.*;
/*

YOU SHOULD NOT MODIFY THIS CODE.

A class to represent a token in the CofinFin translation project.

It has data members

public static

1. TOKEN_LABELS: an array of 31 strings you are to use in issuing error messages
   when some particular set of tokens is expected and the next token is not
   in that set

2. PROGRAM, ..., UNRECOGNIZED 31 symbolic ints for naming the token types 
   corresponding to the labels

3. TreeSet<Integer> sets for all of the grammar variables; these are initialized in
   a static initializer block to hold the lookahead types that are lookaheads for
   the variable's replacement rules.  The identifiers use are the grammar variable
   + "Set", for example, setTestSuffixSet.  The TreeSets hold the union of the
   lookahead sets of all the rhs's for the grammar variables.

instance specific private

1. tokenType: an int identifying the kind of token according to the
   scheme given below; note this is not the only scheme possible; we could
   have had BINARY_SET_OPERATOR as a type, for example, and grouped
   intersection, union, and set difference into one kind, using the string
   to distinguish them

2. lineNum: the int >= 1 identifying the line from which this token 
   was drawn; first line of the source is regarded as line 1; EOF should
   have a line number that is one greater than the line number of the
   last line of the file

3. tokenString: a String variable holding the actual token for the token
   types NATCONST, ID, and UNRECOGNIZED

Class Invariants

1. The following are each independently invalid calls of the constructor

   a. line number <= 0
   b. token type not in range 0 to TOKEN_LABELS.length - 1
   c. type is NATCONST, ID, or UNRECOGNIZED but no string is given or
      the given string is null or empty or begins with WS
   d. type is not NATCONST, ID, or UNRECOGNIZED and a string is given

   In any such case, lineNum will be set to -1 and tokenType will be
   set to UNRECOGNIZED and tokenString will be set to "invalidly constructed"

2. if lineNum >= 1 then the token constructed passed the tests above
   and if the token type is not NATCONST, ID, or UNRECOGNIZED, tokenString
   will be "".
   
NOTE ON EOF's LINE NUMBER

The EOF token instance that would be created if the consume method
reaches the end of the file w/o encountering any non-ws characters should
have the line number that is one greater than the last line of the file.
For example, the file with three lines

line1
line2
line3

would have three ID tokens, and the fourth would be EOF, with line number 4.
You can imagine that it sits on a line by itself after everything else in
the file.

********************************************************************************************/
import java.util.regex.Pattern;
public class CFToken implements Comparable<CFToken>{


	
   public static final String[] TOKEN_LABELS = { 

   "symmetric difference\"(+)\"",   "identifier",      "\"let\"",             "\"endlet\"",
   "\"if\"",                         "\"else\"",        "\"endif\"",           "nonnegative integer constant", 
   "leftbrace\"{\"",                 "rightbrace\"}\"", "leftparen\"(\"",      "rightparen\")\"", 
   "semicolon\";\"",                 "comma\",\"",      "subset of\"<=\"",     "equals\"=\"",
   "intersection\"@\"",              "union\"U\"",      "setdifference\"\\\"", "complement\"-\"", 
   "\"in\"",                         "\"then\"",        "\"CMP\"",             "end of string\"$\"",
   "end of file",                    "unrecognized"};

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

   public  static TreeSet<Integer> SSet = new  TreeSet<Integer>();  // for <S>

   public  static TreeSet<Integer> ESet = SSet;      // for <E>; same as for <S>
                                                     // and <TEST>
   

   
   // <A>, <B>, <C>, <D> all have the same lookahead sets
   public  static TreeSet<Integer> ASet = new TreeSet<Integer>();
   public  static TreeSet<Integer> BSet = ASet;
   public  static TreeSet<Integer> CSet = ASet;
   public  static TreeSet<Integer> DSet = ASet;
   
   public  static TreeSet<Integer> TESTSet = SSet;    // for <TEST>
   public  static TreeSet<Integer> TESTSUFFIXSet = new TreeSet<Integer>();    // for <TEST SUFFIX>
   
   public  static TreeSet<Integer> BLISTSet = new TreeSet<Integer>();  // for <BLIST>
   public  static TreeSet<Integer> CONSTSet = new TreeSet<Integer>();       // for <CONST>
   public  static TreeSet<Integer> SETINTERIORSet = new  TreeSet<Integer>();    // for <SET INTERIOR>
   public  static TreeSet<Integer> NESETINTERIORSet = new TreeSet<Integer>();  // for <NE SET INTERIOR>
   public  static TreeSet<Integer> BinaryOperators = new TreeSet<Integer>();  // for <NE SET INTERIOR>
   public static TreeSet<String> ReservedWords = new TreeSet<String>(); // for the reserved words
     
   static{

      // initializing code for the static variables of lookahead sets for the
      // grammar variables

      SSet.add(CFToken.LEFTPAREN);
      SSet.add(CFToken.CMP);
      SSet.add(CFToken.LEFTBRACE);
      SSet.add(CFToken.COMPLEMENT);
      SSet.add(CFToken.ID);
      SSet.add(CFToken.LET);
      SSet.add(CFToken.IF);

      ASet.add(CFToken.LEFTPAREN);
      ASet.add(CFToken.CMP);
      ASet.add(CFToken.LEFTBRACE);
      ASet.add(CFToken.COMPLEMENT);
      ASet.add(CFToken.ID);

      TESTSUFFIXSet.add(CFToken.SUBSETOF);
      TESTSUFFIXSet.add(CFToken.EQUALS);
      
      BLISTSet.add(CFToken.IN);
      BLISTSet.add(CFToken.ID);
      
      CONSTSet.add(CFToken.CMP);      
      CONSTSet.add(CFToken.LEFTBRACE);
      
      SETINTERIORSet.add(CFToken.RIGHTBRACE);
      SETINTERIORSet.add(CFToken.NAT);

      NESETINTERIORSet.add(CFToken.NAT);
      
      ReservedWords.add("U");
      ReservedWords.add("CMP");
      ReservedWords.add("if");
      ReservedWords.add("let");
      ReservedWords.add("then");
      ReservedWords.add("else");
      ReservedWords.add("endif");
      ReservedWords.add("in");
      ReservedWords.add("endlet");
      
      BinaryOperators.add(CFToken.UNION);
      BinaryOperators.add(CFToken.INTERSECTION);
      BinaryOperators.add(CFToken.SYMMETRICDIFF);
      BinaryOperators.add(CFToken.SETDIFF);
      
   }   

   private int
      tokenType;  // uses the constants defined above to name the token

   private String tokenString; // required for nat constants, identifiers, and unrecognized

   public static boolean isReservedWord(String s){
      return ReservedWords.contains(s);
   }
           
   public CFToken(int tkCode){
      if (tkCode < 0 || tkCode >= TOKEN_LABELS.length ||
         tkCode == UNRECOGNIZED   || tkCode == ID || tkCode == NAT){
         tokenType = UNRECOGNIZED;
         tokenString = "invalidly constructed";
      }
      else{
         tokenType = tkCode; // enough for all but NAT, ID, and UNRECOGNIZED        
         tokenString = "";           
      }
   }

   // to handle NAT, ID, UNRECOGNIZED; should only be called with those
   // tokens
   public CFToken(int tkCode, String tkString){
      if (tkString == null || tkString.length() == 0 || Character.isWhitespace(tkString.charAt(0))
         || (tkCode != UNRECOGNIZED && tkCode != ID && tkCode != NAT)  || 
         (tkCode == ID && !tkString.matches("[a-zA-Z][0-9a-zA-Z]*")) ||
         (tkCode == NAT && !tkString.matches("0|[1-9][0-9]*"))){
         tokenType = UNRECOGNIZED;
         tokenString = "invalidly constructed";
      }
      else{
         tokenType = tkCode;
         tokenString = tkString;
      }
   }

   // getters

   public int getTokenType(){
      return tokenType;
   }


   public String getTokenString(){
      return tokenString;
   }

   public String toString(){

      StringBuilder bldr = new StringBuilder();

      bldr.append(TOKEN_LABELS[tokenType]);

      if (tokenType == NAT || tokenType == ID ||
         tokenType == UNRECOGNIZED){
         bldr.append("(\"");
         bldr.append(tokenString);
         bldr.append("\")");
      }
   
      return bldr.toString();

   } 

   public int compareTo(CFToken tk){
      // only compares the types, not the string values for nat, id, 
      // or unrecognized
      return tokenType - tk.tokenType;
   }

   public boolean equals(CFToken tk){
      // only compares the types, not the string values for nat, id, 
      // or unrecognized
      return tokenType == tk.tokenType;
   }

   public static void main(String[] args){

      int i;

      CFToken tk = null;

      // tests for valid tokens
      for (i = 0; i < TOKEN_LABELS.length; i++){
         if (i == NAT)
            tk = new CFToken(i, "123");
         else if (i == ID)
            tk = new CFToken(i, "abc123");
         else if (i == UNRECOGNIZED)
            tk = new CFToken(i, "?notatoken?");
         else
            tk = new CFToken(i);

         System.out.println("CFToken for i = " + i + ": " + tk);
      }
     
      // test for errors
      System.out.println("The rest should be invalid.");

      for (i = 0; i < TOKEN_LABELS.length; i++){
         if (i == NAT)
            ;
         else if (i == ID)
            ;
         else if (i == UNRECOGNIZED)
            ;
         else
            tk = new CFToken(i, "abc");

         System.out.println("CFToken for i = " + i + ": " + tk);
      }
      
   }
}
      
