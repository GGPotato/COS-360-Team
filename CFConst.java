/*
	
   YOU SHOULD NOT MODIFY THE CODE IN THIS CLASS.
   
	subclass to hold a constant CofinFin value.
	
	The substitute, eval, and deepCopy methods are quite simple for this
	subclass, so I implement them.
	
******************************************************************/	
import java.util.Map;
public class CFConst extends CFExp{
	
	private CofinFin theVal;
	
	/*
   
   The constructor takes an array and whether the array is
	complemented.
   
   Given the CofinFin constructor, only a valid CofinFin
   object can be created, so the object instance result of this
   constructor is always valid.
   
   ****************************************************************/
	
	public CFConst(boolean isC, int[] vals){
		theVal = new CofinFin(isC, vals);
	}
   
   /*
   
   throw an exception if the input CofinFin object is null, else
   create a CFConst object for it

   ****************************************************************/
   public CFConst(CofinFin c)throws Exception{
      if (c == null)
         throw new Exception("error in CFConst constructor : CofinFin value c is null");
      else
         theVal = c;
   }
   
   public String toString(){
      return theVal.toString();
   }
	
	public CFExp substitute(Map<String,CFExp> bindings){
		// since the constant is a leaf w/o variables, the
		// there is nothing to substitute
		return this;
	}
	
	public CofinFin eval(Map<String, CofinFin> env) throws Exception{
		// just return the value of this leaf
		return theVal;
	}
   
   // CofinFin objects are not modified by substitution,
   // so there is no danger in sharing them; the class only
   // has mutators add and remove that are not used in this
   // application, so they are in effect immutable objects
   public CFExp deepCopy(){
      return this;
   }
      
}

