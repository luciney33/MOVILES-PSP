package org.example.emailspring.ui.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
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
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Activación de cuenta - Sistema");
            helper.setText(construirMensajeActivacion(nombreUsuario, codigoActivacion), true);
            mailSender.send(mensaje);
        } catch (Exception e) {
            log.error("Error al enviar correo a {}", destinatario, e);
            System.err.println("Error al enviar correo: " + e.getMessage());
            // En desarrollo, mostramos el código en consola
            System.out.println("=================================================");
            System.out.println("CÓDIGO DE ACTIVACIÓN PARA: " + destinatario);
            System.out.println("Código: " + codigoActivacion);
            System.out.println("=================================================");
        }
    }

    private String construirMensajeActivacion(String nombreUsuario, String codigoActivacion) {
        Context context = new Context();
        context.setVariable("nombreUsuario", nombreUsuario);
        context.setVariable("codigoActivacion", codigoActivacion);
        return templateEngine.process("email-activacion", context);
    }
}

