/*

YOU WILL NEED TO CODE THE eval, substitute, AND deepCopy METHODS OF THIS CLASS.
These are all defined recursively on the CFExp and don't require a lot of code.


For the CofinFin expressions with binary operators.  There are four

union U
intersection @
set difference \
symmetric difference (+)

we will code them in the operator data member by their token ids

The constructors here and elsewhere tests the input so that only valid operators and
expressions can be created.

*************************************************************************/
import java.util.Map;
public class CFBinary extends CFExp{
   
   private int operator;
   private CFExp leftSub, rightSub;
   
   public CFBinary(int op, CFExp sub1, CFExp sub2) throws Exception{
         if (sub1 == null)
            throw new Exception("error in CFBinary constructor : left subexpression is null");
         else if (sub2 == null)
            throw new Exception("error in CFBinary constructor : right subexpression is null");
         else if (!CFToken.BinaryOperators.contains(op))
            throw new Exception("error in CFBinary constructor : operator code " + op +
            " is not the code of a binary operator");
         else{
            operator = op;
            leftSub = sub1;
            rightSub = sub2;
         }
   }
   
   public String toString(){
      String c;
      
      switch (operator){
         
         case CFToken.UNION:
            c = " U ";
            break;
            
            
         case CFToken.INTERSECTION:
            c = " @ ";
            break;
            
         case CFToken.SETDIFF:
            c = " \\ ";
            break;
         
         default:
            c = " (+) ";
            
      }
      
      return "(" + leftSub.toString() + c + rightSub.toString() + ")";
   }
   
   /*
   
   YOU MUST CODE THIS
   
   if env is null 
      throw an Exception if env with message
      "error in eval : env is null"
      
   if there is a variable in the expression tree with no entry in
   env throw an exception with message
   
   "error in eval : variable " + s + " is not bound in the environment"
   
   where s is the variable name (this will be accomplished by the 
   recursive calls)
   
   Otherwise, return the value of the expression obtained by using the
   values given in env
   
   So that the error messages are consistent, the code should recursively
   evaluate the left subexpression before the right subexpression
   
   **********************************************************************/
   
	public CofinFin eval(Map<String, CofinFin> env) throws Exception{
               
         return null;
      }
   
            
	
   /*
   
   YOU MUST CODE THIS
   
   if bindings is null throw an exception with message
   
   "error in substitute : bindings is null"
   
   otherwise return a CFBinary object that is the result of
   calling substitute for the two subexpressions.
   
   Because the old expression will not be needed, it is adequate
   to do the substitution in place on this CFBinary object, that
   is replace the two subexpressions w their substitute results and
   return this.
   
   **********************************************************************/
 
	public  CFExp substitute(Map<String, CFExp> bindings)throws Exception{
      
      return null;
      
   }
   
   /*
   
   YOU MUST CODE THIS
   
   return a deep copy of this expresssion.
   
   *****************************************************************/
	public  CFExp deepCopy(){
     // should never throw an exception

        return null;
     }
 
	
}


