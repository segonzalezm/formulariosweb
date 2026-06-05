package cl.cgr.formulariosweb.portlet.command;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;
import cl.cgr.formulariosweb.util.EmailUtil;

/**
 * MVC Action Command para enviar formularios
 * 
 * @author sgonzalezm
 */
@Component(immediate = true, property = {
		"javax.portlet.name=" + FormularioswebPortletKeys.FORMULARIOSWEB,
		"mvc.command.name=enviarFormulario"
}, service = MVCActionCommand.class)
public class EnviarFormularioMVCActionCommand extends BaseMVCActionCommand {

	private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
	private static final int MAX_TEXT_LENGTH = 255;
	private static final int MAX_DESCRIPCION_LENGTH = 4000;

	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		// Obtener parámetros del formulario
		String nombreApellido = ParamUtil.getString(actionRequest, "nombreApellido", "").trim();
		String dependencia = ParamUtil.getString(actionRequest, "dependencia", "").trim();
		String telefonoCelular = ParamUtil.getString(actionRequest, "telefonoCelular", "").trim();
		String telefonoParticular = ParamUtil.getString(actionRequest, "telefonoParticular", "").trim();
		String emailFuncionario = ParamUtil.getString(actionRequest, "emailFuncionario", "").trim();
		String emailParticular = ParamUtil.getString(actionRequest, "emailParticular", "").trim();
		String descripcion = ParamUtil.getString(actionRequest, "descripcion", "").trim();

		if (!isValidFormData(
				nombreApellido, dependencia, telefonoCelular, telefonoParticular,
				emailFuncionario, emailParticular, descripcion)) {
			SessionErrors.add(actionRequest, "error-validacion-formulario");
			redirectSafely(actionRequest, actionResponse);
			return;
		}

		// Obtener configuración desde PortletPreferences
		PortletPreferences prefs = actionRequest.getPreferences();
		String destinatarios = ParamUtil.getString(actionRequest, "destinatarios", "").trim();
		String asunto = prefs.getValue("asuntoEmail", "");

		if (Validator.isNull(destinatarios)) {
			destinatarios = prefs.getValue("destinatariosEmail", "");
		}
		destinatarios = Validator.isNotNull(destinatarios) ? destinatarios.trim() : "";
		asunto = Validator.isNotNull(asunto) ? asunto.trim() : "";

		if (Validator.isNull(destinatarios)) {
			System.out.println("\n[ERROR] No hay destinatarios disponibles!");
			SessionErrors.add(actionRequest, "error-configuracion-formulario");
			redirectSafely(actionRequest, actionResponse);
			return;
		}
		if (Validator.isNull(asunto)) {
			System.out.println("\n[ERROR] No hay asunto disponible!");
			SessionErrors.add(actionRequest, "error-configuracion-formulario");
			redirectSafely(actionRequest, actionResponse);
			return;
		}

		String safeNombreApellido = HtmlUtil.escape(nombreApellido);
		String safeDependencia = HtmlUtil.escape(dependencia);
		String safeTelefonoCelular = HtmlUtil.escape(telefonoCelular);
		String safeTelefonoParticular = HtmlUtil.escape(telefonoParticular);
		String safeEmailFuncionario = HtmlUtil.escape(emailFuncionario);
		String safeEmailParticular = HtmlUtil.escape(emailParticular);

		// Convertir saltos de línea a <br> para que se vean correctamente en HTML
		String descripcionHTML = HtmlUtil.escape(descripcion)
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
				"<p>Se ha recibido una nueva solicitud a trav&eacute;s del formulario de contacto. A continuaci&oacute;n se detalla la informaci&oacute;n del remitente:</p>"
				+
				"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
				"<h3 style=\"color: #003366; margin-bottom: 10px;\">Informaci&oacute;n del Funcionario</h3>" +
				"<table style=\"width: 100%; border-collapse: collapse;\">" +
				"<tr><td style=\"padding: 8px; font-weight: bold; width: 200px;\">Nombre y Apellido:</td><td style=\"padding: 8px;\">"
				+ safeNombreApellido + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Dependencia:</td><td style=\"padding: 8px;\">"
				+ safeDependencia + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Celular:</td><td style=\"padding: 8px;\">"
				+ safeTelefonoCelular + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Particular:</td><td style=\"padding: 8px;\">"
				+ safeTelefonoParticular + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Email:</td><td style=\"padding: 8px;\">"
				+ safeEmailFuncionario + "</td></tr>" +
				"<tr><td style=\"padding: 8px; font-weight: bold;\">Email Particular:</td><td style=\"padding: 8px;\">"
				+ safeEmailParticular + "</td></tr>" +
				"</table>" +
				"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
				"<h3 style=\"color: #003366; margin-bottom: 10px;\">Descripci&oacute;n de la Solicitud</h3>" +
				"<p style=\"background-color: #f5f5f5; padding: 15px; border-left: 4px solid #003366;\">"
				+ descripcionHTML + "</p>" +
				"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
				"<p style=\"font-size: 12px; color: #666;\">Este es un mensaje automatizado. Por favor, no responda directamente a este correo.</p>"
				+
				"</body>" +
				"</html>";

		String remitente = "noreply@contraloria.cl";

		String[] destinatariosArray = destinatarios.split(",");
		int enviados = 0;
		int errores = 0;

		for (String destinatario : destinatariosArray) {
			String destinatarioTrimmed = destinatario.trim();
			if (destinatarioTrimmed.isEmpty()) {
				continue;
			}

			try {
				EmailUtil.enviarCorreo(destinatarioTrimmed, asunto, cuerpo, remitente);
				enviados++;
			} catch (Exception e) {
				e.printStackTrace();
				errores++;
			}
		}

		// Enviar copia al usuario que realiza la solicitud
		String emailUsuario = emailFuncionario;
		if (emailUsuario != null && !emailUsuario.trim().isEmpty()) {
			String asuntoCopia = "COPIA SOLICITUD - " + asunto;
			String cuerpoConfirmacion = "<!DOCTYPE html>" +
					"<html>" +
					"<head>" +
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
					"</head>" +
					"<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
					"<p>Estimado/a " + safeNombreApellido + ",</p>" +
					"<p>Le enviamos una copia de la solicitud que ha realizado a trav&eacute;s del formulario de contacto de la Contralor&iacute;a General de la Rep&uacute;blica.</p>"
					+
					"<p>Esta es una confirmaci&oacute;n de que su solicitud ha sido recibida exitosamente.</p>" +
					"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
					"<h3 style=\"color: #003366; margin-bottom: 10px;\">Informaci&oacute;n de su Solicitud</h3>" +
					"<table style=\"width: 100%; border-collapse: collapse;\">" +
					"<tr><td style=\"padding: 8px; font-weight: bold; width: 200px;\">Nombre y Apellido:</td><td style=\"padding: 8px;\">"
					+ safeNombreApellido + "</td></tr>" +
					"<tr><td style=\"padding: 8px; font-weight: bold;\">Dependencia:</td><td style=\"padding: 8px;\">"
					+ safeDependencia + "</td></tr>" +
					"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Celular:</td><td style=\"padding: 8px;\">"
					+ safeTelefonoCelular + "</td></tr>" +
					"<tr><td style=\"padding: 8px; font-weight: bold;\">Tel&eacute;fono Particular:</td><td style=\"padding: 8px;\">"
					+ safeTelefonoParticular + "</td></tr>" +
					"<tr><td style=\"padding: 8px; font-weight: bold;\">Email:</td><td style=\"padding: 8px;\">"
					+ safeEmailFuncionario + "</td></tr>" +
					"<tr><td style=\"padding: 8px; font-weight: bold;\">Email Particular:</td><td style=\"padding: 8px;\">"
					+ safeEmailParticular + "</td></tr>" +
					"</table>" +
					"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
					"<h3 style=\"color: #003366; margin-bottom: 10px;\">Descripci&oacute;n de su Solicitud</h3>" +
					"<p style=\"background-color: #f5f5f5; padding: 15px; border-left: 4px solid #003366;\">"
					+ descripcionHTML + "</p>" +
					"<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
					"<p style=\"font-size: 12px; color: #666;\">Este es un mensaje automatizado. Por favor, no responda directamente a este correo.</p>"
					+
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

		// Agregar mensaje de éxito o error a la sesión
		if (errores == 0 && enviados > 0) {
			SessionMessages.add(actionRequest, "formulario-enviado-exitosamente");
		} else if (errores > 0) {
			SessionErrors.add(actionRequest, "error-envio-parcial");
		}

		redirectSafely(actionRequest, actionResponse);
	}

	private boolean isValidFormData(
			String nombreApellido, String dependencia, String telefonoCelular,
			String telefonoParticular, String emailFuncionario,
			String emailParticular, String descripcion) {

		if (Validator.isNull(nombreApellido) || Validator.isNull(dependencia) ||
				Validator.isNull(emailFuncionario) || Validator.isNull(descripcion)) {
			return false;
		}

		if (nombreApellido.length() > MAX_TEXT_LENGTH || dependencia.length() > MAX_TEXT_LENGTH ||
				emailFuncionario.length() > MAX_TEXT_LENGTH || emailParticular.length() > MAX_TEXT_LENGTH ||
				descripcion.length() > MAX_DESCRIPCION_LENGTH) {
			return false;
		}

		if (!Validator.isEmailAddress(emailFuncionario)) {
			return false;
		}

		if (Validator.isNotNull(emailParticular) && !Validator.isEmailAddress(emailParticular)) {
			return false;
		}

		if (Validator.isNotNull(telefonoCelular) && !PHONE_PATTERN.matcher(telefonoCelular).matches()) {
			return false;
		}

		if (Validator.isNotNull(telefonoParticular) && !PHONE_PATTERN.matcher(telefonoParticular).matches()) {
			return false;
		}

		return true;
	}

	private void redirectSafely(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		String redirect = ParamUtil.getString(actionRequest, "redirect");
		redirect = PortalUtil.escapeRedirect(redirect);

		if (Validator.isNotNull(redirect)) {
			actionResponse.sendRedirect(redirect);
		}
	}

}
