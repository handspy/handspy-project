package pt.up.hs.project.web.rest.users;

import org.springframework.security.test.context.support.WithSecurityContext;
import pt.up.hs.project.security.AuthoritiesConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockNormalUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "bob";

    String firstname() default "Bob";

    String lastname() default "";

    String email() default "bob@handspy.up.pt";

    String[] roles() default {AuthoritiesConstants.USER};
}
