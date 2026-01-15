package cl.cgr.formulariosweb.portlet.command;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;
import cl.cgr.formulariosweb.util.EmailUtil;

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

		// Validar que hay destinatarios
		if (destinatarios == null || destinatarios.trim().isEmpty()) {
			System.out.println("\n[ERROR] No hay destinatarios disponibles!");
			System.out.println("================================================================================");
			return;
		}

		// Validar que hay asunto
		if (asunto == null || asunto.trim().isEmpty()) {
			System.out.println("\n[ERROR] No hay asunto disponible!");
			System.out.println("================================================================================");
			return;
		}

		// Construir cuerpo del email
		String cuerpo = "<b>Datos del Funcionario:</b><br>" +
			"* Nombre y Apellido: " + nombreApellido + "<br>" +
			"* Dependencia: " + dependencia + "<br>" +
			"* Telefono Celular: " + telefonoCelular + "<br>" +
			"* Telefono Particular: " + telefonoParticular + "<br>" +
			"* Email Funcionario: " + emailFuncionario + "<br>" +
			"* Email Particular: " + emailParticular + "<br><br>" +
			"<b>Descripcion de la Solicitud:</b><br>" + descripcion;

		String remitente = "portal.contraloria@cgr.cl";

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

		System.out.println("\n[SUMMARY] RESUMEN DE ENVIO:");
		System.out.println("[OK] Correos enviados exitosamente: " + enviados);
		System.out.println("[ERROR] Errores durante envio: " + errores);
		System.out.println("================================================================================\n");
	}
}
