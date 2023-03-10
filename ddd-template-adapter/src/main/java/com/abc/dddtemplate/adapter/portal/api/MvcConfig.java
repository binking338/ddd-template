package com.abc.dddtemplate.adapter.portal.api;

import com.abc.dddtemplate.adapter.portal.api.interceptor.ClearContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author <template/>
 * @date
 */
@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ClearContextInterceptor()).addPathPatterns("/**");
    }
}
