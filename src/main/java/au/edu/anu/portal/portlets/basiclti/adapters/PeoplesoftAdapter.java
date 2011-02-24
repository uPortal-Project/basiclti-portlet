package au.edu.anu.portal.portlets.basiclti.adapters;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This adapter enforces the user_id to be all uppercase, as required by PeopleSoft
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class PeoplesoftAdapter extends AbstractAdapter {

	private final Log log = LogFactory.getLog(getClass().getName());
	
	/**
	 * Modify the map of params to uppercase the user_id and replace it
	 * 
	 * @param params	map of launch data params
	 * @return the map, modified
	 */
	@Override
	public Map<String, String> processLaunchData(Map<String, String> params) {
		
		log.error("PeoplesoftAdapter.processLaunchData() called");
		
		params.put("user_id", StringUtils.upperCase(params.get("user_id")));
		
		//add defaults
		params.putAll(super.getDefaultParameters());
		
		return params;
		
	}

}
