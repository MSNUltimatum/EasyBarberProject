package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.BaseServices;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
@RequestMapping("serviceEditor")
public class ServiceController {
    @Autowired
    BaseServicesRepo baseServicesRepo;


    @GetMapping
    public String serviceEditorMain(Model model)
    {
        model.addAttribute("exception", "");
        model.addAttribute("currentServices", baseServicesRepo.findAll());
        return "AdminServiceEditor";
    }

    @PostMapping("newService")
    public String savService( @RequestParam String serviceName
                            , Model model)
    {
        BaseServices thisService = baseServicesRepo.findByName(serviceName);
        if(serviceName.equals("")) {
            model.addAttribute("exception", "Fail :(");
            model.addAttribute("currentServices", baseServicesRepo.findAll());
            return "AdminServiceEditor";
        }
        if(thisService == null) {
            BaseServices baseServices = new BaseServices(serviceName);
            baseServicesRepo.save(baseServices);
        }
        return "redirect:/serviceEditor";
    }
}
