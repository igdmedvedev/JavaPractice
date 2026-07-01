package org.example.entity.common;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String title() default "";
    boolean nullable() default true;
    int length() default 255;
}
