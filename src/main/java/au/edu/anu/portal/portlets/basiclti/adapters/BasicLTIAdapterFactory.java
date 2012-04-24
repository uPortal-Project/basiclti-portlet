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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class to get an instance of the required IBasicLTIAdapter implementation
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class BasicLTIAdapterFactory {

	private final static Log log = LogFactory.getLog(BasicLTIAdapterFactory.class);

	/**
	 * Instantiate the desired implementation
	 * 
	 * @param className		name of class
	 * @return
	 */
	public IBasicLTIAdapter newAdapter(String className) {
		
		IBasicLTIAdapter adapter = null;
	    try {
	    	adapter = (IBasicLTIAdapter) Class.forName(className).newInstance();    
	    } catch (Exception e) {
	    	log.error("Error instantiating class: " + e.getClass() + ":" + e.getMessage());
	    }
	    return adapter;
	}
}
