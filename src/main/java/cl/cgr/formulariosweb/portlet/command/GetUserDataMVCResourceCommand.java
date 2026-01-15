package cl.cgr.formulariosweb.portlet.command;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	private static final Log _log =
		LogFactoryUtil.getLog(GetUserDataMVCResourceCommand.class);

	private static final String JNDI_DATASOURCE_NAME = "java:jboss/Usuario";

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

			// Obtener dependencia desde la base de datos
			String dependencia = obtenerDependenciaDelUsuario(email);

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
				).put(
					"dependencia", dependencia != null ? dependencia : ""
				)
			);

		} catch (Exception e) {
			_log.error("Error obteniendo datos del usuario", e);
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

	private DataSource getDataSource(String jndiName) throws Exception {
		Context ctx = new InitialContext();
		return (DataSource) ctx.lookup(jndiName);
	}

	private String obtenerDependenciaDelUsuario(String email) {
		if (email == null || email.trim().isEmpty()) {
			return "";
		}

		String sql = "SELECT dependencia FROM funcionario.view_funcionario_intranet WHERE email = ?";

		try (Connection connection = getDataSource(JNDI_DATASOURCE_NAME).getConnection();
			 PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, email.trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String dependencia = rs.getString("dependencia");
					return dependencia != null ? dependencia : "";
				}
			}
		}
		catch (SQLException sqle) {
			_log.error("Error SQL buscando dependencia para " + email, sqle);
		}
		catch (Exception e) {
			_log.error("Error obteniendo datasource para dependencia", e);
		}

		_log.info("No se encontró dependencia para el email: " + email);
		return "";
	}
}
