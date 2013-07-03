<%--

    Copyright 2010-2013 The Australian National University

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

--%>
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


