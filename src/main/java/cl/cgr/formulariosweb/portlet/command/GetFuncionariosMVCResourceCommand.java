package cl.cgr.formulariosweb.portlet.command;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

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
		"mvc.command.name=/formularios/getFuncionarios"
	},
	service = MVCResourceCommand.class
)
public class GetFuncionariosMVCResourceCommand implements MVCResourceCommand {

	private static final Log _log =
		LogFactoryUtil.getLog(GetFuncionariosMVCResourceCommand.class);

	private static final String JNDI_DATASOURCE_NAME = "java:jboss/Usuario";

	@Override
	public boolean serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		try {
			_log.info("Iniciando obtención de funcionarios...");
			JSONArray funcionarios = obtenerFuncionarios();
			_log.info("Funcionarios obtenidos: " + funcionarios.length());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"success", true
				).put(
					"funcionarios", funcionarios
				)
			);

		} catch (Exception e) {
			_log.error("Error obteniendo funcionarios", e);
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

	private JSONArray obtenerFuncionarios() {
		JSONArray result = JSONFactoryUtil.createJSONArray();
		String sql = "SELECT nombre, email, dependencia FROM funcionario.view_funcionario_intranet ORDER BY nombre";

		try (Connection connection = getDataSource(JNDI_DATASOURCE_NAME).getConnection();
			 PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			_log.info("DESDE EL GET Consultando funcionarios desde base de datos...");
			
			int count = 0;
			while (rs.next()) {
				   String nombre = rs.getString("nombre");
				   String email = rs.getString("email");
				   String dependencia = rs.getString("dependencia");
				
				if (email == null || email.trim().isEmpty()) {
					continue;
				}
				
				count++;
				JSONObject funcionario = JSONFactoryUtil.createJSONObject();
				
				funcionario.put("nombre", nombre != null ? nombre : "nombre");
				funcionario.put("email", email);
				funcionario.put("dependencia", dependencia != null ? dependencia : "");
				
				if (count == 1) {
					_log.info("Ejemplo funcionario - nombre: " + nombre + ", email: " + email + ", dependencia: " + dependencia);
					_log.info("JSON creado: " + funcionario.toString());
				}
				
				result.put(funcionario);
			}
			
			_log.info("Total funcionarios procesados: " + count);
		}
		catch (SQLException sqle) {
			_log.error("Error SQL consultando funcionarios", sqle);
		}
		catch (Exception e) {
			_log.error("Error obteniendo datasource o ejecutando consulta", e);
		}

		return result;
	}
}
