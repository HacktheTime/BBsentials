package de.hype.bbsentials.client.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MessageSubscribe {
    String name();
    boolean enabled() default true;
}
