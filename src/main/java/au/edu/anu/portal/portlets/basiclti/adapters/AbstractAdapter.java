package au.edu.anu.portal.portlets.basiclti.adapters;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a base class that all adapters should extend.
 * 
 * It provides common Basic LTI functions to adapters
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public abstract class AbstractAdapter implements IBasicLTIAdapter  {

	/**
	 * Override this to perform your adapter specific manipulations
	 */
	public abstract Map<String, String> processLaunchData(Map<String, String> params);

	/**
	 * Return the common default launch params
	 * @param params
	 * @return
	 */
	protected final Map<String,String> getDefaultParameters() {
		
		Map<String,String> defaultParams = new HashMap<String,String>();
		defaultParams.put("launch_presentation_locale", "en_AU");
		defaultParams.put("lti_message_type", "basic-lti-launch-request");
		defaultParams.put("lti_version", "LTI-1p0");
		defaultParams.put("oauth_callback","about:blank");
		defaultParams.put("basiclti_submit","null");
		
		return defaultParams;
	}
	
	
}
