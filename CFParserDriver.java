/*

YOU SHOULD NOT MODIFY THIS CODE.

A class to test the CFExpParser and related methods.

Usage is

java CFParserDriver [nameOfFile]

The program will then create a CFScanner from nameOfFile or standard in, if
nameOfFile is not present.  It will repeatedly attempt to parse CFExp instances
from the file and evaluate them using a certain, specific internal map(given below)
with bindings for variables named with the single letters a through r (except l,
which is a bad variable name choice because it looks so much like 1).

The driver steps through the input file using the CFScanner object 
w the loop

while the scanner lookahead is not EOF{
1. attempt to parse the next expression.
2. if the parse succeeds the expression and its value will be displayed
   at standard out and the scanner will be reset for the next expression
3. if the parse fails then the error message is reported and
   tokens are consumed from the scanner until the lookahead is EOS/$ or EOF
   and if the lookaheaed is EOS/$, then the scanner is reset
   
When the loop terminates at EOF, the driver reports the number of expressions
and the counts of valid and invalid expressions.


******************************************************************************/

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class CFParserDriver{
   private static CFScanner lex;
   private static CFExpParser parser;
   private static CFExp exp;
   private static CofinFin result;
   private static HashMap<String, CofinFin> env;
   
   static{
      env = new HashMap<String,CofinFin>();
      env.put("a", new CofinFin(false, new int[] {}));
      env.put("b", new CofinFin(false, new int[] {0}));
      env.put("c", new CofinFin(false, new int[] {1}));
      env.put("d", new CofinFin(false, new int[] {3,2 }));
      env.put("e", new CofinFin(false, new int[] {0,1,23 }));
      env.put("f", new CofinFin(false, new int[] {0,1,234 }));
      env.put("g", new CofinFin(false, new int[] {0,1,2345 }));
      env.put("h", new CofinFin(false, new int[] {56}));
      env.put("i", new CofinFin(true, new int[] {0}));
      env.put("j", new CofinFin(true, new int[] {72}));
      env.put("k", new CofinFin(true, new int[] {0,1,2 }));
      env.put("m", new CofinFin(true, new int[] {0,1,2,3 }));
      env.put("n", new CofinFin(true, new int[] {0,1,2,3,4 }));
      env.put("o", new CofinFin(true, new int[] {0,1,2,3,4,5 }));
      env.put("p", new CofinFin(true, new int[] {0,1,2,3,4,5,6 }));
      env.put("q", new CofinFin(true, new int[] {0,1,2,3,4,5,6,7 }));
      env.put("r", new CofinFin(true, new int[] {}));
   }
      
   
   
   public static void main(String[] args){
      int
         validExpCount = 0,
         invalidExpCount = 0;
         
         try{
            if (args.length == 0)
               lex = new CFScanner(new Scanner(System.in));
            else
               lex = new CFScanner(new Scanner(new File(args[0])));
         }
         catch (Exception e){
            System.out.println("Unable to create the scanner.");
            e.printStackTrace();
            return;
         }
     
      
      try{
         parser = new CFExpParser(lex);
      }
      catch (Exception e){
         System.out.println("Could not create the parser.");
         e.printStackTrace();
         return;
      }
      
      CFToken tk = lex.lookahead();
      int tkT = tk.getTokenType();
      int expNum = 1;
      
      while (tkT != CFToken.EOF){
         try{
            exp = parser.parseNext();
            result = exp.eval(env);
            System.out. println("Expression #" + expNum + " parse was successful.\nIt is "
            + exp.toString() + "\nand it evaluates to \n" + result.toString());
            lex.reset();
            validExpCount++;
         }
         catch(Exception e){
            System.out. println("Expression #" + expNum + " parse failed w message\n{{\n" +
            e.getMessage() + "\n}}\nSkipping to next EOS");
            tk = lex.lookahead();
            tkT = tk.getTokenType();
            invalidExpCount++;
            while (tkT != CFToken.EOF && tkT != CFToken.EOS){
               lex.consume();
               tk = lex.lookahead();
               tkT = tk.getTokenType();
            }
            if (tkT!= CFToken.EOF){
               lex.reset();
            }
         }
         expNum++;
         tkT = lex.lookahead().getTokenType();
      }
      System.out.println("End of file reached.");
      System.out.println("" + (expNum - 1) + " expressions were consumed, " + invalidExpCount + " invalid and "
      + validExpCount + " valid.");
   }
      }