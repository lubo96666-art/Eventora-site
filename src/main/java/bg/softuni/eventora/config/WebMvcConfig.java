package bg.softuni.eventora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final SessionAccessInterceptor sessionAccessInterceptor;

    public WebMvcConfig(SessionAccessInterceptor sessionAccessInterceptor) {
        this.sessionAccessInterceptor = sessionAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionAccessInterceptor)
                .addPathPatterns("/checkout", "/my-tickets/**", "/admin/**");
    }
}
