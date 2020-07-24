package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.Roles;
import com.dreamteam.easybarber.domain.User;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    BaseServicesRepo baseServicesRepo;

    @GetMapping("/")
    public String greeting(@AuthenticationPrincipal User user,Model model) {
        if(user != null && user.getActivationCode() == null)
            model.addAttribute("user", user);
        else
            model.addAttribute("user", -1);
        model.addAttribute("admin", Roles.ADMINISTRATOR);

        return "index";
    }

}
