<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>


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
<fmt:setBundle basename="au.edu.anu.portal.portlets.basiclti.utils.messages" />

<rs:resourceURL var="jQueryPath" value="/rs/jquery/1.4.2/jquery-1.4.2.min.js"/>

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
			    		</ul>
		    		</p>
		   			<p>Future support may include:
						<ul>
			    			<li>sakai_oae</li>
		   					<li>moodle</li>
		   					<li>mediawiki</li>
		   					<li>wordpress</li>
		   					<li>... anything that supports Basic LTI and has an adapter implementation in this portlet.</li>
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
	    </dl>
			
		</div>
	
	</div>
	
	

</div>
