/*
 * @(#) FullClassDecl.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.programconstructs;

import java.util.HashMap;
import java.util.Map;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.at.AtTableName;
import metadata.invariant.pbse.transform.AnnoVal;

/**
 * @author John Edstrom
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class FullClassDecl {
	String 		packageName;
	ClassDecl 	classDecl;
	AtTableName atTableName;
	String 		transforms;
	
	public FullClassDecl() {}
	
	public FullClassDecl(String packageName, ClassDecl classDecl, AtTableName atTableName) {
		this.packageName = packageName;
		this.classDecl = classDecl;
		this.atTableName = atTableName;
		
		String classname = this.classDecl.getName();
		String attrVal = this.atTableName.getName();
		Map<String,String> map = new HashMap<String, String>();
		map.put(STR.var_classname, classname);
		AnnoVal annoVal = new AnnoVal(map, attrVal);
		this.transforms = annoVal.getTransformations();
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public ClassDecl getClassDecl() {
		return classDecl;
	}
	
	public AtTableName getAtTableName() {
		return atTableName;
	}
	
	public String getTransforms() {
		return transforms;
	}
}
