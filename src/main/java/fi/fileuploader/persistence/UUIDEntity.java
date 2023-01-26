package fi.fileuploader.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public abstract class UUIDEntity extends BaseEntity<UUID> {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false)
    private UUID id;
}
