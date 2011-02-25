package au.edu.anu.portal.portlets.basiclti.adapters;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This is the default adapter for processing launch data. It returns the data unchanged, as per a standard Basic LTI provider.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class StandardAdapter extends AbstractAdapter {

	private final Log log = LogFactory.getLog(getClass().getName());

	
	/**
	 * Returns the map of params unchanged (except for adding the default params), as per a standard Basic LTI request.
	 * 
	 * @param params	map of launch data params
	 * @return the map, unchanged
	 */
	@Override
	public Map<String,String> processLaunchData(Map<String,String> params){
		
		log.debug("StandardAdapter.processLaunchData() called");

		//add defaults
		params.putAll(super.getDefaultParameters());
		
		return params;
	}
	
	
}
