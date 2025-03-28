package com.app.springnovels.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockMemberSecurityContextFactory.class)
public @interface MockMember {
    long id() default 1L;
    String displayName() default "reader";
    String username() default "test@test.com";
    String role() default "ROLE_MEMBER";
}
