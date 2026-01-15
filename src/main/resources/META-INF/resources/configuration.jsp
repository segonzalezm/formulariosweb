<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/init.jsp" %>

<%
	PortletPreferences prefs = renderRequest.getPreferences();
	String destinatariosEmail = prefs.getValue("destinatariosEmail", "");
	String asuntoEmail = prefs.getValue("asuntoEmail", "");
	System.out.println("======================================");
	System.out.println("DEBUG CONFIGURATION.JSP - SE ESTÁ CARGANDO LA CONFIGURACIÓN");
	System.out.println("DEBUG CONFIGURATION.JSP - Destinatarios: [" + destinatariosEmail + "]");
	System.out.println("DEBUG CONFIGURATION.JSP - Asunto: [" + asuntoEmail + "]");
	System.out.println("======================================");
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<div class="container-fluid">
	<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />
		
		<div class="card shadow-lg mt-4 mb-4">
			<div class="card-header bg-dark text-white">
				<h4 class="mb-0">
					<i class="fa fa-cog mr-2"></i>Configuración del Formulario
				</h4>
				<small class="text-muted">Configura los parámetros de envío de correos electrónicos</small>
			</div>
			
			<div class="card-body">
				<div class="alert alert-info alert-dismissible fade show" role="alert">
					<i class="fa fa-info-circle mr-2"></i>
					<strong>Nota:</strong> Los valores configurados aquí se utilizarán para enviar los formularios de contacto.
					<button type="button" class="close" data-dismiss="alert" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>

				<aui:fieldset>
					<div class="form-group">
						<aui:input 
							label="Destinatarios de Email" 
							name="preferences--destinatariosEmail--" 
							type="textarea" 
							value="<%= destinatariosEmail %>"
							cssClass="form-control"
						/>
						<div class="alert alert-light border border-secondary mt-2 p-2 small" role="alert" style="font-size: 12px;">
							<strong>Formato:</strong> Correos separados por comas (email1@cgr.cl, email2@cgr.cl).
						</div>
					</div>

					<div class="form-group">
						<aui:input 
							label="Asunto del Email" 
							name="preferences--asuntoEmail--" 
							type="text" 
							value="<%= asuntoEmail %>"
							cssClass="form-control"
						/>
					</div>
				</aui:fieldset>
			</div>

			<div class="card-footer bg-light">
				<aui:button-row>
					<aui:button type="submit" value="Guardar Cambios" cssClass="btn btn-primary btn-lg" />
				</aui:button-row>
			</div>
		</div>
	</aui:form>
</div>
