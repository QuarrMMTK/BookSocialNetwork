package mmtk.projects.backend.config;

import mmtk.projects.backend.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 24
 * Project Name : BookSocialNetwork
 **/
public class ApplicationAuditAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken
                || authentication == null
                || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        User userPrinciple = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrinciple.getId());
    }
}
