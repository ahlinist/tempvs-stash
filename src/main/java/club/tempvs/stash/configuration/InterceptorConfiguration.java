package club.tempvs.stash.configuration;

import club.tempvs.stash.interceptor.AuthInterceptor;
import club.tempvs.stash.interceptor.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final UserInfoInterceptor userInfoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
        registry.addInterceptor(userInfoInterceptor);
    }
}
