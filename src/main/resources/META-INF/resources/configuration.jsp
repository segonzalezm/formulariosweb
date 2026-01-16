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

<div class="container mt-5 mb-5">
	<div class="row justify-content-center">
		<div class="col-lg-8 col-md-10">
			<aui:form action="<%= configurationActionURL %>" method="post" name="fm" cssClass="needs-validation">
				<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
				<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />
				
				<div class="card border-0 shadow">
					<div class="card-header bg-gradient" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
						<h3 class="text-white mb-0 font-weight-bold">
							<i class="fa fa-sliders-h mr-2"></i>Configuración del Formulario
						</h3>
					</div>
					
					<div class="card-body p-4">
						<div class="alert alert-info border-left border-info pl-3" role="alert">
							<div class="d-flex">
								<div class="mr-3">
									<i class="fa fa-info-circle fa-lg text-info"></i>
								</div>
								<div>
									<h5 class="alert-heading mb-1">Información</h5>
									<p class="mb-0">Los valores configurados aquí se utilizarán para enviar los formularios de contacto.</p>
								</div>
							</div>
						</div>

						<div class="form-group mt-4">
							<label for="preferences--destinatariosEmail--" class="font-weight-bold text-dark mb-2">
								<i class="fa fa-envelope mr-2 text-primary"></i>Destinatarios de Email
							</label>
							<aui:input 
								inlineLabel="true"
								label=""
								name="preferences--destinatariosEmail--" 
								type="textarea" 
								value="<%= destinatariosEmail %>"
								cssClass="form-control form-control-lg"
								rows="3"
								placeholder="email1@cgr.cl, email2@cgr.cl"
							/>
							<div class="d-flex align-items-center mt-2 text-muted small">
								<i class="fa fa-lightbulb mr-2"></i>
								<span><strong>Formato:</strong> Correos separados por comas (email1@cgr.cl, email2@cgr.cl)</span>
							</div>
						</div>

						<div class="form-group mt-4">
							<label for="preferences--asuntoEmail--" class="font-weight-bold text-dark mb-2">
								<i class="fa fa-heading mr-2 text-primary"></i>Asunto del Email
							</label>
							<aui:input 
								inlineLabel="true"
								label=""
								name="preferences--asuntoEmail--" 
								type="text" 
								value="<%= asuntoEmail %>"
								cssClass="form-control form-control-lg"
								placeholder="Ej: Nuevo formulario de contacto"
							/>
						</div>
					</div>

					<div class="card-footer bg-light border-top">
						<div class="d-flex justify-content-between">
							<div></div>
							<aui:button-row>
								<aui:button type="submit" value="Guardar Cambios" cssClass="btn btn-primary btn-lg px-4 py-2 font-weight-bold" />
							</aui:button-row>
						</div>
					</div>
				</div>
			</aui:form>
		</div>
	</div>
</div>
