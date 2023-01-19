/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
 * <p>If yes, create an adapter that does these things by extending AbstractAdapter (which implement this class for you) and wire it up in portlet.xml.</p>
 * <p>Note: You must always add the default set of Basic LTI parameters from AbstractAdapter</p>
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
