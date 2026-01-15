<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/init.jsp" %>

<%
	PortletPreferences prefs = renderRequest.getPreferences();
	String destinatariosEmail = prefs.getValue("destinatariosEmail", "");
	String asuntoEmail = prefs.getValue("asuntoEmail", "");
	System.out.println("DEBUG - Destinatarios leídos de preferencias: [" + destinatariosEmail + "]");
	System.out.println("DEBUG - Asunto leído de preferencias: [" + asuntoEmail + "]");
%>

<portlet:resourceURL id="/formularios/getUserData" var="getUserDataURL" />
<portlet:resourceURL id="/formularios/getPageName" var="getPageNameURL" />
<portlet:resourceURL id="/formularios/getFuncionarios" var="getFuncionariosURL" />

<div class="container mt-5">
	<div class="row">
		<div class="col-md-8 offset-md-2">
			<div class="card">
				<div class="card-header d-flex justify-content-between align-items-center" style="background-color:#1b1f49; color:white;">
					<h4 class="mb-0">Formulario de Contacto - Solicitud de Beneficios</h4>
				</div>
				<div class="card-body">
					<% if (destinatariosEmail != null && !destinatariosEmail.trim().isEmpty()) { %>
						<div class="alert alert-info mb-3" role="alert">
							<strong><i class="fa fa-envelope"></i> Destinatarios configurados:</strong> 
							<%= destinatariosEmail %>
						</div>
					<% } %>
					<form id="contactForm" method="POST" action="<portlet:actionURL name='enviarFormulario' />">
						<!-- DATOS DEL FUNCIONARIO -->
						<fieldset class="mb-4">
							<legend class="border-bottom pb-2 mb-3">
								<h5>Datos del Funcionario</h5>
								<small class="text-muted d-block mt-1">Usuario: <strong id="screenNameDisplay">Cargando...</strong></small>
							</legend>
							
							<div class="form-group">
								<label for="nombreApellido">Nombre y Apellido <span class="text-danger">*</span></label>
								<input type="text" class="form-control" id="nombreApellido" name="nombreApellido" placeholder="Ingrese su nombre y apellido" required readonly>
							</div>

							<div class="form-group">
								<label for="emailFuncionario">Email <span class="text-danger">*</span></label>
								<input type="email" class="form-control" id="emailFuncionario" name="emailFuncionario" placeholder="correo@ejemplo.com" required readonly>
							</div>

							<div class="form-group">
								<label for="dependencia">Dependencia<span class="text-danger">*</span></label>
								<input type="text" class="form-control" id="dependencia" name="dependencia" placeholder="Ingrese su dependencia" required readonly>
							</div>

							<!-- Campo oculto para destinatarios seleccionados -->
							<input type="hidden" id="destinatarios" name="destinatarios" value="<%= destinatariosEmail %>">
						</fieldset>

						<!-- DATOS DE LA SOLICITUD -->
						<fieldset class="mb-4">
							<legend class="border-bottom pb-2 mb-3">
								<h5>Datos de la Solicitud</h5>
							</legend>
							
							<div class="form-group">
								<label for="asunto">Asunto <span class="text-danger">*</span></label>
								<input type="text" class="form-control" id="asunto" name="asunto" placeholder="Ingrese el asunto de la solicitud" required readonly>
							</div>

							<div class="form-group">
								<label for="descripcion">Descripción <span class="text-danger">*</span></label>
								<textarea class="form-control" id="descripcion" name="descripcion" rows="5" placeholder="Describa detalladamente su solicitud" required></textarea>
							</div>
						</fieldset>

						<!-- DATOS DE CONTACTO -->
						<fieldset class="mb-4">
							<legend class="border-bottom pb-2 mb-3">
								<h5>Datos de Contacto</h5>
							</legend>
							<!--
							<div class="form-group">
								<label for="telefonoCelular">Teléfono Celular <span class="text-muted">(Opcional)</span></label>
								<input type="tel" class="form-control" id="telefonoCelular" name="telefonoCelular" placeholder="Número de celular">
							</div>
							-->
							<div class="form-group">
								<label for="telefonoCelular">
									Teléfono Celular <span class="text-muted">(Opcional)</span>
								</label>

								<div class="input-group">
									<span class="input-group-text">+56 9</span>
									<input type="tel"
										class="form-control"
										id="telefonoCelular"
										name="telefonoCelular"
										placeholder="Ingrese su número celular (8 dígitos)"
										pattern="[0-9]{8}"
										maxlength="8"
										title="Ingrese su número (8 dígitos)">
								</div>
							</div>
							<div class="form-group">
								<label for="telefonoParticular">Teléfono Particular <span class="text-muted">(Opcional)</span></label>
								<input type="tel" class="form-control" id="telefonoParticular" name="telefonoParticular" placeholder="Número de teléfono" maxlength="8">
							</div>

							<div class="form-group">
								<label for="emailParticular">Email Particular <span class="text-muted">(Opcional)</span></label>
								<input type="email" class="form-control" id="emailParticular" name="emailParticular" placeholder="correo.particular@ejemplo.com">
							</div>
						</fieldset>

						<!-- BOTÓN ENVIAR -->
						<div class="form-group text-center mt-4">
							<button type="submit" class="btn" style="background-color:#1b1f49; color:white;" btn-lg>
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
	let benefitName = '';
	let recipients = [];
	// let allFuncionarios = [];	

	// Obtener el nombre de la página (beneficio)
	fetch('<%= getPageNameURL %>')
		.then(response => response.json())
		.then(data => {
			if (data.success) {
				benefitName = data.pageName;
				// Llenar el asunto desde configuración del portlet
				const asuntoConfigurable = '<%= asuntoEmail %>';
				document.getElementById('asunto').value = asuntoConfigurable || 'Solicitud de beneficio - ' + benefitName;
			}
		})
		.catch(error => {
			console.error('Error al obtener nombre de página:', error);
		});

	// Asegura que el campo oculto destinatarios esté actualizado antes de enviar el formulario principal
	document.getElementById('contactForm').addEventListener('submit', function(e) {
		updateHiddenRecipients();
		if (recipients.length === 0) {
			alert('Debe agregar al menos un destinatario antes de enviar.');
			e.preventDefault();
			return;
		}
		const key = 'formularios_destinatarios_' + (benefitName || '');
		localStorage.setItem(key, recipients.join(','));
		updateHiddenRecipients();
	});

	// Llamar al MVCResourceCommand para obtener los datos del usuario
	console.log('URL de getUserData:', '<%= getUserDataURL %>');
	fetch('<%= getUserDataURL %>')
		.then(response => {
			console.log('Response status:', response.status);
			console.log('Response ok:', response.ok);
			if (!response.ok) {
				throw new Error('HTTP error, status=' + response.status);
			}
			return response.json();
		})
		.then(data => {
			console.log('Datos recibidos:', data);
			if (data.success) {
				// Mostrar el screenName
				console.log('Actualizando con screenName:', data.screenName);
				document.getElementById('screenNameDisplay').textContent = data.screenName;
				
				// Llenar los campos de email y nombre
				document.getElementById('emailFuncionario').value = data.email || '';
				document.getElementById('nombreApellido').value = data.fullName || '';
				document.getElementById('dependencia').value = data.dependencia || '';
				
				console.log('Datos cargados exitosamente');
			} else {
				document.getElementById('screenNameDisplay').textContent = 'Error: ' + (data.error || 'Error desconocido');
				console.error('Error en respuesta:', data.error);
			}
		})
		.catch(error => {
			document.getElementById('screenNameDisplay').textContent = 'Error de conexión';
			console.error('Error al obtener datos del usuario:', error);
		});

	function updateHiddenRecipients() {
		document.getElementById('destinatarios').value = recipients.join(',');
	}
</script>
