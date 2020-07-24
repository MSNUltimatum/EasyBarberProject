package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.BarberService;
import com.dreamteam.easybarber.domain.MasterPortfolio;
import com.dreamteam.easybarber.domain.Roles;
import com.dreamteam.easybarber.domain.User;
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
@PreAuthorize("hasAuthority('MASTER')")
@RequestMapping("portfolio")
public class PortfolioController {

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
    public String getPortfolio(@AuthenticationPrincipal User user, Model model)
    {
        if(user.getRoles().contains(Roles.MASTER))
        {
            model.addAttribute("user", user);
            model.addAttribute("choseDate", LocalDate.now().toString());
            Collection<BarberService> all = barberServiceRepo.findAllByMstrServiceEmbed_MasterId(user.getId());
            List<String> names = new ArrayList<>();
            for(BarberService service : all)
            {
                int i = Arrays.asList(DefaultServicesService.mainPageServices).indexOf(service.getServices().getName());
                service.getServices().setName(DefaultServicesService.serviceShortcut[i]);
            }
            model.addAttribute("servises", all);
            model.addAttribute("portfolioPhotos", masterPortfolioRepo.findAllByMasterId(user.getId()));
            return "master-portfolio";
        }
        return "redirect:/personalArea";
    }

    @PostMapping("photoLoading")
    public String loadPhoto(  @AuthenticationPrincipal User user
            , @RequestParam("file") MultipartFile file
            , @RequestParam String description) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            MasterPortfolio masterPortfolio = new MasterPortfolio();
            masterPortfolio.setMasterId(user.getId());
            masterPortfolio.setPhotoUrl(resultFilename);
            masterPortfolio.setDescription(description);
            masterPortfolioRepo.save(masterPortfolio);
        }
        return "redirect:/portfolio";
    }

    @Transactional
    @PostMapping("deletePortfolioFoto/{photo}")
    public String deletePhotoFromPotfolio( @AuthenticationPrincipal User user
            , @PathVariable("photo") String photo)
    {
        MasterPortfolio byPhotoUrl = masterPortfolioRepo.findByPhotoUrl(photo);
        if (!photo.equals("") &&  !byPhotoUrl.equals(null)) {
            masterPortfolioRepo.delete(byPhotoUrl);
            File oldFile = new File(uploadPath + "/" + photo);
            oldFile.delete();
        }
        return "redirect:/portfolio";
    }
}
