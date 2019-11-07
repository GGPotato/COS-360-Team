/*

YOU DO NOT NEED TO MODIFY THE CODE OF THIS CLASS(AND SHOULD NOT).

This is the abstract class for all CofinFin expressions.  It will have the following
subclasses

CFConst to hold an actual CofinFin value
   syntactic examples are  {}, {0,1}, CMP{}, CMP{0,1} etc.
   
CFVar to hold a identifier that might occur in an expressions
   examples, s, t, xx, and any string in [a-zA-Z][0-9a-zA-Z]* that is 
   not a reserved word
   
CFUnary to hold expressions with unary operators at the root
   the only unary operator we have is -, the absolute complement
   
CFBinary to hold expressions with binary operators at the root
   the binary operators we have are, from high to low priority
   @  set intersection
   U  set union
   \  set difference
   (+) symmetric difference
   
CFConditional to hold conditional expressions of the form 
   if test then expr1 else expr2 endif
   tests are of the from  expression1 op expression2
   where expression1 and expression2 are CofinFin expressions
   and op is either <= for subset of and = for set equality
   
We do not need a separate subclass for the let ... in expression endlet
expression, because we will apply the substitutions when the expression
is parsed.

The grammar and terminal tokens are more fully described elsewhere.

There are three abstract methods that you have to implement in all of
the subclasses except CFConst.

public abstract CofinFin eval(Map<String, CofinFin> env) throws Exception
   using the association of variables to CofinFin values given in env,
   evaluate this and return the result; throw an Exception if there is 
   a variable in the expression that is not associated with a value in 
   env.
   

public abstract CFExp substitute(Map<String, CFExp> bindings)
   returns an expression that is like the receiver, but variables in
   leaves with entries in the bindings map are replaced with their
   entries.  I originally had hoped to do this as a void method
   as a mutator on the receiver, but if the object instance is CFVar,
   it cannot be changed to some other subclass.  Nonetheless, I think
   it is best to modify the receiver

   modifies the receiver expression by replacing variables occurring at 
   the leaves with the expressions they are associated with in the
   bindings map;
   

public abstract CFExp deepCopy()
   makes a copy of this that has no shared objects with this except possibly
   the CofinFin objects at leaf positions
   
   
All of these methods are defined recursively on the tree structure of
the expressions.

******************************************************************************/
import java.util.Map;
public abstract class CFExp{
	
   // throw an Exception if env is null or there is a variable in the
   // expression that env has no value for
	public abstract CofinFin eval(Map<String, CofinFin> env) throws Exception;
	
   // throw an Exception if bindings is null
	public abstract CFExp substitute(Map<String, CFExp> bindings)throws Exception;
   
   // we will need this to avoid a subtle problem with the let expressions;
   // the intention is to make an entirely separate copy of a CFExp object that
   // has no shared parts with any other, except possibly CofinFin objects, which
   // are immutable
   public abstract CFExp deepCopy(); 
	
}

   

