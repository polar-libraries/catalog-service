package com.polarbookshop.catalog_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PedidoController {

    public String welcome(){
        return "Nada";
    }
}
