package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto implements Serializable {
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> props;
}
