package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.BaseServices;
import com.dreamteam.easybarber.domain.Roles;
import com.dreamteam.easybarber.domain.User;
import com.dreamteam.easybarber.helpResources.RandomPasswordGenerator;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import com.dreamteam.easybarber.repos.UserRepo;
import com.dreamteam.easybarber.service.DefaultServicesService;
import com.dreamteam.easybarber.service.MailService;
import com.dreamteam.easybarber.service.UserService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private BaseServicesRepo baseServicesRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${mail.path}")
    private String path;

    @PostConstruct
    public void init()
    {
        User userFromDb = userRepo.findByPhonenumber("admin");
        if(userFromDb == null)
        {
            User admin = new User();
            RandomPasswordGenerator passGen = new RandomPasswordGenerator();
            String password = passGen.generateCommonLangPassword();
            admin.setPassword(password);
            admin.setUsername("admin");
            admin.setPhonenumber("admin");
            admin.setAccounttype("administrator");
            Set<Roles> adminRoles = new HashSet<Roles>();
            adminRoles.add(Roles.USER);
            adminRoles.add(Roles.ADMINISTRATOR);
            admin.setRoles(adminRoles);
            userRepo.save(admin);
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", null);
        String[] citys = DefaultServicesService.citys;
        Arrays.sort(citys);
        model.addAttribute("citys", citys);
        model.addAttribute("sizeCitys", DefaultServicesService.citys.length);
        return "/login";
    }

    @GetMapping("/registration")
    public String getRegistration(Model model)
    {
        model.addAttribute("message", null);
        return "redirect:/login";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        User userFromDb = userRepo.findByPhonenumber(user.getPhonenumber());
        //User usrByMail = userRepo.findByEmail(user.getEmail());
        if (userFromDb != null /*|| usrByMail != null*/) {
            model.addAttribute("message", "User exists!");
            String[] citys = DefaultServicesService.citys;
            Arrays.sort(citys);
            model.addAttribute("citys", citys);
            return "login";
        }
        if(user.getCity() != null && user.getAccounttype() != null) {

            String role = user.getAccounttype();
            if (role.equals("user")) {
                user.setRoles(Collections.singleton(Roles.USER));
            } else {
                Set<Roles> userRoles = new HashSet<Roles>();
                userRoles.add(Roles.USER);
                userRoles.add(Roles.MASTER);
                user.setRoles(userRoles);
            }
            user.setActivationCode(UUID.randomUUID().toString().toUpperCase().substring(0, 4));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
            if (!user.getEmail().isEmpty()) {
                String message = String.format(
                        "Hello, %s! \n" +
                                "Welcome to Easy barber. Please, visit next link: http://localhost:8080/activate/%s\n" +
                                "Regards, \"Easy barber\".",
                        user.getUsername(),
                        user.getActivationCode()
                );

                mailService.send(user.getEmail(), "Activation code", message);
            }
            model.addAttribute("message", "We have sent you an activation code by mail.");
            return "login";
        }
        else
        {
            model.addAttribute("message", "Ooops, какие то данные некорректны.");
            return "login";
        }
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }

    @GetMapping("/rebootPass")
    public String pageReboot(Model model)
    {
        model.addAttribute("sender", null);
        return "reboot-password";
    }

    @PostMapping("/rebootPass/mailActivate")
    public String checkPhone(@RequestParam String phonenumber, Model model)
    {
        User user = userRepo.findByPhonenumber(phonenumber);
        if(user != null)
        {
            user.setActivationCode(UUID.randomUUID().toString().toUpperCase().substring(0, 4));
            userRepo.save(user);
            if (!user.getEmail().isEmpty()) {
                String message = String.format(
                        "Hellow, %s! \n" +
                                "follow the link to confirm the password change: %s/reboot/%s \n" +
                                "Regards, \"Easy barber\".",
                        user.getUsername(),
                        path,
                        user.getActivationCode()
                );

                mailService.send(user.getEmail(), "Reboot password", message);
            }
            model.addAttribute("message", "We have sent you an activation code by mail.");
            model.addAttribute("sender", null);
            return "reboot-password";
        }
        else
        {
            model.addAttribute("sender", null);
            model.addAttribute("message", "User is not exist");
            return "reboot-password";
        }
    }

    @GetMapping("/reboot/{code}")
    public String reboot(Model model, @PathVariable String code) {
        User user = userRepo.findByActivationCode(code);
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "User successfully activated");
            model.addAttribute("sender", 1);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("sender", null);
            model.addAttribute("message", "Activation code is not found!");
        }

        return "reboot-password";
    }

    @PostMapping("/rebootPass/newPass")
    public String newPassword(@RequestParam String phonenumber, @RequestParam String firstVersion, @RequestParam String secondVersion, Model model)
    {
        User user = userRepo.findByPhonenumber(phonenumber);
        if(firstVersion.equals(secondVersion))
        {
            user.setPassword(passwordEncoder.encode(firstVersion));
            userRepo.save(user);
            model.addAttribute("message", "Password changed successfully");
            return "login";
        }
        else
        {
            model.addAttribute("message", "Passwords are different");
            model.addAttribute("sender", 1);
            model.addAttribute("user", user);
            return "reboot-password";
        }
    }
}