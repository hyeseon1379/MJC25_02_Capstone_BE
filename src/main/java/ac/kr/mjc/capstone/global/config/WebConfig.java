package ac.kr.mjc.capstone.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        // 로컬 개발용 주소
                        "http://localhost/",
                        "http://localhost/",
                        "http://localhost:8080",  // 프론트엔드 개발 서버
                        "http://127.0.0.1/",
                        "http://127.0.0.1/",
                        "http://127.0.0.1:8080/",
                        "http://localhost:8080/"
                        // 배포 서버용 주소
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // preflight 요청 캐싱 (1시간)
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/").addResourceLocations("file:uploads/");
    }
}