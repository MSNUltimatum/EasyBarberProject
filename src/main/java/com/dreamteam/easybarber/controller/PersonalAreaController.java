package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.*;
import com.dreamteam.easybarber.repos.BarberServiceRepo;
import com.dreamteam.easybarber.repos.MasterPortfolioRepo;
import com.dreamteam.easybarber.repos.ReceptionTimeRepoE;
import com.dreamteam.easybarber.repos.UserRepo;
import com.dreamteam.easybarber.service.DefaultServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

@Controller
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("personalArea")
public class PersonalAreaController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    ReceptionTimeRepoE receptionTimeRepoE;
    @Autowired
    BarberServiceRepo barberServiceRepo;
    @Autowired
    MasterPortfolioRepo masterPortfolioRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String profile(@AuthenticationPrincipal User user, Model model)
    {
        model.addAttribute("master", user);
        model.addAttribute("masterRole", Roles.MASTER);
        model.addAttribute("choseDate", LocalDate.now().toString());
        return "personal-area";
    }

    @PostMapping("saveInf")
    public String saveInf( @AuthenticationPrincipal User user
                           , @RequestParam("file") MultipartFile file
                           , @RequestParam String description
                           ,@RequestParam String city
                           , @RequestParam String username
                           , @RequestParam String instagram
                           , Model model) throws IOException {
        User thisUser = userRepo.findByPhonenumber(user.getPhonenumber());
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            if(user.getImgFileName() != null) {
                File oldFile = new File(uploadPath + "/" + user.getImgFileName());
                oldFile.delete();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            user.setImgFileName(resultFilename);
        }
        user.setDescription(description);
        if(city != null && city != "" && Arrays.asList(DefaultServicesService.citys).contains(city)) {
            String s = city.substring(0, 1);
            city = city.replaceFirst(s, s.toUpperCase());
            user.setCity(city);
        }
        if(username != null && !username.equals(""))
            user.setUsername(username);
        userRepo.save(user);
        return "redirect:/personalArea";
    }
}
