import java.util.*;
/*

YOU SHOULD NOT MODIFY THIS CODE.


A class to perform the lexical analysis for the translation project.

It contains instance specific data members

1. src: the Scanner origin of the source code text
2. currToken: the current token
3. currLine: a char array of a line from src from which currToken was drawn
4. currPos: the position within currLine from which the next token will be searched for;
      if there is a token in currToken, this will typically be the position right after 
      the last character that was scanned to construct the current token
5. currLineNumber: the index of currLine within the src, with the first line regarded as at index 1
6. atEOF: boolean; true if the Scanner src has reached the end of the file it is reading from;
7. prevToken: the token that preceded the current token, or null if currToken is the
       first token of the expression
8. currTokenNumber: the index of the current token relative to the last time the 
                    scanner was reset; first token is 1, second is 2, etc.

Class invariants

1. src is not null
2. currToken is never null
3. currLine is not null; if there is no next line, it will be set to the empty array
   we can imagine that even an empty file has one line on it that contains the end of file
   marker
4. 0 <= currPos <= currLine.length
5. currLineNumber >= 1
6. atEOF iff currToken is the EOF token

It provides methods

1. getting methods for all data members except src and currLine(the getter for
   currToken is the lookahead method)
2. void consume(): fetches the next token from src into currToken and updates the
   other data members
3. CFToken lookahead(): returns currToken
4. reset; if at end of file this has no effect, else it returns the scanner to an
   initial state in which the previous and current tokens are null and the current 
   token number is 0, and then does a consume call to make the current token the
   next token
   

*****************************************************************************************/
import java.util.regex.Pattern;
public class CFScanner{

   private Scanner src;
   private CFToken
      prevToken,
      currToken;
   // NOTE: NO TOKEN CAN CROSS A LINE BOUNDARY
   private boolean atEOF = false;
   private int
      currTokenNumber,
      currLineNumber,
      currLineLen;
   private char[] currLine = {};
   private int currPos; // 0 <= currPos <= currLine.length
                        // to record where we are on the current line

   // creates an instance of this scanner class from a Java scanner
   public CFScanner(Scanner sc)throws Exception{
      if (sc == null)
         throw new Exception("Null Scanner passed to setScannerSolution constructor.");

      src = sc;
      // load currToken with the first token
      this.consume();
   }

   /*
   
   if not atEOF return to initial state where the tokens are null and the currTokenNumber is 0.
   Only current line number and the Scanner  src are not changed, so it continues to read from
   Scanner src.
   
   This should be called after the complete parse of an expression w/o consuming the
   end of string token ($) that separates expressions.  That way the Scanner src
   will continue from the point AFTER the $.
   
   ***************************************************************************/
   public void reset(){
      if (!atEOF){
         prevToken = currToken = null;
         currTokenNumber = 0;
         consume();
      }
   }
         
   
   // returns the current token w/o advancing
   public CFToken lookahead(){
      return currToken;
   }

   public CFToken getPrevToken(){
      return prevToken;
   }
   
   public int getCurrTokenNumber(){
      return currTokenNumber;
   }
   
   public int getCurrLineNum(){
      return currLineNumber;
   }

   public int getCurrPos(){
      return currPos;
   }

   private void skipWS(){
      boolean onWS = true;

      while (!atEOF && onWS)
         if (currPos == currLineLen)
            if (src.hasNextLine()){
               currLine = src.nextLine().toCharArray();
               currPos = 0;
               currLineLen = currLine.length;
               currLineNumber++;
            }
            else{
               // make the current token EOF
               atEOF = true;
               ++currLineNumber;
               currToken = new CFToken(CFToken.EOF);
               currPos = 0;
               currLine = new char[0];
            }
         else
            if (Character.isWhitespace(currLine[currPos]))
               currPos++;
            else
               onWS = false;
   }

         
   public boolean getAtEOF(){
      return atEOF;
   }

   public void consume(){
   /*

     if the current token is already EOF this
     operation has no effect.

     skip over WS in src until either reaches end of file
     or a non WS char
       
     if reaches eof w/o seeing nonWS loads 
         currToken with EOF
     else
         scans the src from the current non-ws position and
         loads currToken with the longest prefix that
         matches a token definition; if no prefix matches,
         loads currToken with UNRECOGNIZED; UNRECOGNIZED will 
         consume non-ws characters until ws or end of file;

     The thing to be mindful of is when one token's definition
     is in effect a prefix of another's.  Proper prefixes of 
     reserved words and "endlet" and "in" are identifiers, and
     "endlet", "in", and other reserved words, if followed immediately
     by letters or digits are identifiers. No other tokens
     have non-empty common prefixes.

   */
  
      if (!atEOF){  // once we reach eof, nothing more to do
         skipWS();
         if (!atEOF){
            prevToken = currToken;
            currTokenNumber++;
            
            StringBuilder b = new StringBuilder();
            String s;

            char c = currLine[currPos++];

            if (Character.isLetter(c)){
               b.append(c);
               
               while (currPos < currLineLen && 
                      Character.isLetterOrDigit(currLine[currPos]))
                  b.append(currLine[currPos++]);
                  
               s = b.toString();

               // check for reserved words
               if (s.equals("let"))
                  currToken = new CFToken(CFToken.LET);
               else if (s.equals("U"))
                  currToken = new CFToken(CFToken.UNION);
               else if (s.equals("CMP"))
                  currToken = new CFToken(CFToken.CMP);
               else if (s.equals("if"))
                  currToken = new CFToken(CFToken.IF);
               else if (s.equals("else"))
                  currToken = new CFToken(CFToken.ELSE);
               else if (s.equals("endif"))
                  currToken = new CFToken(CFToken.ENDIF);
               else if (s.equals("in"))
                  currToken = new CFToken(CFToken.IN);
               else if (s.equals("then"))
                  currToken = new CFToken(CFToken.THEN);
               else if (s.equals("endlet"))
                  currToken = new CFToken(CFToken.ENDLET);
               else // an identifier
                  currToken = new CFToken(CFToken.ID, s);
            }
            else if (c == '0')
               currToken = new CFToken(CFToken.NAT, "0");
            else if (Character.isDigit(c)){
               b.append(c);

               while (currPos < currLineLen && 
                      Character.isDigit(currLine[currPos]))
                  b.append(currLine[currPos++]);
                  
               s = b.toString();
               currToken = new CFToken(CFToken.NAT, s);
            }
            else // not a letter or a digit;

               switch (c){

                  // deal with single char tokens first
                  case ';' :
                     currToken = new CFToken(CFToken.SEMICOLON);
                     break;
                  case '@' :
                     currToken = new CFToken(CFToken.INTERSECTION);
                     break;
                  case ')' :
                     currToken = new CFToken(CFToken.RIGHTPAREN);
                     break;
                  case '{' :
                     currToken = new CFToken(CFToken.LEFTBRACE);
                     break;
                  case '}' :
                     currToken = new CFToken(CFToken.RIGHTBRACE);
                     break;
                  case '$' :
                     currToken = new CFToken(CFToken.EOS);
                     break;
                  case ',' :
                     currToken = new CFToken(CFToken.COMMA);
                     break;
                  case '=' :
                     currToken = new CFToken(CFToken.EQUALS);
                     break;
                  case '-' :
                     currToken = new CFToken(CFToken.COMPLEMENT);
                     break;
                  case '\\' :
                     currToken = new CFToken(CFToken.SETDIFF);
                     break;
                  case '(':  // if followed by +), SYMMETRICDIFF, else just LEFTPAREN
                     if (currPos < currLineLen - 1 && currLine[currPos] == '+' && currLine[currPos + 1] == ')'){
                        currToken = new CFToken(CFToken.SYMMETRICDIFF);
                        currPos += 2;
                     }
                     else
                        currToken = new CFToken(CFToken.LEFTPAREN);
                     break;
                  case '<':
                     if (currPos < currLineLen && currLine[currPos] == '='){
                        currPos++;
                        currToken = new CFToken(CFToken.SUBSETOF);
                     }
                     else{
                       // construct an UNRECOGNIZED token
                       b.append(c);
                       /*
                          loop invariant:

                          b has all characters from the first non-WS character of this call
                          up to the character at currLine[currPos-1] and all were non-WS and 
                          currPos <= currLineLen

                          we add and advance if we are not at the end of the current line and the
                          current character is still not WS
                       */
                       while (currPos < currLineLen && !Character.isWhitespace((c = currLine[currPos]))){
                          b.append(c);
                          currPos++;
                       }
                       currToken = new CFToken(CFToken.UNRECOGNIZED, b.toString());
                     }
                     break;
                        
                  // drop through to unrecognized;
                  default:
                     { 
                       // construct an UNRECOGNIZED token
                       b.append(c);
                       /*
                          loop invariant:

                          b has all characters from the first non-WS character of this call
                          up to the character at currLine[currPos-1] and all were non-WS and 
                          currPos <= currLineLen

                          we add and advance if we are not at the end of the current line and the
                          current character is still not WS
                       */
                       while (currPos < currLineLen && !Character.isWhitespace((c = currLine[currPos]))){
                          b.append(c);
                          currPos++;
                       }
                       currToken = new CFToken(CFToken.UNRECOGNIZED, b.toString());
                     }
                     break;
               }
         }
      }
   }
   // for testing
   public static void main(String[] args)throws Exception{
      CFToken tk;

      
      CFScanner lex = new CFScanner(new Scanner(System.in));

      int tNum = 0;
      tk = lex.lookahead();
      while (tk.getTokenType() != CFToken.EOF){
         System.out.println("CFToken #" + (++tNum) + " = " + tk);
         lex.consume();
         tk = lex.lookahead();
      }
      System.out.println("CFToken #" + (++tNum) + " = " + tk);
   }
          
   
}

