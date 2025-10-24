package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config.RabbitMQConfig;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.EmailDto;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailConsumerService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void sendEmail(EmailDto emailDto) {
        log.info("Recebido pedido para enviar e-mail para: {}", emailDto.getTo());
        try {
            Context context = new Context();
            context.setVariables(emailDto.getProps());

            String html = templateEngine.process(emailDto.getTemplate(), context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom("seu_email_completo@gmail.com");
            helper.setTo(emailDto.getTo());
            helper.setSubject(emailDto.getSubject());
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("E-mail enviado com sucesso para: {}", emailDto.getTo());

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para {}: {}", emailDto.getTo(), e.getMessage());
        }
    }
}
