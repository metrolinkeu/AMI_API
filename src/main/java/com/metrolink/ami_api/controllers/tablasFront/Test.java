package com.metrolink.ami_api.controllers.tablasFront;


import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/test")
public class Test {

    @GetMapping
    public void test() {
        System.out.println("estoy en test");
        
        return;
    }

}
