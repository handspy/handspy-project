package pt.up.hs.project.web.rest.users;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.Collection;

public class WithMockNormalUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Collection authorities = new ArrayList<>();
        for (String role: customUser.roles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUser.username(), "password", authorities);
        context.setAuthentication(auth);
        return context;
    }
}
