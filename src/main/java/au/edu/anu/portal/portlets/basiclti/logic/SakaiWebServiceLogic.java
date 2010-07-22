package au.edu.anu.portal.portlets.basiclti.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.edu.anu.portal.portlets.basiclti.models.Site;
import au.edu.anu.portal.portlets.basiclti.models.Tool;
import au.edu.anu.portal.portlets.basiclti.support.WebServiceSupport;
import au.edu.anu.portal.portlets.basiclti.utils.XmlParser;


/**
 * This class is a simple logic class that makes the required web service calls
 * for the Basic LTI portlet to get it's information.
 * 
 * It users the WebServiceSupport class for the actual web service calls.
 * 
 * A remote sessionid is stored locally, and checked however if any service calls fail, it is invalidated to force a new one.
 * The 
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class SakaiWebServiceLogic {

	private String adminUsername;
	private String adminPassword;
	private String loginUrl;
	private String scriptUrl;
	private String eid;
	
	// stores the admin session, not accessible
	private String session;

	private final String METHOD_LOGIN="login";
	private final String METHOD_GET_USER_ID="getUserId";
	private final String METHOD_CHECK_SESSION="checkSession";
	private final String METHOD_GET_ALL_SITES_FOR_USER="getAllSitesForUser";
	private final String METHOD_GET_PAGES_AND_TOOLS_FOR_SITE="getPagesAndToolsForSite";

	
	
	
	
	/**
	 * Get the userId for a user.
	 * @return id or null if no response
	 */
	public String getRemoteUserIdForUser() {
		
		Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
		data.put("sessionid", new HashMap<String,String>(){
			{
				put("value",getSession());
				put("type","string");
			}
		});
		data.put("eid", new HashMap<String,String>(){
			{
				put("value",getEid());
				put("type","string");
			}
		});
		
		return WebServiceSupport.call(getScriptUrl(), METHOD_GET_USER_ID, data);
	}
	
	/**
	 * Get the XML for a list of all sites for a user, transformed to a List of Sites
	 * @return
	 */
	public List<Site> getAllSitesForUser() {
				
		Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
		data.put("sessionid", new HashMap<String,String>(){
			{
				put("value",getSession());
				put("type","string");
			}
		});
		data.put("eid", new HashMap<String,String>(){
			{
				put("value",getEid());
				put("type","string");
			}
		});
		
		String xml = WebServiceSupport.call(getScriptUrl(), METHOD_GET_ALL_SITES_FOR_USER, data);		
		
		return XmlParser.parseListOfSites(xml);
	}
	
	/**
	 * Get the list of tools in a site.
	 * @param siteId	siteId
	 * @return
	 */
	public List<Tool> getToolsForSite(final String siteId) {
		
		Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
		data.put("sessionid", new HashMap<String,String>(){
			{
				put("value",getSession());
				put("type","string");
			}
		});
		data.put("userid", new HashMap<String,String>(){
			{
				put("value",getEid());
				put("type","string");
			}
		});
		data.put("siteid", new HashMap<String,String>(){
			{
				put("value",siteId);
				put("type","string");
			}
		});
		
		String xml = WebServiceSupport.call(getScriptUrl(), METHOD_GET_PAGES_AND_TOOLS_FOR_SITE, data);		

		return XmlParser.parseListOfPages(xml);
	}
	
	
	
	
	/**
	 * Get a new session for the admin user. Don't call this directly, use getSession() instead.
	 * @return
	 */
	private String getNewAdminSession() {
		
		String session = null;
		
		//setup data to send
		Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
		data.put("id", new HashMap<String,String>(){
			{
				put("value",getAdminUsername());
				put("type","string");
			}
		});
		data.put("pw", new HashMap<String,String>(){
			{
				put("value",getAdminPassword());
				put("type","string");
			}
		});
		
		session = WebServiceSupport.call(getLoginUrl(), METHOD_LOGIN, data);
		
		//store locally
		setSession(session);
		
		//and return it
		return session;
	}
	
	/**
	 * Check the current session is still active. Don't call this directly, use getSession() instead.
	 * @return
	 */
	private boolean isSessionActive() {
		
		Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
		data.put("sessionid", new HashMap<String,String>(){
			{
				put("value",session);
				put("type","string");
			}
		});
		
		String results = WebServiceSupport.call(getScriptUrl(), METHOD_CHECK_SESSION, data);
		
		if(StringUtils.equals(results, session)) {
			return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * Default no-arg constructor
	 */
	public SakaiWebServiceLogic() {
		
	}
	

	public String getAdminUsername() {
		return adminUsername;
	}
	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	public String getLoginUrl() {
		return loginUrl;
	}
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	public String getScriptUrl() {
		return scriptUrl;
	}
	public void setScriptUrl(String scriptUrl) {
		this.scriptUrl = scriptUrl;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public String getEid() {
		return eid;
	}

	
	
	/**
	 * Get local session, check it's still active, otherwise get a new one
	 * @return
	 */
	private String getSession() {
		
		if(StringUtils.isBlank(session)) {
			return getNewAdminSession();
		}
		
		if(!isSessionActive()){
			return getNewAdminSession();
		}
		
		return session;
	}
	private void setSession(String session) {
		this.session = session;
	}
	
	
}
