/**
 * Copyright 2010-2012 The Australian National University
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
		
		log.debug("PeoplesoftAdapter.processLaunchData() called");
		
		params.put("user_id", StringUtils.upperCase(params.get("user_id")));
		
		//add defaults
		params.putAll(super.getDefaultParameters());
		
		return params;
		
	}

}
