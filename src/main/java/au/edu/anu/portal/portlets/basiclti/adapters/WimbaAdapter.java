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
 * An adapter for connecting to Wimba Classroom (www.wimba.com/products/wimba_classroom)
 * 
 * <p>THIS ADAPTER NEEDS WORK. See docs below.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class WimbaAdapter extends AbstractAdapter {

	private final Log log = LogFactory.getLog(getClass().getName());

	private final String ROLE_INSTRUCTOR = "Instructor";
	private final String ROLE_STUDENT = "Student";

	/**
	 * This adapter needs work. It currently hardcodes the 'roles' attribute to Instructor. You will need to provide some logic
	 * so that the roles can be looked up here
	 * 
	 * @param params	map of launch data params
	 * @return the map, unchanged
	 */
	@Override
	public Map<String,String> processLaunchData(Map<String,String> params){
		
		log.debug("WimbaAdapter.processLaunchData() called");

		/*
		String user_id = params.get("user_id");
		//your logic to check the actual role for the given user should go here
		*/
		
		params.put("roles", ROLE_INSTRUCTOR);
		
		//add defaults
		params.putAll(super.getDefaultParameters());
		
		return params;
	}
	
	
}
