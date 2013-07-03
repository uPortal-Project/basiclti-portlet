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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>

<%@ page import="au.edu.anu.portal.portlets.basiclti.utils.Constants" %>

<%
List<String> heights = new ArrayList<String>();
heights.add(String.valueOf(Constants.PORTLET_HEIGHT_400));
heights.add(String.valueOf(Constants.PORTLET_HEIGHT_600));
heights.add(String.valueOf(Constants.PORTLET_HEIGHT_800));
heights.add(String.valueOf(Constants.PORTLET_HEIGHT_1200));
heights.add(String.valueOf(Constants.PORTLET_HEIGHT_1600));
pageContext.setAttribute("heights",heights);
%>

<portlet:defineObjects />

<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="request" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="au.edu.anu.portal.portlets.basiclti.utils.messages" />

<style type="text/css">

.basiclti-portlet form p {
	margin-top: 1em;
	margin-bottom: 0;
	font-size: 0.9em;
	font-weight: bold;
}

.basiclti-portlet form textarea {
	width: inherit !important;
}

.basiclti-portlet-form {
	float:left;
  	width: 600px;
}
.basiclti-portlet-info {
    margin-left: 600px;
}

.basiclti-portlet-info dl {
	margin:0;
	margin-top: 1em;
}

.basiclti-portlet-info dt {
	font-weight: bold;
}



</style>


<div class="basiclti-portlet">

	<div class="basiclti-portlet-form">
		<c:if test="${not empty errorMessage}">
			<p class="portlet-msg-error">${errorMessage}</p>
		</c:if>
			
		
		<form method="POST" action="<portlet:actionURL/>" id="<portlet:namespace/>_config">
		
			<p><fmt:message key="config.portlet.title" /></p>
			<input type="text" name="portletTitle" value="${configuredPortletTitle}" />
			
			<p><fmt:message key="config.portlet.providertype" /></p>
			<input type="text" name="providerType" value="${configuredProviderType}" />
			
			<p><fmt:message key="config.portlet.height" /></p>
			<select name="portletHeight">
				<c:forEach var="item" items="${heights}">
					<c:choose>
						<c:when test="${item eq configuredPortletHeight}">
						<option value="${item}" selected="selected">${item}</option>
						</c:when>
						<c:otherwise>
							<option value="${item}">${item}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			
				
			<p><fmt:message key="config.portlet.launchdata" /></p>
			<textarea name="launchData" rows="20" cols="80">${configuredLaunchData}</textarea>
			
			<p><fmt:message key="config.portlet.basiclti.key" /></p>
			<input type="text" name="key" value="${key}" />
			
			<p><fmt:message key="config.portlet.basiclti.secret" /></p>
			<input type="text" name="secret" value="${secret}" />
			
			<p>
	 			<input type="submit" value="<fmt:message key='config.button.submit' />">
			</p>
		</form>
	</div>
	
	<div class="basiclti-portlet-info">
		<div class="portlet-msg-info">
			<h3>Configuration information</h3>
			<dl>
			<dt>Provider type</dt>
				<dd>
		    		<p><em>Optional</em>. Defaults to 'standard' if not set.</p>
					<p>This is because specially named attributes or special processing of attributes may need to be performed before the launch data is sent.</p>
					<p>If your provider requires any special information, you MUST set this otherwise the launch will fail.</p>
		    
		    		<p>Currently supported values are:
			    		<ul>
			    			<li>standard</li>
			    			<li>sakai</li>
			    			<li>peoplesoft</li>
			    			<li>noteflight</li>
			    			<li>chemvantage</li>
			    			<li>wimba</li>
			    		</ul>
		    		</p>
		   			<p>Future support may include:
						<ul>
			    			<li>sakai_oae</li>
			    			<li>mediawiki</li>
		   					<li>wordpress</li>
		   					<li>moodle</li>
		   					<li>blackboard</li>
		   					<li>... anything that provides Basic LTI provider support and has an adapter implementation in this portlet.</li>
		   				</ul>
		   			</p>
		    	</dd>
	    	<dt>Launch data</dt>
		    <dd>
			   	<p><em>Required.</em> Include any special data you want to set, plus anything that is static and will not be set by the adapter.</p>
				
			    <p>You must include:
				    <ul>
				    	<li>the raw endpoint URL (endpoint_url)</li>
				    	<li>all parameters that need to be sent in key=value form, with delimiter ;;</li>
				    </ul>
			    </p>
			    
			    <p>Do not include:
					<ul>
					<li>any user attributes, these will be added before the request is sent.</li>
					<li>any specific data on the endpoint URL. This will be catered for in the portlet itself, since you have set the Provider type. 
						The portlet will use the appropriate adapter to process the data before sending the request.</li>
					<li>any default parameters about Basic LTI itself, these will be added automatically.</li>
					</ul>
				</p>
				
				<p>For more info on the Basic LTI launch params, see 
					<a href="http://www.imsglobal.org/lti/blti/bltiv1p0/ltiBLTIimgv1p0.html" target="_blank">http://www.imsglobal.org/lti/blti/bltiv1p0/ltiBLTIimgv1p0.html</a>
				</p> 
			</dd>
			<dt>Key</dt>
		    <dd>
			   	<p><em>Required.</em> This is the public parameter that uniquely identifies this portlet connecting to the Basic LTI provider, e.g. <b>my.school.edu.au</b>.</p>
			</dd>
			<dt>Secret</dt>
		    <dd>
			   	<p><em>Required.</em> This is the private value tied to the Basic LTI key, and is privately shared between the portlet and the Basic LTI provider, e.g. <b>secret</b>.</p>
			</dd>
	    </dl>
			
		</div>
	
	</div>
	
	

</div>
