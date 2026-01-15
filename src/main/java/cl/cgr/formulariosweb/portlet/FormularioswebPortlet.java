
package cl.cgr.formulariosweb.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.ProcessAction;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;
import cl.cgr.formulariosweb.util.EmailUtil;

/**
 * @author sgonzalezm
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Formulariosweb",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.name=" + FormularioswebPortletKeys.FORMULARIOSWEB,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)

public class FormularioswebPortlet extends MVCPortlet {
	@ProcessAction(name = "enviarFormulario")
	public void enviarFormulario(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		System.out.println("\n====================================");
		System.out.println("[OLD] ENVIAR FORMULARIO - METODO LLAMADO (ProcessAction)");
		System.out.println("====================================");
		
		String nombreApellido = actionRequest.getParameter("nombreApellido");
		String dependencia = actionRequest.getParameter("dependencia");
		String telefonoCelular = actionRequest.getParameter("telefonoCelular");
		String telefonoParticular = actionRequest.getParameter("telefonoParticular");
		String emailFuncionario = actionRequest.getParameter("emailFuncionario");
		String emailParticular = actionRequest.getParameter("emailParticular");
		String asunto = actionRequest.getParameter("asunto");
		String descripcion = actionRequest.getParameter("descripcion");
		String destinatarios = actionRequest.getParameter("destinatarios");

		System.out.println("DEBUG PARAMETROS RECIBIDOS:");
		System.out.println("  - nombreApellido: [" + nombreApellido + "]");
		System.out.println("  - dependencia: [" + dependencia + "]");
		System.out.println("  - telefonoCelular: [" + telefonoCelular + "]");
		System.out.println("  - telefonoParticular: [" + telefonoParticular + "]");
		System.out.println("  - emailFuncionario: [" + emailFuncionario + "]");
		System.out.println("  - emailParticular: [" + emailParticular + "]");
		System.out.println("  - asunto: [" + asunto + "]");
		System.out.println("  - descripcion: [" + descripcion + "]");
		System.out.println("  - destinatarios: [" + destinatarios + "]");
		System.out.println("====================================");

		if (destinatarios == null || destinatarios.isEmpty()) {
			System.out.println("[WARN] ERROR: No hay destinatarios configurados!");
			System.out.println("====================================");
			actionResponse.setRenderParameter("error", "No hay destinatarios configurados");
			return;
		}

		String cuerpo = "<b>Datos del Funcionario:</b><br>" +
			"* Nombre y Apellido: " + nombreApellido + "<br>" +
			"* Dependencia: " + dependencia + "<br>" +
			"* Telefono Celular: " + telefonoCelular + "<br>" +
			"* Telefono Particular: " + telefonoParticular + "<br>" +
			"* Email Funcionario: " + emailFuncionario + "<br>" +
			"* Email Particular: " + emailParticular + "<br><br>" +
			"<b>Descripcion de la Solicitud:</b><br>" + descripcion;

		String remitente = "portal.contraloria@cgr.cl";
		
		System.out.println("Procesando destinatarios...");
		String[] destinatariosArray = destinatarios.split(",");
		System.out.println("Total de destinatarios a procesar: " + destinatariosArray.length);

		boolean envioExitoso = true;
		for (String destinatario : destinatariosArray) {
			String destinatarioTrimmed = destinatario.trim();
			System.out.println("Enviando correo a: [" + destinatarioTrimmed + "]");
			try {
				EmailUtil.enviarCorreo(destinatarioTrimmed, asunto, cuerpo, remitente);
				System.out.println("✓ Correo enviado exitosamente a: " + destinatarioTrimmed);
			} catch (Exception e) {
				System.out.println("✗ Error al enviar a " + destinatarioTrimmed + ": " + e.getMessage());
				e.printStackTrace();
				envioExitoso = false;
			}
		}
		System.out.println("====================================\n");
		
		// Patrón POST-Redirect-GET: establece parámetros de render para evitar reenvío al actualizar
		if (envioExitoso) {
			actionResponse.setRenderParameter("success", "true");
		} else {
			actionResponse.setRenderParameter("error", "Algunos correos no pudieron ser enviados");
		}
	}
}