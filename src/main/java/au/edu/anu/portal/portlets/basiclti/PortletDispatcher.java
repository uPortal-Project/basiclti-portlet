package au.edu.anu.portal.portlets.basiclti;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.edu.anu.portal.portlets.basiclti.helper.SakaiWebServiceHelper;
import au.edu.anu.portal.portlets.basiclti.logic.SakaiWebServiceLogic;
import au.edu.anu.portal.portlets.basiclti.models.Site;
import au.edu.anu.portal.portlets.basiclti.support.HttpSupport;
import au.edu.anu.portal.portlets.basiclti.utils.Constants;
import au.edu.anu.portal.portlets.basiclti.utils.Messages;



public class PortletDispatcher extends GenericPortlet{

	private final Log log = LogFactory.getLog(getClass().getName());
	
	// pages
	private String viewUrl;
	private String editUrl;
	private String proxyUrl;
	private String errorUrl;
	
	// params
	private String key;
	
	// local
	private boolean replayForm;
	private boolean isValid;

		
	
	public void init(PortletConfig config) throws PortletException {	   
	   super.init(config);
	   log.info("Basic LTI PortletDispatcher init()");
	   
	   //get pages
	   viewUrl = config.getInitParameter("viewUrl");
	   editUrl = config.getInitParameter("editUrl");
	   proxyUrl = config.getInitParameter("proxyUrl");
	   errorUrl = config.getInitParameter("errorUrl");

	   //get params
	   key = config.getInitParameter("key");
	   
	}
	
	/**
	 * Process any portlet actions
	 */
	public void processAction(ActionRequest request, ActionResponse response) {
		
		if(request.getPortletMode().equals(PortletMode.EDIT)) {
			replayForm = false;
			isValid = false;
			
			//get prefs and submitted values
			PortletPreferences prefs = request.getPreferences();
			String portletHeight = request.getParameter("portletHeight");
			String portletTitle = request.getParameter("portletTitle");
			
			
			//portlet title could be blank, set to default
			if(StringUtils.isBlank(portletTitle)){
				portletTitle=Constants.PORTLET_TITLE_DEFAULT;
			}
			
			//form ok so validate
			try {
				prefs.setValue("portletHeight", portletHeight);
				prefs.setValue("portletTitle", portletTitle);
			} catch (ReadOnlyException e) {
				response.setRenderParameter("errorMessage", Messages.getString("error.form.readonly.error"));
				log.error(e);
			}
			
			//save them
			try {
				prefs.store();
				isValid=true;
			} catch (ValidatorException e) {
				response.setRenderParameter("errorMessage", e.getMessage());
				log.error(e);
			} catch (IOException e) {
				response.setRenderParameter("errorMessage", Messages.getString("error.form.save.error"));
				log.error(e);
			}
			
			//if ok, return to view
			if(isValid) {
				try {
					response.setPortletMode(PortletMode.VIEW);
				} catch (PortletModeException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	
	/**
	 * Render the main view
	 */
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		log.info("Basic LTI doView()");
		
		//get data
		Map<String,String> launchData = getLaunchData(request, response);
		
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
		request.setAttribute("preferredHeight", getPreferredPortletHeight(request));
		
		dispatch(request, response, viewUrl);
	}	
		
	/**
	 * Render the edit page
	 */
	protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		log.info("Basic LTI doEdit()");
		
		
		
	
		//do we need to replay the form? This could be due to an error, or we need to show the lists again.
		//if so, use the original request params
		//otherwise, use the preferences
		if(replayForm) {
			request.setAttribute("preferredPortletHeight", request.getParameter("portletHeight"));
			request.setAttribute("preferredPortletTitle", request.getParameter("portletTitle"));
			request.setAttribute("errorMessage", request.getParameter("errorMessage"));
		} else {
			request.setAttribute("preferredPortletHeight", getPreferredPortletHeight(request));
			request.setAttribute("preferredPortletTitle", getPreferredPortletTitle(request));
		}
		dispatch(request, response, editUrl);
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
	 * Setup the Map of params for the request
	 * @param request
	 * @param response
	 * @return Map of params or null if any required data is missing
	 */
	private Map<String,String> getLaunchData(RenderRequest request, RenderResponse response) {
		
		//TODO need to get the data
		//maybe just deserialise the XML back out into an object?
		//need to send across all valid basic LTI fields as we don't know what provider we are connecting to.
		
		
		//get user info
		Map<String,String> userInfo = getUserInfo(request);
		
		//setup launch map
		Map<String,String> props = new HashMap<String,String>();

		//optional fields - we don't need this since we are a truster consumer
		//props.put("lis_person_sourcedid","school.edu:user");
		//props.put("roles","Instructor");
		//props.put("context_title","Design of Personal Environments");
		//props.put("context_label","SI182");
		//props.put("tool_consumer_instance_description", "Australian National University");
		
		//required fields
		props.put("user_id", userInfo.get("username"));
		props.put("lis_person_name_given", userInfo.get("givenName"));
		props.put("lis_person_name_family", userInfo.get("sn"));
		props.put("lis_person_name_full", userInfo.get("displayName"));
		props.put("lis_person_contact_email_primary", userInfo.get("email"));
		props.put("resource_link_id", getPortletNamespace(response));
		//props.put("context_id", preferredRemoteSiteId);
		props.put("tool_consumer_instance_guid", key);
		props.put("lti_version","LTI-1p0");
		props.put("lti_message_type","basic-lti-launch-request");
		props.put("oauth_callback","about:blank");
		props.put("basiclti_submit", "Launch Endpoint with BasicLTI Data");
		//props.put("user_id", remoteUserId);
		
		//additional fields
		//props.put("remote_tool_id", preferredRemoteToolId);
		
		return props;
	}
	
	
	/**
	 * Get the preferred portlet height if set, or default from Constants
	 * @param request
	 * @return
	 */
	private int getPreferredPortletHeight(RenderRequest request) {
	      PortletPreferences pref = request.getPreferences();
	      return Integer.parseInt(pref.getValue("portletHeight", String.valueOf(Constants.PORTLET_HEIGHT_DEFAULT)));
	}
	
	/**
	 * Get the preferred portlet title if set, or default from Constants
	 * @param request
	 * @return
	 */
	private String getPreferredPortletTitle(RenderRequest request) {
		PortletPreferences pref = request.getPreferences();
		return pref.getValue("portletTitle", Constants.PORTLET_TITLE_DEFAULT);
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
		return getPreferredPortletTitle(request);
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
