package ru.schedule.manager.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.schedule.manager.business.entity.ScheduleItem;
import ru.schedule.manager.infrastructure.base.dictionary.administered.entity.Dictionary;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long>, JpaSpecificationExecutor<ScheduleItem> {

	Optional<ScheduleItem> findByRowAndColAndTimesAndSemesterAndFacultyAndGroupAndSubgroup(Integer row, Integer col, Dictionary times, Dictionary semester, Dictionary faculty, Dictionary group, Dictionary subgroup);

	Optional<ScheduleItem> findByRowAndColAndTimesAndSemesterAndProfessor(Integer row, Integer col, Dictionary times, Dictionary semester, Dictionary professor);

	@Query("select max(id) from schedule_item")
	Optional<Long> getMaxId();

	List<ScheduleItem> findAllByRowAndColAndTimesAndSemesterAndClassroomIsNotNullAndProfessorIsNotNull(Integer row, Integer col, Dictionary times, Dictionary semester);

}
