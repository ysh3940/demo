package com.shenhui.demo.control;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class HealthControl {

    @GetMapping("/ok")
    public String ok() {
        return "ok";
    }


}
