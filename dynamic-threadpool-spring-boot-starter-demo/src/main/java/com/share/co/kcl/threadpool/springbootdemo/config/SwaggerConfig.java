package com.share.co.kcl.threadpool.springbootdemo.config;


import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

    /**
     * 引入Knife4j提供的扩展类
     */
    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Bean
    public Docket defaultDocket() {
        return new Docket(DocumentationType.OAS_30)
                .host("")
                .apiInfo(this.defaultApiInfo())
                .groupName("API")
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.share.co.kcl.threadpool.springbootdemo")
                        .and(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)))
                .paths(PathSelectors.any())
                .build()
                //赋予插件体系
                .extensions(openApiExtensionResolver.buildExtensions("API"));
    }


    private ApiInfo defaultApiInfo() {
        return new ApiInfoBuilder()
                .title("监听器API接口文档")
                .description("接口文档")
                .contact(new Contact("kcl-co", "", "kcl.co.share@gmail.com"))
                .version("1.0")
                .build();
    }

}
