package au.edu.anu.portal.portlets.basiclti;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.edu.anu.portal.portlets.basiclti.adapters.BasicLTIAdapterFactory;
import au.edu.anu.portal.portlets.basiclti.adapters.IBasicLTIAdapter;
import au.edu.anu.portal.portlets.basiclti.support.CollectionsSupport;
import au.edu.anu.portal.portlets.basiclti.support.HttpSupport;
import au.edu.anu.portal.portlets.basiclti.support.OAuthSupport;
import au.edu.anu.portal.portlets.basiclti.utils.Constants;
import au.edu.anu.portal.portlets.basiclti.utils.Messages;


/**
 * BasicLTIPortlet
 * 
 * This is the portlet class.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class BasicLTIPortlet extends GenericPortlet{

	private final Log log = LogFactory.getLog(getClass().getName());
	
	// pages
	private String viewUrl;
	private String proxyUrl;
	private String errorUrl;
	
	// params
	private String key;
	private String secret;

	//adapter classes
	private Map<String,String> adapterClasses;
	
	//cache
	private Cache cache;
	private final String CACHE_NAME = "au.edu.anu.portal.portlets.cache.BasicLTIPortletCache";
	
	public void init(PortletConfig config) throws PortletException {	   
	   super.init(config);
	   log.info("Basic LTI Portlet init()");
	   
	   //pages
	   viewUrl = config.getInitParameter("viewUrl");
	   proxyUrl = config.getInitParameter("proxyUrl");
	   errorUrl = config.getInitParameter("errorUrl");

	   //params
	   key = config.getInitParameter("key");
	   secret = config.getInitParameter("secret");

	   
	   //adapter classes
	   adapterClasses = new HashMap<String,String>();
	   adapterClasses.put("standard", config.getInitParameter("standard-adapter-class"));
	   adapterClasses.put("sakai", config.getInitParameter("sakai-adapter-class"));
	   adapterClasses.put("peoplesoft", config.getInitParameter("peoplesoft-adapter-class"));

	   //setup cache
	   CacheManager manager = new CacheManager();
	   cache = manager.getCache(CACHE_NAME);
	   
	}
	
	
	/**
	 * Render the main view
	 */
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		log.info("Basic LTI doView()");
		
		//get data
		Map<String,String> launchData = setupLaunchData(request, response);
		
		//catch - errors already handled
		if(launchData == null) {
			return;
		}
		
		
		//setup the params, serialise to a URL
		StringBuilder proxy = new StringBuilder();
		proxy.append(request.getContextPath());
		proxy.append(proxyUrl);
		proxy.append("?");
		proxy.append(HttpSupport.serialiseMapToQueryString(launchData));
		
		request.setAttribute("proxyContextUrl", proxy.toString());
		request.setAttribute("preferredHeight", getConfiguredPortletHeight(request));
		
		dispatch(request, response, viewUrl);
	}	
			
	/**
	 * Get the current user's details, exposed via portlet.xml
	 * @param request
	 * @return Map<String,String> of info
	 */
	@SuppressWarnings("unchecked")
	private Map<String,String> getUserInfo(RenderRequest request) {
		return (Map<String,String>)request.getAttribute(PortletRequest.USER_INFO);
	}
	
	
	/**
	 * Gets the unique namespace for this portlet
	 * @param response
	 * @return
	 */
	private String getPortletNamespace(RenderResponse response) {
		return response.getNamespace();
	}
	
	/**
	 * Get the appropriate adapter class for this configured portlet instance
	 * @param request
	 * @return
	 */
	private String getAdapterClassName(RenderRequest request) {
		return adapterClasses.get(getConfiguredProviderType(request));
	}
	
	/**
	 * Setup the parameters for the request
	 * @param request
	 * @param response
	 * @return Map of params or null if any required data is missing
	 */
	private Map<String,String> setupLaunchData(RenderRequest request, RenderResponse response) {
		
		Map<String,String> params = new HashMap<String,String>();
		
		//check cache, otherwise form up all of the data
		String cacheKey = getPortletNamespace(response);
		Element element = cache.get(cacheKey);
		if(element != null) {
			log.info("Fetching data from cache for: " + cacheKey);
			params = (Map<String, String>) element.getObjectValue();
		} else {
		
			//get the configured launch data
			String rawLaunchData = getConfiguredLaunchData(request);
			
			//process it to a map
			params = CollectionsSupport.splitStringToMap(rawLaunchData, ";;", "=", true);
			
			//setup adapter
			String adapterClassName = getAdapterClassName(request);
			String providerType = getConfiguredProviderType(request);

			if(log.isDebugEnabled()) {
				log.info("Adapter: " + adapterClassName);
				log.info("ProviderType: " + providerType);
			}
	
			//get user info
			Map<String,String> userInfo = getUserInfo(request);
			
			//add required user fields
			params.put("user_id", userInfo.get("username"));
			params.put("lis_person_name_given", userInfo.get("givenName"));
			params.put("lis_person_name_family", userInfo.get("sn"));
			params.put("lis_person_name_full", userInfo.get("displayName"));
			params.put("lis_person_contact_email_primary", "steve.swinsburg@anu.edu.au");
			
			//add required basic LTI fields
			params.put("resource_link_id", getPortletNamespace(response));
			params.put("tool_consumer_instance_guid", key);
			
			
			//process the params according to the adapter in use
			BasicLTIAdapterFactory factory = new BasicLTIAdapterFactory();
			IBasicLTIAdapter adapter = factory.newAdapter(getAdapterClassName(request));  
			params = adapter.processLaunchData(params);
		
			//cache the data, must be done before signing
			log.info("Adding data to cache for: " + cacheKey);
			cache.put(new Element(cacheKey, params));
			
		}
		
		if(log.isDebugEnabled()) {
			log.debug("Parameter map before OAuth signing");
			CollectionsSupport.printMap(params);
		}
		
		//sign the properties map
		params = OAuthSupport.signProperties(params.get("endpoint_url"), params, "POST", key, secret);

		if(log.isDebugEnabled()) {
			log.debug("Parameter map after OAuth signing");
			CollectionsSupport.printMap(params);
		}
		
		return params;
	}
	
	
	/**
	 * Get the provider type from the preferences/configuration, or default from Constants
	 * @param request
	 * @return
	 */
	private String getConfiguredProviderType(RenderRequest request) {
	      PortletPreferences pref = request.getPreferences();
	      return pref.getValue("provider_type", Constants.DEFAULT_PROVIDER_TYPE);
	}
	
	/**
	 * Get the preferred portlet title if set, or default from Constants
	 * @param request
	 * @return
	 */
	private String getConfiguredPortletTitle(RenderRequest request) {
		PortletPreferences pref = request.getPreferences();
		return pref.getValue("portlet_title", Constants.PORTLET_TITLE_DEFAULT);
	}
	
	/**
	 * Get the preferred portlet height if set, or default from Constants
	 * @param request
	 * @return
	 */
	private int getConfiguredPortletHeight(RenderRequest request) {
	      PortletPreferences pref = request.getPreferences();
	      return Integer.parseInt(pref.getValue("portlet_height", String.valueOf(Constants.PORTLET_HEIGHT_DEFAULT)));
	}
	
	/**
	 * Get the launch data from the preferences/configuration, no default.
	 * @param request
	 * @return
	 */
	private String getConfiguredLaunchData(RenderRequest request) {
		PortletPreferences pref = request.getPreferences();
		return pref.getValue("launch_data", "");
	}
	
	
	/**
	 * Get the current username
	 * @param request
	 * @return
	 */
	private String getAuthenticatedUsername(RenderRequest request) {
		Map<String,String> userInfo = getUserInfo(request);
		return userInfo.get("username");
	}
	
	
	/**
	 * Override GenericPortlet.getTitle() to use the preferred title for the portlet instead
	 */
	@Override
	protected String getTitle(RenderRequest request) {
		return getConfiguredPortletTitle(request);
	}
	
	
	

	/**
	 * Helper to handle error messages
	 * @param messageKey	Message bundle key
	 * @param headingKey	optional error heading message bundle key, if not specified, the general one is used
	 * @param request
	 * @param response
	 */
	private void doError(String messageKey, String headingKey, RenderRequest request, RenderResponse response){
		
		//message
		request.setAttribute("errorMessage", Messages.getString(messageKey));
		
		//optional heading
		if(StringUtils.isNotBlank(headingKey)){
			request.setAttribute("errorHeading", Messages.getString(headingKey));
		} else {
			request.setAttribute("errorHeading", Messages.getString("error.heading.general"));
		}
		
		//dispatch
		try {
			dispatch(request, response, errorUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Dispatch to a JSP or servlet
	 * @param request
	 * @param response
	 * @param path
	 * @throws PortletException
	 * @throws IOException
	 */
	protected void dispatch(RenderRequest request, RenderResponse response, String path)throws PortletException, IOException {
		response.setContentType("text/html"); 
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(path);
		dispatcher.include(request, response);
	}

	
	
	public void destroy() {
		log.info("destroy()");
	}
	
	
}
