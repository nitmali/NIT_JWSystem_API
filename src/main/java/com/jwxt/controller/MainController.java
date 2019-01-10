package com.jwxt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author me@nitmali.com
 * @date 2019/1/10 22:51
 */
@Controller
public class MainController {


    @GetMapping("/")
    public String main() {
        return "/client/index.html";
    }

}
