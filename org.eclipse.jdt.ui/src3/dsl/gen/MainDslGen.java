/*
 * @(#) MainGen.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl.gen;

/**
 * @author Myoungkyu Song
 * @date Aug 11, 2011
 * @since JDK1.6
 */
public class MainDslGen{

	// /** @METHOD */
	// public MainPbseGen() {
	// super();
	//
	// CodeGeneratingVisitor codeGenVisitor = new CodeGeneratingVisitor();
	// System.out.println("[DBG] Auto-Generated PBSE Specification.");
	// System.out.println("------------------------------------------");
	//
	// codeGenVisitor.fileopen();
	// accept(codeGenVisitor);
	// codeGenVisitor.fileclose();
	//
	// System.out.println("------------------------------------------");
	// System.out.println("[DBG] >>> Done.");
	// System.out.println("==========================================");
	// }

	// /** @METHOD */
	// public void accept(CodeGeneratingVisitor visitor) {
	//
	// if (relationContains("class") || relationVarContains("class")) {
	// new ClazzRefNode().accept(visitor);
	// }
	// else if (relationContains("method")) {
	// new MethodRefNode().accept(visitor);
	// }
	// else if (relationContains("field")) {
	// new FieldRefNode().accept(visitor);
	// }
	//
	// new WhereQueryNode().accept(visitor);
	// new WhereBodyNode().accept(visitor);
	// }

	/** @METHOD */
	public static void main(String[] args) {
		// new MainPbseGen();
		new DSLGen().pbseGenProc();
	}
}

/*
 * >>> adito <<<
 * 
 * Class c in p
 * Where(public class *)
 * ' c += @form
 * ' @form.name = LOWFIRSTCHAR($CLASS-NAME)
 * 
 * MyPBSE<Package p>
 * Class c in p
 * Where(public class *)
 * ' @Metadata.FORM-NAME += LOWFIRSTCHAR($CLASS-NAME)
 * '
 */

/*
 * >>> wfe <<<
 * 
 * MyPBSE<Package p>
 * Class c in p
 * Where(public *)
 * ' c += @table
 * ' @table.name += PREFIX(JBPM_) + UPPERCASE(c.name)
 * '
 */

/*
 * >>> jEdit <<<
 * 
 * Method m in c
 * Where(m == c.superclass.method)
 * ' m += @Override
 * '
 */

/*
 * >>> seam <<<
 * 
 * Field f in c
 * Where(private * *)
 * ' c += @XmlType
 * ' @XmlType.propOrder -> f.name
 * 
 * Field f in c
 * Where(private * *)
 * ' CONTAINS($METADATA.ATTRVAL, $FIELD)
 * '
 */

/*
 * >>> intellij2 <<<
 * 
 * Field f in c
 * Where(private * *)
 * ' c += @XmlType
 * ' @XmlType.propOrder = f.name
 * 
 * 
 * Field f in c
 * Where(private * *)
 * ' MATCH($METADATA.ATTRVAL, $FIELD)
 * '
 */

/*
 * >>> intellij1 <<<
 * 
 * Field f in c
 * Where(private * *)
 * ' f += @Attribute
 * ' @Attribute.name = f.name
 * 
 * 
 * Field f in c
 * Where(private * *)
 * ' MATCH($ANNOTATION_ATTRIBUTE. NAME-OR-EMPTY, $FIELD_NAME)
 * '
 */

/*
 * >>> mule1 <<<
 * 
 * Method m in c
 * Where(public * *)
 * ' m += @Web*
 * ' @Web*.name = m.returntype
 * 
 * Method m in c
 * Where(@Web* public * *)
 * ' @Web*.name = m.returntype
 * '
 * 
 * Method m in c
 * Where(@Web* public *)
 * ' MATCH($ANNOTATION_ATTRIBUTE.NAME, $METHOD_RETURNTYPE)
 */

/*
 * >>> spring <<<
 * 
 * MyPBSE<Package p>
 * Class c in p
 * Where(public *Configuration)
 * ' c += @Configuration
 * '
 */

/*
 * >>> mule2 <<<
 * 
 * Class c in p
 * Where (public *)
 * ' c += @metadata
 * ' @metadata = c.name
 * '
 * 
 * MyPBSE<Package p>
 * Class c in p
 * Where(public *)
 * ' @Metadata.CLASS-NAME += MATCH($META, $CLASS-NAME)
 * '
 */
