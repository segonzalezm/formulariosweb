<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.servlet.SessionMessages" %>
<%@ page import="com.liferay.portal.kernel.servlet.SessionErrors" %>
<%@ page import="com.liferay.portal.kernel.util.PortalUtil" %>

<%
	PortletPreferences prefs = renderRequest.getPreferences();
	String destinatariosEmail = prefs.getValue("destinatariosEmail", "");
	String asuntoEmail = prefs.getValue("asuntoEmail", "");
	
	// Obtener URL actual para el redirect
	String currentURL = PortalUtil.getCurrentURL(request);
%>

<portlet:resourceURL id="/formularios/getUserData" var="getUserDataURL" />
<portlet:resourceURL id="/formularios/getPageName" var="getPageNameURL" />
<portlet:resourceURL id="/formularios/getFuncionarios" var="getFuncionariosURL" />

<div class="container mt-5">
	<div class="row">
		<div class="col-md-10 offset-md-1">
			<div class="card">
				<div class="card-header d-flex justify-content-between align-items-center"
					 style="background-color:#1b1f49; color:white;">
					<h4 class="mb-0">Formulario de Contacto</h4>
				</div>

				<div class="card-body">

					<%
						// Mostrar mensaje de éxito o error usando SessionMessages
						boolean showSuccess = SessionMessages.contains(renderRequest, "formulario-enviado-exitosamente");
						boolean showError = SessionErrors.contains(renderRequest, "error-envio-parcial");
						
						if (showSuccess) {
					%>
						<div class="alert alert-success alert-dismissible fade show" role="alert">
							<strong><i class="fa fa-check-circle"></i> ¡Éxito!</strong> 
							El formulario ha sido enviado correctamente.
							<button type="button" class="close" data-dismiss="alert" aria-label="Cerrar">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
					<%
						} else if (showError) {
					%>
						<div class="alert alert-danger alert-dismissible fade show" role="alert">
							<strong><i class="fa fa-exclamation-triangle"></i> Error:</strong> 
							Algunos correos no pudieron ser enviados
							<button type="button" class="close" data-dismiss="alert" aria-label="Cerrar">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
					<%
						}
					%>

					<% 
						// Solo mostrar destinatarios configurados a usuarios Administradores
						boolean isAdmin = themeDisplay.getPermissionChecker().isOmniadmin();
						if (isAdmin && destinatariosEmail != null && !destinatariosEmail.trim().isEmpty()) { 
					%>
					<% } %>

					<portlet:actionURL name="enviarFormulario" var="enviarFormularioURL">
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<form id="contactForm"
						  method="POST"
						  action="<%= enviarFormularioURL %>">

						<!-- ================= DATOS DEL FUNCIONARIO ================= -->
						<fieldset class="mb-4">
							<legend class="border-bottom pb-2 mb-3">
								<h5>Datos del Funcionario</h5>
							<small class="text-muted d-none mt-1">
								Usuario: <strong id="screenNameDisplay">Cargando...</strong>
							</small>
							</legend>

							<div class="form-group">
								<label for="nombreApellido">
									Nombre y Apellido <span class="text-danger">*</span>
								</label>
								<input type="text"
									   class="form-control"
									   id="nombreApellido"
									   name="<portlet:namespace />nombreApellido"
								   placeholder="Ingrese su nombre y apellido"
								   readonly>
							</div>

							<div class="form-group">
								<label for="emailFuncionario">
									Email <span class="text-danger">*</span>
								</label>
								<input type="email"
									   class="form-control"
									   id="emailFuncionario"
									   name="<portlet:namespace />emailFuncionario"
								   placeholder="correo@ejemplo.com"
								   readonly>
							</div>

							<div class="form-group">
								<label for="dependencia">
									Dependencia <span class="text-danger">*</span>
								</label>
								<input type="text"
									   class="form-control"
									   id="dependencia"
									   name="<portlet:namespace />dependencia"
								   placeholder="Ingrese su dependencia"
								   readonly>
							</div>

							<!-- Hidden -->
							<input type="hidden"
								   id="destinatarios"
								   name="<portlet:namespace />destinatarios"
								   value="<%= destinatariosEmail %>">
						</fieldset>

						<!-- ================= DATOS DE LA SOLICITUD ================= -->
						<fieldset class="mb-4">
							<legend class="border-bottom pb-2 mb-3">
								<h5>Datos de la Solicitud</h5>
							</legend>

							<div class="form-group">
								<label for="asunto">
									Asunto <span class="text-danger">*</span>
								</label>
								<input type="text"
									   class="form-control"
									   id="asunto"
									   name="<portlet:namespace />asunto"
									   placeholder="Ingrese el asunto de la solicitud"
								   value="<%= asuntoEmail %>"
								   readonly>
							</div>

							<div class="form-group">
								<label for="descripcion">
									Descripción <span class="text-danger">*</span>
								</label>
								<textarea class="form-control"
										  id="descripcion"
										  name="<portlet:namespace />descripcion"
										  rows="5"
										  placeholder="Describa detalladamente su solicitud"
										  required></textarea>
							</div>
						</fieldset>

						<!-- ================= DATOS DE CONTACTO ================= -->
						<fieldset class="mb-4">
							<legend class="border-bottom pb-2 mb-3">
								<h5>Datos de Contacto</h5>
							</legend>

							<div class="form-group">
								<label for="telefonoCelular">
									Teléfono Celular <span class="text-muted">(Opcional)</span>
								</label>
								<div class="input-group">
									<span class="input-group-text">+56 9</span>
									<input type="tel"
										   class="form-control"
										   id="telefonoCelular"
										   name="<portlet:namespace />telefonoCelular"
										   placeholder="Ingrese su número celular (8 dígitos)"
										   pattern="[0-9]{8}"
										   maxlength="8">
								</div>
							</div>

							<div class="form-group">
								<label for="telefonoParticular">
									Teléfono Particular <span class="text-muted">(Opcional)</span>
								</label>
								<div class="input-group">
									<span class="input-group-text">+56</span>
									<input type="tel"
										   class="form-control"
										   id="telefonoParticular"
										   name="<portlet:namespace />telefonoParticular"
										   placeholder="Ingrese su número particular (8 dígitos)"
										   pattern="[0-9]{8}"
										   maxlength="8">
								</div>
							</div>

							<div class="form-group">
								<label for="emailParticular">
									Email Particular <span class="text-muted">(Opcional)</span>
								</label>
								<input type="email"
									   class="form-control"
									   id="emailParticular"
									   name="<portlet:namespace />emailParticular">
							</div>
						</fieldset>

						<div class="form-group text-center mt-4">
							<button type="submit"
									class="btn"
									style="background-color:#1b1f49; color:white;">
								<i class="fa fa-paper-plane"></i> Enviar
							</button>
						</div>

					</form>
				</div>
			</div>
		</div>
	</div>
</div>



<script>
	console.log('========== SCRIPT INICIADO ==========');

	// Inicializar cuando el DOM esté listo
	document.addEventListener('DOMContentLoaded', function() {
		console.log('DOMContentLoaded - Inicializando formulario...');
		
		// Obtener datos del usuario desde el servidor
		console.log('URL de getUserData:', '<%= getUserDataURL %>');
		fetch('<%= getUserDataURL %>')
			.then(response => {
				console.log('Response status:', response.status);
				if (!response.ok) {
					throw new Error('HTTP error, status=' + response.status);
				}
				return response.json();
			})
			.then(data => {
				console.log('✓ Datos del usuario recibidos:', data);
				if (data.success) {
					// Llenar los campos del formulario con datos del usuario
					document.getElementById('screenNameDisplay').textContent = data.screenName || 'Desconocido';
					document.getElementById('emailFuncionario').value = data.email || '';
					document.getElementById('nombreApellido').value = data.fullName || '';
					document.getElementById('dependencia').value = data.dependencia || '';
					
					console.log('✓ Formulario prellenado exitosamente');
					console.log('  - Email: ' + data.email);
					console.log('  - Nombre: ' + data.fullName);
					console.log('  - Dependencia: ' + data.dependencia);
				} else {
					document.getElementById('screenNameDisplay').textContent = 'Error: ' + (data.error || 'Error desconocido');
					console.error('✗ Error en respuesta:', data.error);
				}
			})
			.catch(error => {
				document.getElementById('screenNameDisplay').textContent = 'Error de conexión';
				console.error('✗ Error al obtener datos del usuario:', error);
			});
	});

	// Validar formulario antes de enviar
	document.getElementById('contactForm').addEventListener('submit', function(e) {
		console.log('\n========== VALIDANDO FORMULARIO ==========');
		
		const nombreApellido = document.getElementById('nombreApellido').value.trim();
		const emailFuncionario = document.getElementById('emailFuncionario').value.trim();
		const dependencia = document.getElementById('dependencia').value.trim();
		const asunto = document.getElementById('asunto').value.trim();
		const descripcion = document.getElementById('descripcion').value.trim();
		const destinatarios = document.getElementById('destinatarios').value.trim();
		
		console.log('Datos del formulario:');
		console.log('  - nombreApellido: [' + nombreApellido + ']');
		console.log('  - emailFuncionario: [' + emailFuncionario + ']');
		console.log('  - dependencia: [' + dependencia + ']');
		console.log('  - asunto: [' + asunto + ']');
		console.log('  - descripcion: [' + descripcion + ']');
		console.log('  - destinatarios: [' + destinatarios + ']');
		
		if (!nombreApellido || !emailFuncionario || !dependencia || !asunto || !descripcion) {
			console.error('✗ Faltan campos requeridos');
			alert('Por favor, completa todos los campos requeridos.');
			e.preventDefault();
			return false;
		}
		
		if (!destinatarios) {
			console.error('✗ No hay destinatarios configurados');
			alert('No hay destinatarios de email configurados. Contacta al administrador.');
			e.preventDefault();
			return false;
		}
		
		console.log('✓ Validación completada. Enviando formulario...');
	});

	// Limpiar formulario después del envío exitoso
	<% if (showSuccess) { %>
		console.log('✓ Envío exitoso detectado. Limpiando formulario...');
		// Limpiar solo los campos editables (no los precargados)
		document.getElementById('descripcion').value = '';
		document.getElementById('telefonoCelular').value = '';
		document.getElementById('telefonoParticular').value = '';
		document.getElementById('emailParticular').value = '';
		console.log('✓ Formulario limpiado');
	<% } %>
</script>
