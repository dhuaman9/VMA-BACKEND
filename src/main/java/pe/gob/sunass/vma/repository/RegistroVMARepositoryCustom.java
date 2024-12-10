package pe.gob.sunass.vma.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import java.util.Objects;
import java.util.Collections;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.dto.RegistroVMAFilterDTO;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;

@Repository
public interface RegistroVMARepositoryCustom
		extends JpaRepository<RegistroVMA, Integer>, JpaSpecificationExecutor<RegistroVMA> {

	default List<RegistroVMA> findByFilters(RegistroVMAFilterDTO filterDTO) {
		return findAll((root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (Objects.nonNull(filterDTO.getEmpresa()) && StringUtils.hasText(filterDTO.getEmpresa())) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("empresa").get("nombre")),
						"%" + filterDTO.getEmpresa().toLowerCase() + "%"));
			}

			if (Objects.nonNull(filterDTO.getAnio())) {
				predicates.add(criteriaBuilder.equal(root.get("fichaRegistro").get("anio"), filterDTO.getAnio()));
			}

			if (Objects.nonNull(filterDTO.getEstado()) && StringUtils.hasText(filterDTO.getEstado())) {
				predicates.add(criteriaBuilder.equal(root.get("estado"), filterDTO.getEstado()));
			}

			if (Objects.nonNull(filterDTO.getFechaDesde())) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(LocalDate.class),
						filterDTO.getFechaDesde()));
			}

			if (Objects.nonNull(filterDTO.getFechaHasta())) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(LocalDate.class),
						filterDTO.getFechaHasta()));
			}

			// Si no se proporciona ningún filtro, devolver todos los registros
			if (predicates.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		});
	}

}
