package au.edu.anu.portal.portlets.basiclti.adapters;

import java.util.Map;

/**
 * StandardBasicLTIAdapter
 * 
 * This is the default adapter for processing launch data. It returns the data unchanged, as per a standard Basic LTI provider.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class StandardBasicLTIAdapter implements IBasicLTIAdapter {

	/**
	 * Returns the map of params unchanged, as per a standard Basic LTI request.
	 * 
	 * @param params	map of launch data params
	 * @return the map, unchanged
	 */
	@Override
	public Map<String,String> processLaunchData(Map<String,String> params){
		return params;
	}
	
	
}
