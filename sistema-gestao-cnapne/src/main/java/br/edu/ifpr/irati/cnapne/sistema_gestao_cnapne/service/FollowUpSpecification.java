package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;

public class FollowUpSpecification {

    public static Specification<FollowUp> hasStudent(UUID studentId) {
        return (root, query, cb) -> studentId == null ? cb.conjunction()
                : cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<FollowUp> hasProfessional(UUID professionalId) {
        return (root, query, cb) -> professionalId == null ? cb.conjunction()
                : cb.equal(root.join("professionals").get("id"), professionalId);
    }

    public static Specification<FollowUp> hasStatus(String status) {
        return (root, query, cb) -> (status == null || status.trim().isEmpty()) ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<FollowUp> sessionDateIsBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null)
                return cb.conjunction();
            if (startDate != null && endDate != null)
                return cb.between(root.get("sessionDate"), startDate, endDate);
            if (startDate != null)
                return cb.greaterThanOrEqualTo(root.get("sessionDate"), startDate);
            return cb.lessThanOrEqualTo(root.get("sessionDate"), endDate);
        };
    }
}