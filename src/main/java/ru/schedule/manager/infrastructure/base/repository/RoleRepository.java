package ru.schedule.manager.infrastructure.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.schedule.manager.infrastructure.base.dictionary.Roles;
import ru.schedule.manager.infrastructure.base.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByName(Roles name);

}
