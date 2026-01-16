package cl.cgr.formulariosweb.portlet.command;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;
import cl.cgr.formulariosweb.util.EmailUtil;

import com.liferay.portal.kernel.util.PortalUtil;
import javax.portlet.PortletURL;

/**
 * MVC Action Command para enviar formularios
 * @author sgonzalezm
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + FormularioswebPortletKeys.FORMULARIOSWEB,
		"mvc.command.name=enviarFormulario"
	},
	service = MVCActionCommand.class
)
public class EnviarFormularioMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		System.out.println("\n");
		System.out.println("================================================================================");
		System.out.println("*** ENVIAR FORMULARIO MVC ACTION COMMAND - LLAMADO ***");
		System.out.println("================================================================================");
		
		// Obtener parámetros del formulario
		String nombreApellido = ParamUtil.getString(actionRequest, "nombreApellido", "");
		String dependencia = ParamUtil.getString(actionRequest, "dependencia", "");
		String telefonoCelular = ParamUtil.getString(actionRequest, "telefonoCelular", "");
		String telefonoParticular = ParamUtil.getString(actionRequest, "telefonoParticular", "");
		String emailFuncionario = ParamUtil.getString(actionRequest, "emailFuncionario", "");
		String emailParticular = ParamUtil.getString(actionRequest, "emailParticular", "");
		String descripcion = ParamUtil.getString(actionRequest, "descripcion", "");

		System.out.println("\n[PARAMS] PARAMETROS RECIBIDOS DEL FORMULARIO:");
		System.out.println("  [OK] nombreApellido: [" + nombreApellido + "]");
		System.out.println("  [OK] dependencia: [" + dependencia + "]");
		System.out.println("  [OK] telefonoCelular: [" + telefonoCelular + "]");
		System.out.println("  [OK] telefonoParticular: [" + telefonoParticular + "]");
		System.out.println("  [OK] emailFuncionario: [" + emailFuncionario + "]");
		System.out.println("  [OK] emailParticular: [" + emailParticular + "]");
		System.out.println("  [OK] descripcion: [" + descripcion + "]");

		// Obtener configuración desde PortletPreferences
		PortletPreferences prefs = actionRequest.getPreferences();
		String destinatarios = prefs.getValue("destinatariosEmail", "");
		String asunto = prefs.getValue("asuntoEmail", "");

		System.out.println("\n[CONFIG] PARAMETROS DESDE PORTLET PREFERENCES:");
		System.out.println("  [OK] destinatarios: [" + destinatarios + "]");
		System.out.println("  [OK] asunto: [" + asunto + "]");

		if (destinatarios == null || destinatarios.trim().isEmpty()) {
			System.out.println("\n[ERROR] No hay destinatarios disponibles!");
			System.out.println("================================================================================");
			return;
		}
		if (asunto == null || asunto.trim().isEmpty()) {
			System.out.println("\n[ERROR] No hay asunto disponible!");
			System.out.println("================================================================================");
			return;
		}

		// Convertir saltos de línea a <br> para que se vean correctamente en HTML
		String descripcionHTML = descripcion
			.replace("\r\n", "<br>")
			.replace("\n", "<br>")
			.replace("\r", "<br>");

		String cuerpo = "<!DOCTYPE html>" +
			"<html>" +
			"<head>" +
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
			"</head>" +
			"<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
			"<p>Estimado/a,</p>" +
			"<p>Se ha recibido una nueva solicitud a trav&eacute;s del formulario de contacto. A continuaci&oacute;n se detalla la informaci&oacute;n del remitente:</p>" +
			"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
			"<h3 style=\"color: #003366; margin-bottom: 10px;\">Informaci&oacute;n del Funcionario</h3>" +
			"<table style=\"width: 100%; border-collapse: collapse;\">" +
			"<tr><td style=\"padding: 8px; font-weight: bold; width: 200px;\">Nombre y Apellido:</td><td style=\"padding: 8px;\">" + nombreApellido + "</td></tr>" +
			"<tr><td style=\"padding: 8px; font-weight: bold;\">Dependencia:</td><td style=\"padding: 8px;\">" + dependencia + "</td></tr>" +
			"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Celular:</td><td style=\"padding: 8px;\">" + telefonoCelular + "</td></tr>" +
			"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Particular:</td><td style=\"padding: 8px;\">" + telefonoParticular + "</td></tr>" +
			"<tr><td style=\"padding: 8px; font-weight: bold;\">Email:</td><td style=\"padding: 8px;\">" + emailFuncionario + "</td></tr>" +
			"<tr><td style=\"padding: 8px; font-weight: bold;\">Email Particular:</td><td style=\"padding: 8px;\">" + emailParticular + "</td></tr>" +
			"</table>" +
			"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
			"<h3 style=\"color: #003366; margin-bottom: 10px;\">Descripci&oacute;n de la Solicitud</h3>" +
			"<p style=\"background-color: #f5f5f5; padding: 15px; border-left: 4px solid #003366;\">" + descripcionHTML + "</p>" +
			"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
			"<p style=\"font-size: 12px; color: #666;\">Este es un mensaje automatizado. Por favor, no responda directamente a este correo.</p>" +
			"</body>" +
			"</html>";

		String remitente = "noreply@contraloria.cl";

		System.out.println("\n[PROCESS] PROCESANDO DESTINATARIOS:");
		String[] destinatariosArray = destinatarios.split(",");
		System.out.println("Total de destinatarios a procesar: " + destinatariosArray.length);

		int enviados = 0;
		int errores = 0;

		for (String destinatario : destinatariosArray) {
			String destinatarioTrimmed = destinatario.trim();
			if (destinatarioTrimmed.isEmpty()) {
				continue;
			}
			
			System.out.println("\n[EMAIL] Intentando enviar a: [" + destinatarioTrimmed + "]");
			try {
				EmailUtil.enviarCorreo(destinatarioTrimmed, asunto, cuerpo, remitente);
				System.out.println("[OK] Correo enviado exitosamente a: " + destinatarioTrimmed);
				enviados++;
			} catch (Exception e) {
				System.out.println("[ERROR] Error al enviar a " + destinatarioTrimmed + ": " + e.getMessage());
				e.printStackTrace();
				errores++;
			}
		}

		// Enviar copia al usuario que realiza la solicitud
		System.out.println("\n[COPY] ENVIANDO COPIA AL USUARIO:");
		String emailUsuario = emailFuncionario;
		if (emailUsuario != null && !emailUsuario.trim().isEmpty()) {
			String asuntoCopia = "COPIA SOLICITUD - " + asunto;
			String cuerpoConfirmacion = "<!DOCTYPE html>" +
				"<html>" +
				"<head>" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
				"</head>" +
				"<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
				"<p>Estimado/a " + nombreApellido + ",</p>" +
				"<p>Le enviamos una copia de la solicitud que ha realizado a trav&eacute;s del formulario de contacto de la Contralor&iacute;a General de la Rep&uacute;blica.</p>" +
				"<p>Esta es una confirmaci&oacute;n de que su solicitud ha sido recibida exitosamente.</p>" +
				"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
				"<h3 style=\"color: #003366; margin-bottom: 10px;\">Informaci&oacute;n de su Solicitud</h3>" +
				"<table style=\"width: 100%; border-collapse: collapse;\">" +
				"<tr><td style=\"padding: 8px; font-weight: bold; width: 200px;\">Nombre y Apellido:</td><td style=\"padding: 8px;\">" + nombreApellido + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Dependencia:</td><td style=\"padding: 8px;\">" + dependencia + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Celular:</td><td style=\"padding: 8px;\">" + telefonoCelular + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Particular:</td><td style=\"padding: 8px;\">" + telefonoParticular + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Email:</td><td style=\"padding: 8px;\">" + emailFuncionario + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Email Particular:</td><td style=\"padding: 8px;\">" + emailParticular + "</td></tr>" +
				"</table>" +
				"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
				"<h3 style=\"color: #003366; margin-bottom: 10px;\">Descripci&oacute;n de su Solicitud</h3>" +
				"<p style=\"background-color: #f5f5f5; padding: 15px; border-left: 4px solid #003366;\">" + descripcionHTML + "</p>" +
				"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
				"<p style=\"font-size: 12px; color: #666;\">Este es un mensaje automatizado. Por favor, no responda directamente a este correo.</p>" +
				"</body>" +
				"</html>";
			
			try {
				EmailUtil.enviarCorreo(emailUsuario, asuntoCopia, cuerpoConfirmacion, remitente);
				System.out.println("[OK] Copia enviada exitosamente al usuario: " + emailUsuario);
			} catch (Exception e) {
				System.out.println("[ERROR] Error al enviar copia al usuario " + emailUsuario + ": " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("[WARNING] No hay email del usuario disponible para enviar copia");
		}

		System.out.println("\n[SUMMARY] RESUMEN DE ENVIO:");
		System.out.println("[OK] Correos enviados exitosamente: " + enviados);
		System.out.println("[ERROR] Errores durante envio: " + errores);
		System.out.println("================================================================================\n");
		
		// Agregar mensaje de éxito o error a la sesión
		if (errores == 0 && enviados > 0) {
			SessionMessages.add(actionRequest, "formulario-enviado-exitosamente");
		} else if (errores > 0) {
			SessionErrors.add(actionRequest, "error-envio-parcial");
		}
		
		// Post-Redirect-Get: redirigir a la URL actual para evitar reenvío de formulario
		String redirect = ParamUtil.getString(actionRequest, "redirect");
		System.out.println("[REDIRECT] URL de redirección: [" + redirect + "]");

		if (redirect != null && !redirect.trim().isEmpty()) {
			System.out.println("[REDIRECT] Ejecutando redirección...");
			actionResponse.sendRedirect(redirect);
		}
	}
}
