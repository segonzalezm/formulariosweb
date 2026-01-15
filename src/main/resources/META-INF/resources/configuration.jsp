<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/init.jsp" %>

<%
	PortletPreferences prefs = renderRequest.getPreferences();
	String destinatariosEmail = prefs.getValue("destinatariosEmail", "");
	System.out.println("======================================");
	System.out.println("DEBUG CONFIGURATION.JSP - SE ESTÁ CARGANDO LA CONFIGURACIÓN");
	System.out.println("DEBUG CONFIGURATION.JSP - Valor actual: [" + destinatariosEmail + "]");
	System.out.println("======================================");
%>

<h1 style="color: red;">PÁGINA DE CONFIGURACIÓN</h1>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />
	
	<div class="portlet-configuration-body-content">
		<div class="container-fluid container-fluid-max-xl">
			<aui:fieldset>
				<aui:input 
					label="Destinatarios de Email" 
					name="preferences--destinatariosEmail--" 
					type="text" 
					value="<%= destinatariosEmail %>"
					helpMessage="Ingrese los correos electrónicos separados por comas (ejemplo: email1@cgr.cl, email2@cgr.cl)"
				/>
			</aui:fieldset>
		</div>
	</div>
	
	<aui:button-row>
		<aui:button type="submit" value="save" />
	</aui:button-row>
</aui:form>
