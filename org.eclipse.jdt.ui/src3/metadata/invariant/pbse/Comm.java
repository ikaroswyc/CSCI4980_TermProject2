/*
 * @(#) Comm.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse;

import java.util.ArrayList;
import java.util.List;

import util.UtilEPlugIn;
import util.UtilFile;

/**
 * @author Myoungkyu Song
 * @date Jan 6, 2011
 * @since JDK1.6
 */
public class Comm
{

	{
		String curProjectPath = UtilEPlugIn.getRefactoredWorkSpace();
		System.out.println(curProjectPath);
	}
	static private String			fs					= System.getProperty("file.separator");
	static private String			commandfile		= UtilEPlugIn.getRefactoredProject() + fs + "backup" + fs + "command_common.txt";
	static private List<String>	targetToBind	= new ArrayList<String>();

	public static boolean isBind()
	{
		if (targetToBind == null || targetToBind.isEmpty())
			return true;
		return false;
	}

	/** @METHOD */
	public static void readTargetToBind()
	{
		ArrayList<String> filebody = UtilFile.fileRead2List(commandfile);

		String target = getGlobalParm("run");
		assert (!target.isEmpty());

		boolean add = false;
		for (int i = 0; i < filebody.size(); i++)
		{
			String line = filebody.get(i);

			if (line.trim().startsWith("//") || (line.trim().isEmpty() && !add))
				continue;
			else if (line.trim().isEmpty() && add)
				break;

			String[] pair = line.split(":");
			if (pair.length != 2)
				continue;

			String key = pair[0].trim();
			String val = pair[1].trim();

			if (key.equals("target") && val.equals(target))
			{
				add = true;
			}
			else if (add)
			{
				targetToBind.add(line.trim());
			}
			else if (targetToBind.isEmpty() == false)
			{
				break;
			}
		}
		assert (!targetToBind.isEmpty());
	}

	/** @METHOD */
	public static List<String> readArgs(String filepath)
	{
		List<String> argList = new ArrayList<String>();
		ArrayList<String> filebody = UtilFile.fileRead2List(filepath);
		for (int i = 0; i < filebody.size(); i++)
		{
			String elem = filebody.get(i);
			if (elem.trim().startsWith("//") || elem.trim().isEmpty())
				continue;
			argList.add(elem);
		}
		return argList;
	}

	/** @METHOD */
	public void readArgs(ArrayList<String> _testdirlist, String arguments_list_file)
	{
		ArrayList<String> list = UtilFile.fileRead2List(arguments_list_file);
		for (int i = 0; i < list.size(); i++)
		{
			String elem = list.get(i);
			if (elem.trim().startsWith("//") || elem.trim().isEmpty())
			{
				continue;
			}
			_testdirlist.add(elem);
		}
	}

	/**
	 * @METHOD the configuration file consists of a couple of lines, in which
	 * each row includes several elements separated by the comma.
	 * @param filepath - the path of the given file parameter
	 */
	public static List<String[]> getParamList(String filepath)
	{
		List<String[]> paramList = new ArrayList<String[]>();
		List<String> filebody = UtilFile.fileRead2List(filepath);

		for (int i = 0; i < filebody.size(); i++)
		{
			String elem = filebody.get(i);
			if (elem.trim().startsWith("//") || elem.trim().isEmpty())
			{
				continue;
			}
			paramList.add(trim(elem.split(",")));
		}
		return paramList;
	}

	/** @METHOD */
	static String[] trim(String[] array)
	{
		for (int j = 0; j < array.length; j++)
			array[j] = array[j].trim();
		;
		return array;
	}

	/** @METHOD */
	public static String getGlobalParm(String pKey)
	{
		String result = "";
		List<String> filebody = UtilFile.fileRead2List(commandfile);
		boolean begin = false;

		for (int i = 0; i < filebody.size(); i++)
		{
			String line = filebody.get(i).trim();

			if (line.startsWith("//") || line.isEmpty())
				continue;

			if (line.startsWith("@begin"))
				begin = true;
			else if (line.startsWith("@end"))
				break;
			else if (begin)
			{
				String[] pair = line.split(":");
				assert (pair.length == 2);
				String key = pair[0].trim();
				String val = pair[1].trim();
				if (key.equals(pKey))
				{
					return val;
				}
			}
		}
		assert (!result.isEmpty());
		return result;
	}

	/** @METHOD */
	public static String getParm(String pKey)
	{
		String result = "";

		for (int i = 0; i < targetToBind.size(); i++)
		{
			String line = targetToBind.get(i);

			if (line.trim().startsWith("//") || line.trim().isEmpty())
				continue;

			String[] pair = line.split(":");
			if (pair.length != 2)
				continue;

			String key = pair[0].trim();
			String val = pair[1].trim();
			if (key.equals(pKey))
			{
				result = val;
				break;
			}
		}
		return result.trim();
	}

	/** @METHOD */
	public static String getAnnotationInCmdline()
	{
		String relation = Comm.getParm(STR.parm_relation);
		String relationTokens[] = relation.split(",");
		assert (relationTokens.length > 2);
		String annotationInCmdline = relationTokens[3].trim();
		return annotationInCmdline;
	}

	/** @METHOD */
	public static String getAttributeInCmdline()
	{
		String relation = Comm.getParm(STR.parm_relation);
		String relationTokens[] = relation.split(",");
		assert (relationTokens.length > 1);
		String attributeInCmdline = relationTokens[2].trim();
		return attributeInCmdline;
	}

}
