package relation.progelem;

import java.util.ArrayList;
import java.util.List;

public class AnnotatedFieldDecl {
	public String			superclazz;
	public String			clazz;
	public String			name;
	public String			type;
	public int				startPosition;
	public int				lineNumber;

	public List<String>	normalAnnotationList	= new ArrayList<String>();
	public List<String>	markerAnnotationList	= new ArrayList<String>();

	public AnnotatedFieldDecl(String name, String type, String superclass, String clazz) {
		this.name = name;
		this.type = type;
		this.superclazz = superclass;
		this.clazz = clazz;
	}

	public AnnotatedFieldDecl() {
		/* ^.^ */
	}

	String	lineSep	= System.getProperty("line.separator");

	public void addNormalAnnotationList(String parm)
	{
		normalAnnotationList.add(parm);
	}

	public void addMarkerAnnotationList(String parm)
	{
		markerAnnotationList.add(parm);
	}

	public boolean compare(AnnotatedFieldDecl obj)
	{
		if (this.name.equals(obj.name))
			return true;
		return false;
	}

	public String getSuperclazz()
	{
		return superclazz;
	}

	public void setSuperclazz(String superclazz)
	{
		this.superclazz = superclazz;
	}

	public String getClazz()
	{
		return clazz;
	}

	public void setClazz(String clazz)
	{
		this.clazz = clazz;
	}

	public String getFieldName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public List<String> getNormalAnnotationList()
	{
		return normalAnnotationList;
	}

	public void setNormalAnnotationList(List<String> normalAnnotationList)
	{
		this.normalAnnotationList = normalAnnotationList;
	}

	public List<String> getMarkerAnnotationList()
	{
		return markerAnnotationList;
	}

	public void setMarkerAnnotationList(List<String> markerAnnotationList)
	{
		this.markerAnnotationList = markerAnnotationList;
	}

	public int getStartPosition()
	{
		return startPosition;
	}

	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public String toString()
	{
		String dotline = "\n------------------------------------------";
		String normalAnnotations = "";
		for (String elem : normalAnnotationList)
		{
			normalAnnotations += (elem + " ");
		}
		String markerAnnotations = "";
		for (String elem : markerAnnotationList)
		{
			markerAnnotations += (elem + " ");
		}
		return "   " + //
				"Loc: " + lineNumber + lineSep + //
				"Type: " + type + lineSep + //
				"Name: " + name + lineSep + //
				"Supr: " + superclazz + lineSep + //
				"Clzz: " + clazz + lineSep + //
				"@Normal: " + (normalAnnotations.trim().isEmpty() ? "-" : normalAnnotations.trim()) + lineSep + //
				"@Marker: " + (markerAnnotations.trim().isEmpty() ? "-" : markerAnnotations.trim()) + dotline;

	}

	public static class Util {
		/** @METHOD */
		public static boolean compare(List<AnnotatedFieldDecl> xfieldlist, List<AnnotatedFieldDecl> yfieldlist)
		{
			if (xfieldlist.size() != yfieldlist.size())
				return false;

			int counter = 0;
			for (AnnotatedFieldDecl theFieldDecl : xfieldlist)
			{
				if (contains(yfieldlist, theFieldDecl))
				{
					counter++;
				}
			}
			return (counter == xfieldlist.size());
		}
	}

	/** @METHOD */
	public static boolean contains(List<AnnotatedFieldDecl> fieldlist, AnnotatedFieldDecl pField)
	{
		for (AnnotatedFieldDecl theFieldDecl : fieldlist)
		{
			if (theFieldDecl.compare(pField))
			{
				return true;
			}
		}
		return false;
	}
}
