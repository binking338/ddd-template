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

    /**
     * 用于多个文档情形，文档地址会加分组名称 /v3/api-docs/{group}，knife4j ui支持
     * @return
     */
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = {"/**"};
        return GroupedOpenApi.builder()
                .group(applicationName)
                .pathsToMatch(paths)
                .packagesToScan("com.abc.dddtemplate.adapter.portal.api.controller")
                .addOperationCustomizer((operation, handlerMethod) -> operation)
                .addOpenApiCustomiser(openApi -> openApi.info(new Info()
                        .title(applicationName)
                        .version(applicationVersion) ))
                .build();
    }

    /**
     * 用于单个文档情形，文档地址是统一的 /v3/api-docs，knife4j ui不支持
     * 可配置项
     * springdoc.packagesToScan=package1, package2
     * springdoc.pathsToMatch=/v1, /api/balance/**
     * @return
     */
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

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        log.info("swagger URL: http://localhost:" + serverPort + serverServletContentPath + "/swagger-ui/index.html");
        log.info("knife4j URL: http://localhost:" + serverPort + serverServletContentPath + "/doc.html");
    }
}
