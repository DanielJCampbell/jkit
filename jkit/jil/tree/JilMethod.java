package jkit.jil.tree;

import java.util.*;
import jkit.util.Pair;
import jkit.jil.util.*;

public class JilMethod extends SyntacticElementImpl implements jkit.compiler.Clazz.Method {
	private String name;
	private Type.Function type;
	private List<Modifier> modifiers;
	private List<Type.Clazz> exceptions;
	private List<Pair<String, List<Modifier>>> parameters; 
	private List<Stmt> body = new ArrayList<Stmt>();
	
	/**
	 * Construct an object representing a field of a JVM class.
	 * 
	 * @param name -
	 *            The name of the method.
	 * @param type -
	 *            The (fully generic) function type of this method.
	 * @param parameters -
	 *            The names of the parameter variables, in order of their
	 *            appearance. It must hold that parameters.size() ==
	 *            type.parameterTypes().size().
	 * @param modifiers -
	 *            Any modifiers of the method (e.g. public, static, etc)
	 * @param exceptions -
	 *            The (non-null) list of exceptions thrown by this method.
	 */
	public JilMethod(String name, Type.Function type, List<Pair<String,List<Modifier>>> parameters,
			List<Modifier> modifiers, List<Type.Clazz> exceptions,
			Attribute... attributes) {
		super(attributes);
		this.name = name;
		this.type = type;
		this.parameters = parameters;
		this.modifiers = modifiers;
		this.exceptions = exceptions;
	}
	
	/**
     * Construct an object representing a field of a JVM class.
     * 
     * @param name -
     *            The name of the method.
     * @param type -
     *            The (fully generic) function type of this method.
     * @param parameters -
	 *            The names of the parameter variables, in order of their
	 *            appearance. It must hold that parameters.size() ==
	 *            type.parameterTypes().size().
     * @param modifiers -
     *            Any modifiers of the method (e.g. public, static, etc)
     * @param exceptions -
     *            The (non-null) list of exceptions thrown by this method.
     */
	public JilMethod(String name, Type.Function type,
			List<Pair<String, List<Modifier>>> parameters,
			List<Modifier> modifiers, List<Type.Clazz> exceptions,
			List<Attribute> attributes) {
		super(attributes);
		this.name = name;
		this.type = type;
		this.modifiers = modifiers;
		this.exceptions = exceptions;
		this.parameters = parameters;
	}
	
	/**
     * Access the name of this field.  
     * 
     * @return
     */
	public String name() {
		return name;
	}
	
	/**
     * Access the type of this field. This is useful for determining it's
     * package, and/or any generic parameters it declares.
     * 
     * @return
     */
	public Type.Function type() {
		return type;
	}
	
	/**
     * Access the modifiers contained in this method object. The returned list
     * may be modified by adding, or removing modifiers. The returned list is
     * always non-null.
     * 
     * @return
     */
	public List<Modifier> modifiers() { return modifiers; }
	
	/**
	 * Access the names of the parameter variables to this method object. These
	 * are needed to distinguish the other local variables from those which are
	 * parameters.
	 * 
	 * @return
	 */
	public List<Pair<String,List<Modifier>>> parameters() { return parameters; }
	
	/**
     * Access the modifiers contained in this field object. The returned list
     * may be modified by adding, or removing modifiers. The returned list is
     * always non-null.
     * 
     * @return
     */
	public List<Type.Clazz> exceptions() { return exceptions; }
	
	/**
	 * Access the statements that make up the body of this method.
	 * @return
	 */
	public List<Stmt> body() { return body; }
	
	/**
     * Check whether this method has one of the "base" modifiers (e.g. static,
     * public, private, etc). These are found in java.lang.reflect.Modifier.
     * 
     * @param modifier
     * @return true if it does!
     */
	public boolean hasModifier(int modifier) {
		for(Modifier m : modifiers) {
			if(m instanceof Modifier.Base) {
				Modifier.Base b = (Modifier.Base) m;
				if(b.modifier() == modifier) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check whether this method is abstract
	 */
	public boolean isAbstract() {
		return hasModifier(java.lang.reflect.Modifier.ABSTRACT);
	}

	/**
	 * Check whether this method is final
	 */
	public boolean isFinal() {
		return hasModifier(java.lang.reflect.Modifier.FINAL);
	}

	/**
	 * Check whether this method is static
	 */
	public boolean isStatic() {
		return hasModifier(java.lang.reflect.Modifier.STATIC);
	}

	/**
	 * Check whether this method is public
	 */
	public boolean isPublic() {
		return hasModifier(java.lang.reflect.Modifier.PUBLIC);
	}

	/**
	 * Check whether this method is protected
	 */
	public boolean isProtected() {
		return hasModifier(java.lang.reflect.Modifier.PROTECTED);
	}

	/**
	 * Check whether this method is private
	 */
	public boolean isPrivate() {
		return hasModifier(java.lang.reflect.Modifier.PRIVATE);
	}

	/**
	 * Check whether this method is native
	 */
	public boolean isNative() {
		return hasModifier(java.lang.reflect.Modifier.NATIVE);
	}

	/**
	 * Check whether this method is synchronized
	 */
	public boolean isSynchronized() {
		return hasModifier(java.lang.reflect.Modifier.SYNCHRONIZED);
	}

	/**
	 * Check whether this method has varargs
	 */
	public boolean isVariableArity() {
		for(Modifier m : modifiers) {
			if(m instanceof Modifier.VarArgs) {				
				return true;				
			}
		}
		return false;
	}
	
	/**
	 * This method determines the set of local variables used within this
	 * method.  Note, this does not included parameters.
	 * 
	 * @return
	 */
	public List<Pair<String,Boolean>> localVariables() {
		HashSet<String> vars = new HashSet<String>();
		HashSet<String> biguns = new HashSet(); 
		
		for(Stmt s : body) {
			if(s instanceof Stmt.Assign) {
				Stmt.Assign a = (Stmt.Assign) s;
				Map<String,Type> env1 = Exprs.localVariables(a.lhs());
				Map<String,Type> env2 = Exprs.localVariables(a.rhs());
				vars.addAll(env1.keySet());
				vars.addAll(env2.keySet());
				for(Map.Entry<String,Type> e : env1.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}				
				for(Map.Entry<String,Type> e : env2.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}
				
			} else if(s instanceof Stmt.Return) {
				Stmt.Return a = (Stmt.Return) s;
				if(a.expr() != null) {
					Map<String,Type> env = Exprs.localVariables(a.expr());
					vars.addAll(env.keySet());
					for(Map.Entry<String,Type> e : env.entrySet()) {
						if (e.getValue() instanceof Type.Double
								|| e.getValue() instanceof Type.Long) {
							biguns.add(e.getKey());
						}
					}	
				}
			} else if(s instanceof Stmt.Throw) {
				Stmt.Throw a = (Stmt.Throw) s;				
				Map<String,Type> env = Exprs.localVariables(a.expr());
				vars.addAll(env.keySet());
				for(Map.Entry<String,Type> e : env.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}
			} else if(s instanceof Stmt.Lock) {
				Stmt.Lock a = (Stmt.Lock) s;
				Map<String,Type> env = Exprs.localVariables(a.expr());
				vars.addAll(env.keySet());
				for(Map.Entry<String,Type> e : env.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}
			} else if(s instanceof Stmt.Unlock) {
				Stmt.Unlock a = (Stmt.Unlock) s;
				Map<String,Type> env = Exprs.localVariables(a.expr());
				vars.addAll(env.keySet());
				for(Map.Entry<String,Type> e : env.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}
			} else if(s instanceof Stmt.IfGoto) {
				Stmt.IfGoto a = (Stmt.IfGoto) s;
				Map<String,Type> env = Exprs.localVariables(a.condition());
				vars.addAll(env.keySet());
				for(Map.Entry<String,Type> e : env.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}
			} else if(s instanceof JilExpr.Invoke) {
				JilExpr.Invoke a = (JilExpr.Invoke) s;
				Map<String,Type> env = Exprs.localVariables(a);
				vars.addAll(env.keySet());
				for(Map.Entry<String,Type> e : env.entrySet()) {
					if (e.getValue() instanceof Type.Double
							|| e.getValue() instanceof Type.Long) {
						biguns.add(e.getKey());
					}
				}
			}
		}
		
		for(Pair<String,List<Modifier>> p : parameters) {
			vars.remove(p.first());
		}
		
		vars.remove("this"); // these are implicit
		vars.remove("super"); // these are implicit 
		
		ArrayList<Pair<String,Boolean>> r = new ArrayList();
		
		for(String var : vars) {
			if(biguns.contains(var)) {
				r.add(new Pair(var,true));
			} else {
				r.add(new Pair(var,false));
			}
		}
		
		return r;
	}

}
