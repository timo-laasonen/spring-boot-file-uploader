package fi.example.persistence.userinfo;

import fi.example.persistence.UUIDEntity;
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
public class UserInfo extends UUIDEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Getter(lazy = true)
    @Transient
    private final String fullName = this.firstName + " " + this.lastName;
}
