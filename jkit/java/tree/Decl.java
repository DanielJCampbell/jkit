package jkit.java.tree;

import java.util.ArrayList;
import java.util.List;

import jkit.java.tree.Type.Clazz;
import jkit.java.tree.Type.Variable;
import jkit.jil.Attribute;
import jkit.jil.Modifier;
import jkit.jil.SyntacticElement;
import jkit.jil.SyntacticElementImpl;
import jkit.util.Triple;

public interface Decl extends SyntacticElement {

	public static class Clazz extends SyntacticElementImpl implements Decl, Stmt {
		private List<Modifier> modifiers;
		private String name;
		private List<Type.Variable> typeParameters;
		private Type.Clazz superclass;
		private List<Type.Clazz> interfaces;
		private List<Decl> declarations;		

		public Clazz(List<Modifier> modifiers, String name,
				List<Type.Variable> typeParameters, Type.Clazz superclass,
				List<Type.Clazz> interfaces, List<Decl> declarations,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.name = name;
			this.typeParameters = typeParameters;
			this.superclass = superclass;
			this.interfaces = interfaces;
			this.declarations = declarations;			
		}

		public List<Modifier> modifiers() {
			return modifiers;
		}

		public String name() {
			return name;
		}

		public List<Type.Variable> typeParameters() {
			return typeParameters;
		}

		public Type.Clazz superclass() {
			return superclass;
		}

		public List<Type.Clazz> interfaces() {
			return interfaces;
		}

		public List<Decl> declarations() { 
			return declarations;
		}
		
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
	}

	public static class Interface extends Clazz {
		public Interface(List<Modifier> modifiers, String name,
				List<Type.Variable> typeParameters, Type.Clazz superclass,
				List<Type.Clazz> interfaces, List<Decl> declarations,
				Attribute... attributes) {
			super(modifiers, name, typeParameters, superclass, interfaces,
					declarations, attributes);
		}
	}

	public static class Enum extends Clazz {
		private List<EnumConstant> constants;

		public Enum(List<Modifier> modifiers, String name,
				List<Type.Clazz> interfaces, List<EnumConstant> constants,
				List<Decl> declarations, Attribute... attributes) {
			super(modifiers, name, new ArrayList<Type.Variable>(), null,
					interfaces, declarations, attributes);
			this.constants = constants;
		}

		public List<EnumConstant> constants() {
			return constants;
		}
	}

	public static class EnumConstant extends SyntacticElementImpl{
		private String name;
		private List<Expr> arguments;
		private List<Decl> declarations;

		public EnumConstant(String name, List<Expr> arguments,
				List<Decl> declarations, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.arguments = arguments;
			this.declarations = declarations;
		}

		public String name() {
			return name;
		}

		public List<Expr> arguments() {
			return arguments;
		}

		public List<Decl> declarations() {
			return declarations;
		}
	}

	public static class AnnotationInterface extends SyntacticElementImpl  implements Decl {
		private List<Modifier> modifiers;
		private String name;
		private List<Triple<Type, String, Value>> methods; 

		public AnnotationInterface(List<Modifier> modifiers, String name,
				List<Triple<Type, String, Value>> methods,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.name = name;
			this.methods = methods;
		}

		public List<Modifier> modifiers() {
			return modifiers;
		}
		public String name() {
			return name;
		}
		public List<Triple<Type, String, Value>> methods() {
			return methods;
		}
	}

	/**
	 * This class stores all known information about a method, including it's
	 * full (possibly generic) type, its name, its modifiers (e.g. public/private
	 * etc), as well as the methods code.
	 * 
	 * @author djp
	 * 
	 */
	public static class Method extends SyntacticElementImpl  implements Decl {
		private List<Modifier> modifiers;
		private String name;
		private Type returnType;
		private List<Triple<String,List<Modifier>,Type>> parameters;
		private List<Type.Variable> typeParameters;
		private List<Type.Clazz> exceptions;
		private Stmt.Block block;

		public Method(List<Modifier> modifiers, String name, Type returnType,
				List<Triple<String, List<Modifier>, Type>> parameters,
				boolean varargs, List<Type.Variable> typeParameters,
				List<Type.Clazz> exceptions, Stmt.Block block,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.returnType = returnType;
			this.name = name;
			this.parameters = parameters;
			if(varargs) {
				modifiers.add(new Modifier.VarArgs());
			}			
			this.typeParameters = typeParameters;
			this.exceptions = exceptions;
			this.block = block;
		}

		public List<Modifier> modifiers() {
			return modifiers;
		}

		public String name() {
			return name;
		}

		public Type returnType() {
			return returnType;
		}

		/**
		 * List of triples (n,m,t), where n is the parameter name, m are the
		 * modifiers and t is the type.
		 * 
		 * @return
		 */
		public List<Triple<String,List<Modifier>,Type>> parameters() {
			return parameters;
		}

		public List<Type.Variable> typeParameters() {
			return typeParameters;
		}

		public List<Type.Clazz> exceptions() {
			return exceptions;
		}

		public Stmt.Block body() {
			return block;
		}
		
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
	}

	/**
	 * A constructor is a special kind of method.
	 * 
	 * @author djp
	 * 
	 */
	public static class Constructor extends Method {
		public Constructor(List<Modifier> modifiers, String name,
				List<Triple<String, List<Modifier>, Type>> parameters, boolean varargs,
				List<Type.Variable> typeParameters,
				List<Type.Clazz> exceptions,
				Stmt.Block block, Attribute... attributes) {			
			super(modifiers, name, null, parameters, varargs, typeParameters,
					exceptions, block,attributes);
		}
	}

	public static class Field extends SyntacticElementImpl implements Decl {
		private List<Modifier> modifiers;
		private String name;
		private Type type;
		private Expr initialiser;

		public Field(List<Modifier> modifiers, String name, Type type,
				Expr initialiser, Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.name = name;
			this.type = type;
			this.initialiser = initialiser;
		}

		public List<Modifier> modifiers() {
			return modifiers;
		}

		public String name() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public Type type() {
			return type;
		}

		public void setType(Type t) {
			this.type = t;			
		}

		public Expr initialiser() {
			return initialiser;
		}		
		
		public void setInitialiser(Expr init) {
			this.initialiser = init;
		}
		
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
			// note, ACC_TRANSIENT is same mask as ACC_VARARGS in vm spec.
			return hasModifier(java.lang.reflect.Modifier.TRANSIENT);
		}
	}

	public static class InitialiserBlock extends Stmt.Block implements Decl {
		public InitialiserBlock(List<Stmt> statements, Attribute... attributes) {
			super(statements,attributes);
		}
	}
	public static class StaticInitialiserBlock extends Stmt.Block implements Decl {
		public StaticInitialiserBlock(List<Stmt> statements, Attribute... attributes) {
			super(statements,attributes);
		}
	}

	
	
	
}	