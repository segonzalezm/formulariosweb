package cl.cgr.formulariosweb.portlet.command;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author sgonzalezm
 */
@Component(
	property = {
		"javax.portlet.name=" + FormularioswebPortletKeys.FORMULARIOSWEB,
		"mvc.command.name=/formularios/getUserData"
	},
	service = MVCResourceCommand.class
)
public class GetUserDataMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		try {
			// Obtener themeDisplay del request
			ThemeDisplay themeDisplay =
				(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

			// Extraer datos del usuario autenticado
			String email = themeDisplay.getUser().getEmailAddress();
			String screenName = themeDisplay.getUser().getScreenName();
			String fullName = themeDisplay.getUser().getFullName();

			// Si el email viene vacío, intentar obtenerlo de otras fuentes
			if (email == null || email.isEmpty()) {
				email = themeDisplay.getUser().getEmailAddresses().isEmpty() ? 
					"" : themeDisplay.getUser().getEmailAddresses().get(0).getAddress();
			}

			// Responder con JSON
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"success", true
				).put(
					"screenName", screenName
				).put(
					"email", email != null ? email : ""
				).put(
					"fullName", fullName
				)
			);

		} catch (Exception e) {
			try {
				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put(
						"success", false
					).put(
						"error", e.getMessage()
					)
				);
			} catch (Exception ignored) {
			}
		}

		return false;
	}
}
