package com.polarbookshop.catalog_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

//Define a classe como uma fonte para propriedades de configuração
@ConfigurationProperties(prefix = "polar")
public class PolarProperties {

    private String greeting;

    public PolarProperties(String greeting) {
        this.greeting = greeting;
    }


    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
