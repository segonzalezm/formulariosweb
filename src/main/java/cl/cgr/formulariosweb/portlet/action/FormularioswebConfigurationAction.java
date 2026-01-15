package cl.cgr.formulariosweb.portlet.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;

import cl.cgr.formulariosweb.constants.FormularioswebPortletKeys;

@Component(
    immediate = true,
    property = {
        "javax.portlet.name=" + FormularioswebPortletKeys.FORMULARIOSWEB
    },
    service = ConfigurationAction.class
)
public class FormularioswebConfigurationAction extends DefaultConfigurationAction {

    @Override
    public void include(
        PortletConfig portletConfig, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) throws Exception {
        
        System.out.println("======================================");
        System.out.println("DEBUG - include() llamado - Mostrando configuración");
        System.out.println("======================================");
        
        super.include(portletConfig, httpServletRequest, httpServletResponse);
    }

    @Override
    public void processAction(
        PortletConfig portletConfig, ActionRequest actionRequest,
        ActionResponse actionResponse) throws Exception {
        
        System.out.println("======================================");
        System.out.println("DEBUG - processAction() llamado - Guardando configuración");
        
        String destinatariosEmail = ParamUtil.getString(actionRequest, "preferences--destinatariosEmail--");
        String asuntoEmail = ParamUtil.getString(actionRequest, "preferences--asuntoEmail--");
        
        System.out.println("DEBUG - Parámetro destinatarios recibido: [" + destinatariosEmail + "]");
        System.out.println("DEBUG - Parámetro asunto recibido: [" + asuntoEmail + "]");
        
        setPreference(actionRequest, "destinatariosEmail", destinatariosEmail);
        setPreference(actionRequest, "asuntoEmail", asuntoEmail);
        
        System.out.println("DEBUG - Preferencias guardadas");
        System.out.println("======================================");
        
        super.processAction(portletConfig, actionRequest, actionResponse);
        
        SessionMessages.add(actionRequest, "request_processed");
    }
}
