package ch.bloodpressure.domain;

import jakarta.persistence.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Table(indexes = {
        @Index(name = "idx_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public @interface IndexedEntity {
}
