package cl.cgr.formulariosweb.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailServiceUtil;
import javax.mail.internet.InternetAddress;

public class EmailUtil {
    public static void enviarCorreo(String destinatario, String asunto, String cuerpo, String remitente) throws Exception {
        System.out.println("===== EMAILUTIL DEBUG =====");
        System.out.println("[SENDING] INTENTANDO ENVIAR EMAIL");
        System.out.println("Remitente: " + remitente);
        System.out.println("Destinatario: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Cuerpo: " + cuerpo.substring(0, Math.min(100, cuerpo.length())));
        System.out.println("============================");
        
        try {
            InternetAddress to = new InternetAddress(destinatario);
            InternetAddress from = new InternetAddress(remitente);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setTo(to);
            mailMessage.setFrom(from);
            mailMessage.setSubject(asunto);
            mailMessage.setBody(cuerpo);
            mailMessage.setHTMLFormat(true);
            
            System.out.println("Mensaje de correo creado exitosamente");
            System.out.println("Enviando correo...");
            
            MailServiceUtil.sendEmail(mailMessage);
            
            System.out.println("[OK] CORREO ENVIADO EXITOSAMENTE a: " + destinatario);
        } catch (Exception e) {
            System.out.println("[ERROR] AL ENVIAR CORREO: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
