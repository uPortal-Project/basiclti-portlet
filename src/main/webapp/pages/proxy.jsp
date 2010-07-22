<%@ page contentType="text/html" isELIgnored="false" %>

<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<%@ page import="javax.portlet.PortletConfig" %>


<%@ page import="au.edu.anu.portal.portlets.basiclti.support.OAuthSupport" %>
<%@ page import="au.edu.anu.portal.portlets.basiclti.support.HttpSupport" %>

<% 


//need to get values from the URL and then reconstruct the map.
//this is because the iframe is in a different request so does not have access to the original data
//so we must pass it on the URL and deserilaise it back to a map here via Request.getParameterMap

//since this is a separate request, we are no longer in the portlet


//convert URL params back into Map
Map<String,String> props = HttpSupport.deserialiseParameterMap(request.getParameterMap());

//get secret from application config
String secret = application.getInitParameter("secret");

//construct endpoint from application config and add remote toolId
String endpoint = application.getInitParameter("endpoint") + props.get("remote_tool_id");

//get key from map
String key = props.get("tool_consumer_instance_guid");

//sign the map
props = OAuthSupport.signProperties(endpoint, props, "POST", key, secret);
//String data = HttpSupport.doPost(endpoint,props);
String data = HttpSupport.postLaunchHtml(endpoint,props);


if(data == null) {
	out.println("connection failed"); 
} else {
	out.println(data);
}

%>

<%
//endpoint, secret and key can be shraed, although endpoint will need to be base endpoint and then reconstructed here
/*
String endpoint="http://localhost:8081/imsblti/provider/sakai.resources";
String key = "my.anu.edu.au";
String secret = "eba7f57fe1d653e0fd275e9beea8e25f";

Map<String,String> props = new HashMap<String,String>();
props.put("resource_link_id","120988f929-274612");
props.put("roles","Instructor");
props.put("lis_person_name_full","Jean-François Lévêque");
props.put("lis_person_contact_email_primary","user@school.edu");
props.put("lis_person_sourcedid","school.edu:user");
//props.put("context_id","456434513");
props.put("context_id","9ec48d9e-b690-4090-a300-10a44ed7656e");


props.put("context_title","Design of Personal Environments Jean-François Lévêque");
props.put("context_label","SI182");
props.put("tool_consumer_instance_guid", "my.anu.edu.au");
props.put("tool_consumer_instance_description", "Australian National University");
props.put("lti_version","LTI-1p0");
props.put("lti_message_type","basic-lti-launch-request");
props.put("oauth_callback","about:blank");
//props.put("user_id","292832126");
props.put("user_id","5d15d0c7-e3ca-4041-b533-ee3a60390737");



props.put("basiclti_submit", "Launch Endpoint with BasicLTI Data");
	

  props = OAuthSupport.signProperties(endpoint, props, "POST", key, secret);
  //String data = HttpSupport.doPost(endpoint,props);
  String data = HttpSupport.postLaunchHtml(endpoint,props);
  */
  
 /* 
  if(data == null) {
	out.println("connection failed"); 
  } else {
	out.println(data);
  }
*/





%>

