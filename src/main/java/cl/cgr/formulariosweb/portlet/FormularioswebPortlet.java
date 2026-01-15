
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
		String nombreApellido = actionRequest.getParameter("nombreApellido");
		String dependencia = actionRequest.getParameter("dependencia");
		String telefonoCelular = actionRequest.getParameter("telefonoCelular");
		String telefonoParticular = actionRequest.getParameter("telefonoParticular");
		String emailFuncionario = actionRequest.getParameter("emailFuncionario");
		String emailParticular = actionRequest.getParameter("emailParticular");
		String asunto = actionRequest.getParameter("asunto");
		String descripcion = actionRequest.getParameter("descripcion");
		String destinatarios = actionRequest.getParameter("destinatarios");

		String cuerpo = "<b>Datos del Funcionario:</b><br>" +
			"• Nombre y Apellido: " + nombreApellido + "<br>" +
			"• Dependencia: " + dependencia + "<br>" +
			"• Teléfono Celular: " + telefonoCelular + "<br>" +
			"• Teléfono Particular: " + telefonoParticular + "<br>" +
			"• Email Funcionario: " + emailFuncionario + "<br>" +
			"• Email Particular: " + emailParticular + "<br><br>" +
			"<b>Descripción de la Solicitud:</b><br>" + descripcion;

		String remitente = "portal.contraloria@cgr.cl";

		if (destinatarios != null && !destinatarios.isEmpty()) {
			for (String destinatario : destinatarios.split(",")) {
				EmailUtil.enviarCorreo(destinatario.trim(), asunto, cuerpo, remitente);
			}
		}
	}
}