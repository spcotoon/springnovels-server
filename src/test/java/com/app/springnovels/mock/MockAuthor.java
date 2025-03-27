package com.app.springnovels.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockAuthorSecurityContextFactory.class)
public @interface MockAuthor {
    long id() default 1L;
    String displayName() default "author";
    String username() default "test@spring.novels.author";
    String role() default "ROLE_AUTHOR";
}
