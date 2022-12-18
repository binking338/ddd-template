package com.abc.dddtemplate.adapter.configure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger文档配置
 *
 * @author <template/>
 * @date 2018/11/5
 */
@Configuration
@Slf4j
public class SwaggerConfig implements ApplicationListener<WebServerInitializedEvent> {
    @Value("${spring.application.name:need-a-name}")
    private String applicationName;
    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;
    @Value("${springdoc.packagesToScan}")
    private String[] packagesToScan;

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = {"/**"};
        return GroupedOpenApi.builder()
                .group(applicationName)
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .addOperationCustomizer((operation, handlerMethod) -> operation)
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .version(applicationVersion)
                        .description(""));
    }

    @Value("${server.port:80}")
    private String serverPort;
    @Value("${server.servlet.context-path:/}")
    private String serverServletContentPath;
    @Value("${springdoc.swagger-ui.path:/index.html}")
    private String swaggeruiPath;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        log.info("swagger URL: http://localhost:" + serverPort + serverServletContentPath + "/swagger-ui" + swaggeruiPath);
    }
}
