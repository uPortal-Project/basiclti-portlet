<%@ page contentType="text/html" isELIgnored="false" %>

<%@ page import="java.util.Map" %>

<%@ page import="au.edu.anu.portal.portlets.basiclti.support.HttpSupport" %>

<% 

//convert URL params back into a Map
Map<String,String> params = HttpSupport.deserialiseParameterMap(request.getParameterMap());

//create the post
String data = HttpSupport.postLaunchHtml(params.get("endpoint_url"),params);

if(data == null) {
	out.println("Connection failed."); 
} else {
	out.println(data);
}

%>


