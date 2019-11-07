/*

YOU WILL NEED TO CODE THE eval, substitute, AND deepCopy METHODS OF THIS CLASS.
These are all defined recursively on the CFExp and don't require a lot of code.


For the CofinFin expressions with unary operators.  There is just the one,
absolute complement, so we won't include a data member for the operator at
this time.

The constructor tests the input so that only valid expressions can be created.

*************************************************************************/
import java.util.Map;
public class CFUnary extends CFExp{
   
   private CFExp subExp;
   
   public CFUnary(CFExp sb) throws Exception{
         if (sb == null)
            throw new Exception("error in CFUnary constructor : subexpression is null");
         else
            subExp = sb;
   }
   
   public String toString(){
      return "(" + "-" + subExp.toString() + ")";
   }
   
   /*
   
   YOU MUST CODE THIS
   
   if env is null 
      throw an Exception if env with message
      "error in eval : env is null"
      
   else if there is a variable in the expression tree with no entry in
   env throw an exception with message
   
   "error in eval : variable " + s + " is not bound in the environment"
   
   where s is the variable name
   (that will be accomplished in the recursive call)
   
   Otherwise, return the value of the expression obtained by using the
   values given in env and the expression  and the CofinFin complement
   method.
   
   **********************************************************************/
   
	public CofinFin eval(Map<String, CofinFin> env) throws Exception{
      return null;
      
   }
	
   /*
   
   YOU MUST CODE THIS
   
   if bindings is null throw an exception with message
   
   "error in substitute : bindings is null"
   
   otherwise return a CFUnary object that is constructed from this
   one and the result of the recursive call on substitute for the
   subexpression subExp
   
   Because the old value does not need to be preserved, it is
   adequate to replace subExp with its result from substitute,
   and return this.
   
   **********************************************************************/
 
	public  CFExp substitute(Map<String, CFExp> bindings)throws Exception{
      return null;
      
   }
   
   /*
   
   YOU MUST CODE THIS
   
   return a new CFUnary object that is a deep copy of this
   
   ************************************************************************/
	public  CFExp deepCopy(){
      return null;
      
   }
	
}


