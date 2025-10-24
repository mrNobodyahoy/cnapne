package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config.RabbitMQConfig;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.EmailDto;

@Service
public class EmailPublisherService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void scheduleEmail(EmailDto emailDto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_EMAIL, emailDto);
    }
}