package ru.schedule.manager.infrastructure.base.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.schedule.manager.infrastructure.base.dictionary.Roles;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")
@Table(name = "roles")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private Roles name;

    public String getName() {
        return this.name.name();
    }

}
