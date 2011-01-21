package au.edu.anu.portal.portlets.basiclti.adapters;

import java.util.Map;

/**
 * IBasicLTIAdapter
 * 
 * Interface that all Basic LTI adapters must implement.
 * 
 * <p>
 * An adapter is essentially a processor that takes data and processes it according to the requirements of the provider_type. 
 * Each Basic LTI provider_type may have specific requirements. When the portlet is configured, the provider_type is supplied
 * as part of the configuration. The portlet checks this and calls the appropriate adapter.
 * </p>
 * 
 * <p>
 * To create a new adapter, identify the requirements of your provider. 
 * </p>
 * 
 * <p>Things to consider:
 * <ol>
 * <li>Does the URL require any specific data included?</li>
 * <li>Do any special parameters unique to this provider need to be sent in the launch data?</li>
 * <li>Do any parameters need to be adjusted before they are sent?</li>
 * </ol>
 *  
 * <p>If yes, create an adapter that does these things by implementing this class.
 *  You will also need to add some logic to BasicLTIPortlet to add provision for calling this class based on the provider_type.</p>
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public interface IBasicLTIAdapter {
	
	/**
	 * Processes the launch data map according to the requirements of the specific provider
	 * @param params	map of launch data params
	 * @return	the processes Map
	 * 			
	 */
	public Map<String,String> processLaunchData(Map<String,String> params);
}
