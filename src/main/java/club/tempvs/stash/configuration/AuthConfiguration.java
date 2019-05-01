package club.tempvs.stash.configuration;

import club.tempvs.stash.holder.UserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class AuthConfiguration {

    @Bean
    @RequestScope
    public UserHolder userHolder() {
        return new UserHolder();
    }
}
