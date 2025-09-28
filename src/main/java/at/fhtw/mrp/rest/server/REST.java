package at.fhtw.mrp.rest.server;

import at.fhtw.mrp.rest.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface REST {
    String path() default "";

    HttpMethod method();

    boolean authRequired() default true;
}
