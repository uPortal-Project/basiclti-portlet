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

<style type="text/css">
.basiclti-portlet form p {
	margin-top: 1em;
	margin-bottom: 0;
	font-size: 0.9em;
	font-weight: bold;
}
</style>

<div class="basiclti-portlet">

	<c:if test="${not empty errorMessage}">
		<p class="portlet-msg-error">${errorMessage}</p>
	</c:if>
	
	<form method="POST" action="<portlet:actionURL/>" id="<portlet:namespace/>_edit">
		
		<p class="portlet-msg-info"><fmt:message key="edit.preconfigured" /></p>
	
		<p><fmt:message key="config.portlet.title" /></p>
		<input type="text" name="portletTitle" value="${configuredPortletTitle}" />
		
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
		
		<p>
 			<input type="submit" value="<fmt:message key='config.button.submit' />">
		</p>
	</form>

</div>
