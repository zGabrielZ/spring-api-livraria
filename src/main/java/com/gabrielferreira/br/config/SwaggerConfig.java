package com.gabrielferreira.br.config;

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

@EnableSwagger2
@Configuration // Para o Spring Boot reconhecer que é uma classe de configuração
public class SwaggerConfig {

	// Esse objeto vai ter toda a configuração necessário para o Swagger funcionar
	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.gabrielferreira.br.controller"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo());
	}
	
	// Informações da API, Título da Api, Descrição, Versão e Contato
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("API Livraria").description("Controle de gestão referente a livros.")
				.version("1.0").contact(contact()).build();
	}
	
	// Informações do Contato do desenvolvedor
	private Contact contact() {
		return new Contact("Gabriel Ferreira",
				"https://zgabrielz.github.io/git-pages-portfolio/index.html", 
				"ferreiragabriel2612@gmail.com");
	}
}
