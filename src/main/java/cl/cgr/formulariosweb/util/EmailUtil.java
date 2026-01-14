package cl.cgr.formulariosweb.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailServiceUtil;
import javax.mail.internet.InternetAddress;

public class EmailUtil {
    public static void enviarCorreo(String destinatario, String asunto, String cuerpo, String remitente) throws Exception {
        InternetAddress to = new InternetAddress(destinatario);
        InternetAddress from = new InternetAddress(remitente);
        MailMessage mailMessage = new MailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom(from);
        mailMessage.setSubject(asunto);
        mailMessage.setBody(cuerpo);
        mailMessage.setHTMLFormat(true);
        MailServiceUtil.sendEmail(mailMessage);
    }
}
