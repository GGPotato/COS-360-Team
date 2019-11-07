/*

YOU WILL NEED TO CODE THE eval, substitute, AND deepCopy METHODS OF THIS CLASS.
These are all defined recursively on the CFExp and don't require a lot of code.


For the CofinFin expressions with the conditional operator.  The
structure is

if set1 relop set2 then set3 else set4

so it has four CFExp data members and one comparison operator, relop, which
we will code as an int, the CFToken token id for it

The constructors here and elsewhere ensure that only valid expressions can
be created.


*************************************************************************/
import java.util.Map;
public class CFConditional extends CFExp{
   
   private int relop;
   private CFExp leftTest, rightTest, trueChoice, falseChoice;
   /*
   
   The constructor tests for null input expressions and legitimate relop values.
   
   ********************************************************************************/
   public CFConditional(int op, CFExp sub1, CFExp sub2, CFExp sub3, CFExp sub4) throws Exception{
         if (sub1 == null)
            throw new Exception("error in CFConditional constructor : left test subexpression is null");
         else if (sub2 == null)
            throw new Exception("error in CFConditional constructor : right test subexpression is null");
         else if (sub3 == null)
            throw new Exception("error in CFConditional constructor : true choice subexpression is null");
         else if (sub4 == null)
            throw new Exception("error in CFConditional constructor : false choice subexpression is null");
         else if (op != CFToken.SUBSETOF && op != CFToken.EQUALS)
            throw new Exception("error in CFConditional constructor : relop code " + op +
            " is not the code of a set comparison relop");
         else{
            relop = op;
            leftTest = sub1;
            rightTest = sub2;
            trueChoice = sub3;
            falseChoice = sub4;
         }
   }
   
   public String toString(){
      
      String op = (relop == CFToken.EQUALS? " = " : " <= ");
      
      return "( " + leftTest.toString() + op + rightTest.toString() + " ? " +
      trueChoice.toString() + " : " + falseChoice.toString() + " )";
   }
   
   
   /*
   
   YOU MUST CODE THIS
   
   if env is null 
      throw an Exception if env with message
      "error in eval : env is null"
      
   if there is a variable in the expression tree with no entry in
   env throw an exception with message
   
   "error in eval : variable " + s + " is not bound in the environment"
   
   where s is the variable name
   
   Otherwise, return the value of the expression obtained by using the
   values given in env
   
   So that the error messages are consistent, the code should recursively
   evaluate the subexpressions in order
   
   leftTest
   rightTest
   
   then perform the comparison indicated by relop and ONLY evaluate
   whichever of the trueChoice and falseChoice subexpressions the
   boolean result selects.  So, for example
   
   if {} = {} then {1} else x endif
   
   if {} = CMP{} then x else {2} endif
   
   should neither of them throw an exception for x being not bound
   to a value
   
   
   **********************************************************************/
   
	public CofinFin eval(Map<String, CofinFin> env) throws Exception{
      return null;
      
   }
   
  
            
	
   /*
   
   YOU MUST CODE THIS
   
   if bindings is null throw an exception with message
   
   "error in substitute : bindings is null"
   
   otherwise return a CFConditional object that is the result of applying
   the substitutions of bindings to this object.
   
   Because the old value is not needed, it is adequate to replace the
   four subexpressions with their substitute results and return this.
      
   **********************************************************************/
 
	public  CFExp substitute(Map<String, CFExp> bindings)throws Exception{
      return null;
      
     
      
   }
   /*
   
   YOU MUST CODE THIS
   
   return a deep copy of this object;
   
   **********************************************************************/
   public CFExp deepCopy(){
      // should never throw an exception
      return null;
      
   }
}


