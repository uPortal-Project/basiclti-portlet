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
	private List<String> allowedTools;
	
	private String adminUsername;
	private String adminPassword;
	private String loginUrl;
	private String scriptUrl;
	
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
	   adminUsername = config.getInitParameter("sakai.admin.username");
	   adminPassword = config.getInitParameter("sakai.admin.password");
	   loginUrl = config.getInitParameter("sakai.ws.login.url");
	   scriptUrl = config.getInitParameter("sakai.ws.script.url");
	   allowedTools = Arrays.asList(StringUtils.split(config.getInitParameter("allowedtools"), ':'));
	   
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
			String remoteSiteId = request.getParameter("remoteSiteId");
			String remoteToolId = request.getParameter("remoteToolId");
			
			//catch a blank remoteSiteId and replay form
			if(StringUtils.isBlank(remoteSiteId)) {
				replayForm = true;
				response.setRenderParameter("portletTitle", portletTitle);
				response.setRenderParameter("portletHeight", portletHeight);
				response.setRenderParameter("remoteSiteId", remoteSiteId);
				//response.setRenderParameter("errorMessage", Messages.getString("error.form.nosite"));
				return;
			}
			
			//catch a blank remoteSiteid and replay form
			if(StringUtils.isBlank(remoteToolId)) {
				replayForm = true;
				response.setRenderParameter("portletTitle", portletTitle);
				response.setRenderParameter("portletHeight", portletHeight);
				response.setRenderParameter("remoteSiteId", remoteSiteId);
				//response.setRenderParameter("errorMessage", Messages.getString("error.form.notool"));
				return;
			}
			
			
			
			//portlet title could be blank, set to default
			if(StringUtils.isBlank(portletTitle)){
				portletTitle=Constants.PORTLET_TITLE_DEFAULT;
			}
			
			//form ok so validate
			try {
				prefs.setValue("portletHeight", portletHeight);
				prefs.setValue("portletTitle", portletTitle);
				prefs.setValue("remoteSiteId", remoteSiteId);
				prefs.setValue("remoteToolId", remoteToolId);
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
		
		//setup the web service bean
		SakaiWebServiceLogic logic = new SakaiWebServiceLogic();
		logic.setAdminUsername(adminUsername);
		logic.setAdminPassword(adminPassword);
		logic.setLoginUrl(loginUrl);
		logic.setScriptUrl(scriptUrl);
		logic.setEid(getAuthenticatedUsername(request));
		request.setAttribute("logic", logic);
		
		//setup remote userId
		String remoteUserId = getRemoteUserId(request, logic);
		if(StringUtils.isBlank(remoteUserId)) {
			log.error("No user info was returned from remote server.");
			doError("error.no.remote.data", "error.heading.general", request, response);
			return;
		}
		request.setAttribute("remoteUserId", remoteUserId);

		
		// get list of sites
		List<Site> sites = getRemoteSitesForUser(logic);
		if(sites.isEmpty()){
			log.error("No sites were returned from remote server.");
			doError("error.no.remote.data", "error.heading.general", request, response);
			return;
		}
		request.setAttribute("remoteSites", sites);
		
		//set list of allowed tool registrations
		request.setAttribute("allowedToolIds", allowedTools);
	
		//do we need to replay the form? This could be due to an error, or we need to show the lists again.
		//if so, use the original request params
		//otherwise, use the preferences
		if(replayForm) {
			request.setAttribute("preferredPortletHeight", request.getParameter("portletHeight"));
			request.setAttribute("preferredPortletTitle", request.getParameter("portletTitle"));
			request.setAttribute("preferredRemoteSiteId", request.getParameter("remoteSiteId"));
			request.setAttribute("preferredRemoteToolId", request.getParameter("remoteToolId"));
			request.setAttribute("errorMessage", request.getParameter("errorMessage"));
		} else {
			request.setAttribute("preferredPortletHeight", getPreferredPortletHeight(request));
			request.setAttribute("preferredPortletTitle", getPreferredPortletTitle(request));
			request.setAttribute("preferredRemoteSiteId", getPreferredRemoteSiteId(request));
			request.setAttribute("preferredRemoteToolId", getPreferredRemoteToolId(request));
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
		
		//get site prefs
		String preferredRemoteSiteId = getPreferredRemoteSiteId(request);
		if(StringUtils.isBlank(preferredRemoteSiteId)) {
			doError("error.no.config", "error.heading.config", request, response);
			return null;
		}
		
		//get tool prefs
		String preferredRemoteToolId = getPreferredRemoteToolId(request);
		if(StringUtils.isBlank(preferredRemoteToolId)) {
			doError("error.no.config", "error.heading.config", request, response);
			return null;
		}
		
		//setup the web service bean
		SakaiWebServiceLogic logic = new SakaiWebServiceLogic();
		logic.setAdminUsername(adminUsername);
		logic.setAdminPassword(adminPassword);
		logic.setLoginUrl(loginUrl);
		logic.setScriptUrl(scriptUrl);
		logic.setEid(getAuthenticatedUsername(request));
		
		//get remote userId
		String remoteUserId = getRemoteUserId(request, logic);
		if(StringUtils.isBlank(remoteUserId)) {
			doError("error.no.remote.data", "error.heading.general", request, response);
			return null;
		}
		
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
		props.put("context_id", preferredRemoteSiteId);
		props.put("tool_consumer_instance_guid", key);
		props.put("lti_version","LTI-1p0");
		props.put("lti_message_type","basic-lti-launch-request");
		props.put("oauth_callback","about:blank");
		props.put("basiclti_submit", "Launch Endpoint with BasicLTI Data");
		props.put("user_id", remoteUserId);
		
		//additional fields
		props.put("remote_tool_id", preferredRemoteToolId);
		
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
	 * Get the preferred remote site id, or null if they have not made a choice yet
	 * @param request
	 * @return
	 */
	private String getPreferredRemoteSiteId(RenderRequest request) {
		PortletPreferences pref = request.getPreferences();
		return pref.getValue("remoteSiteId", null);
	}
	
	/**
	 * Get the preferred remote tool id, or null if they have not made a choice yet
	 * @param request
	 * @return
	 */
	private String getPreferredRemoteToolId(RenderRequest request) {
		PortletPreferences pref = request.getPreferences();
		return pref.getValue("remoteToolId", null);
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
	 * Get the remote userId for this user, either from session or from remote service
	 * @param request
	 * @param logic
	 * @return
	 */
	private String getRemoteUserId(RenderRequest request, SakaiWebServiceLogic logic){
		
		String remoteUserId = (String) request.getPortletSession().getAttribute("remoteUserId");
		if(StringUtils.isBlank(remoteUserId)) {
			remoteUserId = SakaiWebServiceHelper.getRemoteUserIdForUser(logic);
			request.getPortletSession().setAttribute("remoteUserId", remoteUserId);
		}
		
		return remoteUserId;
	}
	
	/**
	 * Get the list of remote sites for this user
	 * @param logic
	 * @return
	 */
	private List<Site> getRemoteSitesForUser(SakaiWebServiceLogic logic){
		return SakaiWebServiceHelper.getAllSitesForUser(logic);
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
