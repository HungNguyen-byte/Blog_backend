/* api/config/UserDetailsConfig.java */

package backend.blog.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import backend.blog.api.repository.UserRepository;
import backend.blog.api.security.UserDetailsServiceImpl;

@Configuration
public class UserDetailsConfig {

    private final UserRepository userRepository;

    public UserDetailsConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // return a DB-backed UserDetailsService that loads users from your repository
        return new UserDetailsServiceImpl(userRepository);
    }
}