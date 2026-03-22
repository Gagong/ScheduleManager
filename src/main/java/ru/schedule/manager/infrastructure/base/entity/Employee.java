package ru.schedule.manager.infrastructure.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "employee")
@Table(name = "employee")
public class Employee extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Formula("CONCAT_WS(' ', first_name, last_name, middle_name)")
    private String fullName;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    @ColumnDefault("true")
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "account_non_expired", nullable = false)
    @ColumnDefault("true")
    private boolean accountNonExpired = true;

    @Builder.Default
    @Column(name = "account_non_locked", nullable = false)
    @ColumnDefault("true")
    private boolean accountNonLocked = true;

    @Builder.Default
    @Column(name = "credentials_non_expired", nullable = false)
    @ColumnDefault("true")
    private boolean credentialsNonExpired = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "EMPLOYEE_ROLES",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

}
