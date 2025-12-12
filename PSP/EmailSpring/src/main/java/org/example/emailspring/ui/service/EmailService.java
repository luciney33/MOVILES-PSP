package org.example.emailspring.ui.service;

import jakarta.mail.internet.MimeMessage;
import org.example.emailspring.common.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEmailActivacion(String destinatario, String nombreUsuario, String codigoActivacion) {

        try
        {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, Constantes.UTF_8);

            helper.setTo(destinatario);
            helper.setSubject(Constantes.ACTIVACION_DE_CUENTA_SISTEMA);
            helper.setText(construirMensajeActivacion(nombreUsuario, codigoActivacion), true);
            mailSender.send(mensaje);
        } catch (Exception e) {
            log.error(Constantes.ERROR_AL_ENVIAR_CORREO_A, destinatario, e);
            System.err.println(Constantes.ERROR_AL_ENVIAR_CORREO + e.getMessage());

        }
    }

    private String construirMensajeActivacion(String nombreUsuario, String codigoActivacion) {
        Context context = new Context();
        context.setVariable(Constantes.NOMBRE_USUARIO, nombreUsuario);
        context.setVariable(Constantes.CODIGO_ACTIVACION, codigoActivacion);
        return templateEngine.process(Constantes.EMAIL_ACTIVACION, context);
    }
}

