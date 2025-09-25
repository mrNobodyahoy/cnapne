package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service; // ou outro pacote de sua preferência

import org.springframework.data.jpa.domain.Specification;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

public class ProfessionalSpecification {

    public static Specification<Professional> hasFullName(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (query == null || query.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), query.toLowerCase() + "%");
        };
    }

    public static Specification<Professional> isActive(Boolean active) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("active"), active);
        };
    }

    public static Specification<Professional> hasRole(Role role) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (role == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("profile").get("name"), role);
        };
    }
}