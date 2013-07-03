/**
 * Copyright 2010-2013 The Australian National University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package au.edu.anu.portal.portlets.basiclti.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Java.util.Collections utility class. Does a few miscellaneous tasks
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class CollectionsSupport {

	private final static Log log = LogFactory.getLog(CollectionsSupport.class);

	/**
	 * Print all key-value pairs of a map to the log at debug level
	 * @param map
	 */
	public static void printMap(Map<?,?> map) {
		for (Map.Entry<?,?> param : map.entrySet()) {
			log.error(param.getKey() + ":" + param.getValue());
		}
	}
	
	/**
	 * Split a String to a List, based on the given delimiter
	 * @param str			the string
	 * @param delimiter		delimiter to split on
	 * @return
	 */
	public static List<String> splitStringToList(String str, String delimiter) {
		return splitStringToList(str, delimiter, false);
	}
	
	/**
	 * Split a String to a List, based on the given delimiter, and also chomp any new lines that may be present.
	 * @param str			the string
	 * @param delimiter		delimiter to split on
	 * @param chomp			remove new lines as well?
	 * @return
	 */
	public static List<String> splitStringToList(String str, String delimiter, boolean chomp) {
		String[] array = StringUtils.split(str, delimiter);
		if(chomp) {
			//iterate and chomp
			for(int i=0; i<array.length; i++){
				array[i] = StringUtils.strip(array[i]);
			}
		}
		List<String> list = Arrays.asList(array);
		return list;
	}
	
	
	/**
	 * Split a String to a Map, based on the given delimiters, one for the sets and one for each pair
	 * @param str
	 * @param setDelimiter	the delimiter between each set of items
	 * @param kvDelimiter	the delimiter between each item in a pair
	 * @return
	 */
	public static Map<String,String> splitStringToMap(String str, String setDelimiter, String kvDelimiter) {
		return splitStringToMap(str, setDelimiter, kvDelimiter, false);
	}
	
	/**
	 * Split a String to a Map, based on the given delimiters, one for the sets and one for each pair. Can also chomp new lines
	 * @param str
	 * @param setDelimiter	the delimiter between each set of items
	 * @param kvDelimiter	the delimiter between each item in a pair
	 * @param chomp			whether or not to chomp new lines that may separate each pair
	 * @return
	 */
	public static Map<String,String> splitStringToMap(String str, String setDelimiter, String kvDelimiter, boolean chomp) {
		List<String> list = splitStringToList(str, setDelimiter, chomp);
		Map<String,String> map = new HashMap<String,String>();
		for(String pair: list) {
			String[] kv = StringUtils.split(pair, kvDelimiter);
			map.put(kv[0], kv[1]);
		}
		return map;
	}
	
}

