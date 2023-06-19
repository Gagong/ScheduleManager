package ru.schedule.manager.business.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import ru.schedule.manager.business.entity.ProfessorDisciplineLnk;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

@Repository
public interface ProfessorDisciplineLnkRepository extends JpaRepository<ProfessorDisciplineLnk, Long>,
	JpaSpecificationExecutor<ProfessorDisciplineLnk> {

	Optional<ProfessorDisciplineLnk> findByProfessorAndDiscipline(Dictionary professor, Dictionary discipline);

	List<ProfessorDisciplineLnk> findByProfessor(Dictionary professor);

}
