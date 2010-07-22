<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>


<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>

<%@ page import="au.edu.anu.portal.portlets.basiclti.utils.Constants" %>
<%@ page import="au.edu.anu.portal.portlets.basiclti.logic.SakaiWebServiceLogic" %>
<%@ page import="au.edu.anu.portal.portlets.basiclti.helper.SakaiWebServiceHelper" %>
<%@ page import="au.edu.anu.portal.portlets.basiclti.models.Site" %>
<%@ page import="au.edu.anu.portal.portlets.basiclti.models.Tool" %>

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
<script type="text/javascript" language="javascript" src="${jQueryPath}"></script>

<script type="text/javascript">
	$(document).ready(function(){
	   $('select#<portlet:namespace/>_remoteSiteId').change(function(event){
		   $('select#<portlet:namespace/>_remoteToolId').val($('option:first', this).val());
		   $('form#<portlet:namespace/>_config').submit();
		});
	});

</script>

<style type="text/css">

.basiclti-portlet form p {
	margin-top: 1em;
	margin-bottom: 0;
	font-size: 0.9em;
	font-weight: bold;
}

.basiclti-portlet h2 {
	margin-top: 0;
}

</style>


<div class="basiclti-portlet">

	<c:if test="${not empty errorMessage}">
		<p class="portlet-msg-error">${errorMessage}</p>
	</c:if>
		
	
	<form method="POST" action="<portlet:actionURL/>" id="<portlet:namespace/>_config">
	
		<p><fmt:message key="config.portlet.title" /></p>
		<input type="text" name="portletTitle" id="<portlet:namespace/>_portletTitle" value="${preferredPortletTitle}" />
		
		<p><fmt:message key="config.portlet.height" /></p>
		<select name="portletHeight" id="<portlet:namespace/>_portletHeight">
			<c:forEach var="item" items="${heights}">
				<c:choose>
					<c:when test="${item eq preferredPortletHeight}">
					<option value="${item}" selected="selected">${item}</option>
					</c:when>
					<c:otherwise>
						<option value="${item}">${item}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		
			
		<p><fmt:message key="config.remote.site" /></p>
		<select name="remoteSiteId" id="<portlet:namespace/>_remoteSiteId">
			<option value=""><fmt:message key="config.remote.site.choose" /></option>
			<c:forEach var="item" items="${remoteSites}">
				<c:choose>
					<c:when test="${item.id eq preferredRemoteSiteId}">
						<option value="${item.id}" selected>${item.title}</option>
					</c:when>
					<c:otherwise>
						<option value="${item.id}">${item.title}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
			
		
		
		<p><fmt:message key="config.remote.tool" /></p>
		<c:choose>
			<c:when test="${not empty preferredRemoteSiteId || replayForm}">
			
				<%
				// get list of tools for this site
				List<Tool> rawTools = SakaiWebServiceHelper.getToolsForSite((SakaiWebServiceLogic)request.getAttribute("logic"), (String)request.getAttribute("preferredRemoteSiteId"));
				
				//clean it
				List<Tool> tools = new ArrayList<Tool>();
				List<String> allowedToolIds = (List<String>)request.getAttribute("allowedToolIds");
				
				for(Tool tool: rawTools) {
					if(allowedToolIds.contains(tool.getRegistrationId())) {
						tools.add(tool);
					}
				}
				
				
				pageContext.setAttribute("tools",tools);
				%>
				
				<select name="remoteToolId" id="<portlet:namespace/>_remoteToolId">
					<option value=""><fmt:message key="config.remote.tool.choose" /></option>
					
					<c:forEach var="item" items="${tools}">
						<c:choose>
							<c:when test="${item.registrationId eq preferredRemoteToolId}">
								<option value="${item.registrationId}" selected>${item.title}</option>
							</c:when>
							<c:otherwise>
								<option value="${item.registrationId}">${item.title}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			
			
			</c:when>
			<c:otherwise>
				<select name="" disabled>
					<option value=""><fmt:message key="config.remote.tool.choosesitefirst" /></option>
				</select>
			</c:otherwise>
		
		
		</c:choose>
		
		
		
		<p>
 			<input type="submit" value="<fmt:message key='config.button.submit' />">
		</p>
	</form>

</div>
