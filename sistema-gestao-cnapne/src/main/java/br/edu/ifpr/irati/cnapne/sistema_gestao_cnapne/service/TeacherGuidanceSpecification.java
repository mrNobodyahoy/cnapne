package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.TeacherGuidance;
import jakarta.persistence.criteria.Join;

public class TeacherGuidanceSpecification {

    public static Specification<TeacherGuidance> studentNameContains(String studentName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(studentName)) {
                return criteriaBuilder.conjunction();
            }
            Join<TeacherGuidance, Student> studentJoin = root.join("student");
            return criteriaBuilder.like(criteriaBuilder.lower(studentJoin.get("completeName")),
                    studentName.toLowerCase() + "%");
        };
    }

    public static Specification<TeacherGuidance> authoredBy(UUID authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {

                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("author").get("id"), authorId);
        };

    }

    public static Specification<TeacherGuidance> domiciliarEquals(Boolean domiciliar) {
        return (root, query, criteriaBuilder) -> {
            if (domiciliar == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("domiciliar"), domiciliar);
        };
    }

}