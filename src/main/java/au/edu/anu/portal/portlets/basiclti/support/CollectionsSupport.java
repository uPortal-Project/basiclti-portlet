package au.edu.anu.portal.portlets.basiclti.support;

import java.util.Map;

/**
 * Java.util.Collections utility class. Does a few miscellaneous tasks
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class CollectionsSupport {

	/**
	 * Print all key-value pairs of a map to stdout
	 * @param map
	 */
	public static void printMap(Map<?,?> map) {
		for (Map.Entry<?,?> param : map.entrySet()) {
			System.out.println(param.getKey() + ":" + param.getValue());
		}
	}
	
}
