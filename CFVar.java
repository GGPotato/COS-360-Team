/*

YOU WILL NEED TO CODE THE eval, substitute, AND deepCopy METHODS OF THIS CLASS.

   subclass to hold a variable identifier
   
   
   The variable's string should match the pattern [a-zA-Z][0-9a-zA-Z]*
   and not be a reserved word.  The constructor tests for the string
   being null, matching the pattern, and not being a reserved word.
   
   
   
   
*********************************************************************/
import java.util.Map;
import java.util.regex.*;
import java.util.function.*;

public class CFVar extends CFExp{
	
	
	private String theVar;
	
   /*
   
   The tests ensure the string is a valid identifier for this language.
   
   ************************************************************************/
	public CFVar(String s) throws Exception{
		
		if (s == null)
			throw new Exception("error in constructor for CFVar : input string is null");
		else if (!s.matches("[a-zA-Z][0-9a-zA-Z]*"))
			throw new Exception("error in constructor for CFVar : input string does not match the pattern");
		else if (CFToken.ReservedWords.contains(s))
			throw new Exception("error in constructor for CFVar : input string is a reserved word");
		else
			theVar = s;
	}
   
   public String toString(){
      return theVar;
   }
	
	/*
	
	YOU MUST CODE THIS.
	
	The spec is as follows.
	
	if env is null, throw an exception with the message
	
	"error in eval : environment is null"
	
	Otherwise, look up theVar in env, and if it is there, return the
   CofinFin value associated w it.  If it is not there, or the value stored
	in the map is null, throw an Exception with the message
	
	"error in eval : variable " + theVar + " is not bound in the environment"
	
	
	**********************************************************************/
	public CofinFin eval(Map<String, CofinFin> env) throws Exception{
		return null;
      
	}
	
	/*
	
   YOU MUST CODE THIS
   
   if bindings is null throw an exception with message
   
   "error in substitute : bindings is null"
   
   else if bindings has a non-null entry for theVar, then return a
   deep copy of that entry
   
   else if binding has no non-null entry for theVar, return this
   
	
	**********************************************************************/
	public CFExp substitute(Map<String, CFExp> bindings) throws Exception{
      return null;
      
         
	}
   
   /*
   
   YOU MUST CODE THIS
   
   return a new CFVar object instance with the same theVar value
   as this.
   
   *****************************************************************/
   public CFExp deepCopy(){
      // should never throw an exception
      return null;
      
   }
	
}

   

