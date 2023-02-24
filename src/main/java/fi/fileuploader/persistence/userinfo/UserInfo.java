package fi.fileuploader.persistence.userinfo;

import fi.fileuploader.feature.auditlog.AuditAware;
import fi.fileuploader.persistence.UUIDEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_info")
public class UserInfo extends UUIDEntity implements AuditAware {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Getter(lazy = true)
    @Transient
    private final String fullName = this.firstName + " " + this.lastName;
}
