package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import jakarta.persistence.criteria.Join;

public class FollowUpSpecification {

    public static Specification<FollowUp> studentNameContains(String studentName) {
        return (root, query, cb) -> {
            if (studentName == null || studentName.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<FollowUp, Student> studentJoin = root.join("student");
            return cb.like(cb.lower(studentJoin.get("completeName")), studentName.toLowerCase() + "%");
        };
    }

    public static Specification<FollowUp> hasStudent(UUID studentId) {
        return (root, query, cb) -> studentId == null ? cb.conjunction()
                : cb.equal(root.get("student").get("id"), studentId);
    }

    public static Specification<FollowUp> hasProfessional(UUID professionalId) {
        return (root, query, cb) -> professionalId == null ? cb.conjunction()
                : cb.equal(root.join("professionals").get("id"), professionalId);
    }

    public static Specification<FollowUp> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
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

    public static Specification<FollowUp> sessionDateIsIn(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sessionDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThan(root.get("sessionDate"), endDate));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    public static Specification<FollowUp> byProfessionalAndDate(UUID professionalId, LocalDate startDate,
            LocalDate endDate) {
        return Specification.where(hasProfessional(professionalId))
                .and(sessionDateIsIn(startDate, endDate));
    }
}