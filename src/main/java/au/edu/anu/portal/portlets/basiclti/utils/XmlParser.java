package au.edu.anu.portal.portlets.basiclti.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Serializer;

/**
 * A collection of methods to parse XML into various lists and objects
 * Ideally these should be genericised toa generic parsing algorithm
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class XmlParser {

	private static final Log log = LogFactory.getLog(XmlParser.class);
	
	
	
	/**
	 * Parse the given XML into a list of Tools. This comes from getPagesAndToolsForSite.
	 * @param xml
	 * @return
	 */
	/*
	public static List<Tool> parseListOfPages(String xml){
		
		log.debug(xml);
		
		Serializer serializer = new Persister();
		List<Tool> tools = new ArrayList<Tool>();
		
		//validate
		if(!isValid(serializer, SitePageList.class, xml)){
			return tools;
		}
		
		//parse
		try {
			SitePageList sitePageList = serializer.read(SitePageList.class, xml);
			List<Page> pages = sitePageList.getPageList().getPages();
			for(Page p: pages) {
				tools.addAll(p.getTools());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tools;
	}
	*/
	
	
	/**
	 * Check if the given XML validates to the given class type
	 * @param serializer
	 * @param clazz
	 * @param xml
	 * @return
	 * @throws Exception 
	 */
	private static boolean isValid(Serializer serializer, Class<?> clazz, String xml){
		
		if(StringUtils.isBlank(xml)){
			return false;
		}
		
		try {
			if(serializer.validate(clazz, xml)) {
				return true;
			}
		} catch (Exception e) {
			log.error("Unable to validate xml: " + e.getClass().getName() + ": " + e.getMessage());
		}
		return false;
	}
}
