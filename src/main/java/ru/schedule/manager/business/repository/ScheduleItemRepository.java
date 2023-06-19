package ru.schedule.manager.business.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.schedule.manager.business.dictionary.Times;
import ru.schedule.manager.business.entity.ScheduleItem;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long>, JpaSpecificationExecutor<ScheduleItem> {

	Optional<ScheduleItem> findByRowAndColAndTimes(Integer row, Integer col, Times times);

	@Query("select max(id) from schedule_item")
	Long getMaxId();

}
