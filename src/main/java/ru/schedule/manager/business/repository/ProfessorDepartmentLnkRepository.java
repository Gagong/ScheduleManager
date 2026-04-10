package ru.schedule.manager.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.schedule.manager.business.entity.ProfessorDepartmentLnk;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorDepartmentLnkRepository extends JpaRepository<ProfessorDepartmentLnk, Long>, JpaSpecificationExecutor<ProfessorDepartmentLnk> {

	Optional<ProfessorDepartmentLnk> findFirstByProfessor(Dictionary professor);

	List<ProfessorDepartmentLnk> findByDepartment(Dictionary department);

	Optional<ProfessorDepartmentLnk> findByProfessor(Dictionary professor);

}
