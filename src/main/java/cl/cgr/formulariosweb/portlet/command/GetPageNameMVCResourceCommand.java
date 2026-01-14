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
		"mvc.command.name=/formularios/getPageName"
	},
	service = MVCResourceCommand.class
)
public class GetPageNameMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		try {
			// Obtener themeDisplay del request
			ThemeDisplay themeDisplay =
				(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

			// Obtener el nombre de la página (layout)
			String pageName = "";
			if (themeDisplay.getLayout() != null) {
				// Obtener el title de la página que es lo que se muestra en la navegación
				pageName = themeDisplay.getLayout().getName(
					themeDisplay.getLocale());
				
				// Si no tiene nombre, intentar con friendly URL
				if (pageName == null || pageName.isEmpty()) {
					pageName = themeDisplay.getLayout().getFriendlyURL();
					// Limpiar la URL amigable (quitar / y convertir - a espacio)
					if (pageName != null && !pageName.isEmpty()) {
						pageName = pageName.replaceAll("^/|/$", "")
							.replace("-", " ");
						// Capitalizar primera letra de cada palabra
						pageName = capitalizeWords(pageName);
					}
				}
			}

			// Responder con JSON
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"success", true
				).put(
					"pageName", pageName != null ? pageName : ""
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

	/**
	 * Capitaliza la primera letra de cada palabra
	 */
	private String capitalizeWords(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		String[] words = str.split(" ");
		StringBuilder result = new StringBuilder();

		for (String word : words) {
			if (!word.isEmpty()) {
				result.append(Character.toUpperCase(word.charAt(0)))
					.append(word.substring(1).toLowerCase());
				result.append(" ");
			}
		}

		return result.toString().trim();
	}
}
