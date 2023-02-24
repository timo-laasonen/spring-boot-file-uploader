package fi.fileuploader.config;

import fi.fileuploader.feature.auditlog.AuditAware;
import fi.fileuploader.feature.auditlog.domain.AuditEvent;
import fi.fileuploader.feature.auditlog.domain.AuditLogData;
import fi.fileuploader.feature.auditlog.domain.AuditLogFieldData;
import fi.fileuploader.persistence.BaseEntity;
import lombok.extern.log4j.Log4j2;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class AuditLogInterceptor implements Interceptor, Serializable, HibernatePropertiesCustomizer {

    private static final List<String> excludeFieldNames = new ArrayList<>();

    static {
        excludeFieldNames.addAll(
            Arrays.stream(BaseEntity.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", this);
    }

    @Override
    public boolean onSave(
        Object entity,
        Object id,
        Object[] state,
        String[] propertyNames,
        Type[] types
    ) throws CallbackException {
        if (entity instanceof AuditAware) {
            final var auditLogDataBuilder = AuditLogData.builder();
            for (int i = 0; i < propertyNames.length; i++) {
                if (excludeFieldNames.contains(propertyNames[i])) {
                    continue;
                }
                final var auditRow = AuditLogFieldData.builder()
                    .fieldName(propertyNames[i])
                        .currentValue(Optional.ofNullable(state[i]).map(Object::toString).orElse("null"))
                    .build();
                auditLogDataBuilder.property(auditRow);
            }
            final var auditLogData = auditLogDataBuilder
                .auditEvent(AuditEvent.INSERT.name())
                .entityName(entity.getClass().getCanonicalName())
                .id(id.toString())
                .build();
            log.info("INSERT: {} ", auditLogData.toString());
        }

        return Interceptor.super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(
        Object entity,
        Object id,
        Object[] currentState,
        Object[] previousState,
        String[] propertyNames,
        Type[] types
    ) throws CallbackException {
        if (entity instanceof AuditAware) {
            final var auditLogDataBuilder = AuditLogData.builder();
            for (int i = 0; i < propertyNames.length; i++) {
                if (excludeFieldNames.contains(propertyNames[i])
                    || Objects.deepEquals(previousState[i], currentState[i])) {
                    continue;
                }
                final var auditRow = AuditLogFieldData.builder()
                    .fieldName(propertyNames[i])
                    .previousValue(Optional.ofNullable(previousState[i]).map(Object::toString).orElse("null"))
                    .currentValue(Optional.ofNullable(currentState[i]).map(Object::toString).orElse("null"))
                    .build();
                auditLogDataBuilder.property(auditRow);
            }
            final var auditLogData = auditLogDataBuilder
                .auditEvent(AuditEvent.UPDATE.name())
                .entityName(entity.getClass().getCanonicalName())
                .id(id.toString())
                .build();
            log.info("UPDATE: {} ", auditLogData.toString());
        }

        return Interceptor.super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
