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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An adapter for connecting to Noteflight (www.noteflight.com)
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class NoteflightAdapter extends AbstractAdapter {

	private final Log log = LogFactory.getLog(getClass().getName());

	
	/**
	 * Currently returns the map of params unchanged (except for adding the default params).
	 * 
	 * <p>
	 * Note this is currently equivalent to a standard Basic LTI launch.
	 * This adapter has been added to cater for any custom behaviour that Noteflight may provide in the future.
	 * 
	 * @param params	map of launch data params
	 * @return the map, unchanged
	 */
	@Override
	public Map<String,String> processLaunchData(Map<String,String> params){
		
		log.debug("NoteflightAdapter.processLaunchData() called");

		//add defaults
		params.putAll(super.getDefaultParameters());
		
		return params;
	}
	
	
}
