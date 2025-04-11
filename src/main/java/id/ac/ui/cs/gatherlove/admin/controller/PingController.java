package id.ac.ui.cs.gatherlove.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/")
    public String ping() {
        return "running";
    }
}