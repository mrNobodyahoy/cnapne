package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service; // Pode colocar no pacote que preferir

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import jakarta.persistence.criteria.Join;

public class ServiceSpecification {

    public static Specification<Service> studentNameContains(String studentName) {
        return (root, query, cb) -> {
            if (studentName == null || studentName.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<Service, Student> studentJoin = root.join("student");
            return cb.like(cb.lower(studentJoin.get("completeName")), studentName.toLowerCase() + "%");
        };
    }

    public static Specification<Service> hasStudent(UUID studentId) {
        return (root, query, cb) -> studentId == null ? cb.conjunction()
                : cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<Service> hasProfessional(UUID professionalId) {
        return (root, query, cb) -> professionalId == null ? cb.conjunction()
                : cb.equal(root.join("professionals").get("id"), professionalId);
    }

    public static Specification<Service> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Service> sessionDateIsBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return cb.conjunction();
            }
            if (startDate != null && endDate != null) {
                return cb.between(root.get("sessionDate"), startDate, endDate);
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("sessionDate"), startDate);
            }
            return cb.lessThanOrEqualTo(root.get("sessionDate"), endDate);
        };
    }
}