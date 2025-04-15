package INU.software_design.common.config;

import INU.software_design.common.resolver.UserIdResolver;
import INU.software_design.common.usertype.UserTypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UserIdResolver userIdResolver;
    private final UserTypeResolver userTypeResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdResolver);
        resolvers.add(userTypeResolver);
    }
}
