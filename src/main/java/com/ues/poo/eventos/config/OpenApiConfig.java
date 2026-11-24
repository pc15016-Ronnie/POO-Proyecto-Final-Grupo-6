package com.ues.poo.eventos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Configuracion central de la documentacion OpenAPI de la API. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API para Gestion de Eventos y Asistentes")
                        .description("""
                                API REST academica del Grupo 6 para administrar ubicaciones, eventos,
                                asistentes, registros, usuarios y administradores. Incluye control de
                                aforo, registro unico y manejo uniforme de errores.
                                """)
                        .version("3.0.0")
                        .contact(new Contact()
                                .name("Grupo 6 - Programacion Orientada a Objetos")
                                .url("https://github.com/pc15016-Ronnie/POO-Proyecto-Final-Grupo-6")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Aplicacion local"),
                        new Server().url("http://localhost:30080").description("Kubernetes local - NodePort")
                ));
    }
}
