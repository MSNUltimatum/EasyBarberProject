package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.BarberService;
import com.dreamteam.easybarber.domain.Roles;
import com.dreamteam.easybarber.domain.User;
import com.dreamteam.easybarber.repos.BarberServiceRepo;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import com.dreamteam.easybarber.repos.MasterPortfolioRepo;
import com.dreamteam.easybarber.repos.UserRepo;
import com.dreamteam.easybarber.service.DefaultServicesService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/masterList")
public class masterListController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BarberServiceRepo barberServiceRepo;
    @Autowired
    private MasterPortfolioRepo masterPortfolioRepo;
    @Autowired
    private BaseServicesRepo baseServicesRepo;

    @GetMapping("page/{pageNum}")
    public String defMasterList(@AuthenticationPrincipal User user,@PathVariable String pageNum,  Model model)
    {
        if(user != null && user.getActivationCode() == null)
            model.addAttribute("user", user);
        else
            model.addAttribute("user", -1);
        List<User> masterList;
        if(!model.containsAttribute("masterList")) {
            masterList = (List<User>) userRepo.findAllByRolesContaining(Roles.MASTER);
        }
        else
        {
             masterList = (List<User>) model.getAttribute("masterList");
        }
        List<User> pageMasters = new ArrayList<>();
        int masterCount = 20;

        for(int i = (Integer.parseInt(pageNum) - 1) * masterCount; i < masterCount  +  (Integer.parseInt(pageNum) - 1) * masterCount; i++)
        {
            if(i < masterList.size())
                pageMasters.add(masterList.get(i));
        }
        List<Integer> pageNums = new ArrayList<>();
        Double ceil = (double) (masterList.size()) / masterCount;
        for(int i = 0; i < Math.ceil(ceil); i++)
            pageNums.add(i + 1);
        model.addAttribute("masterList" ,pageMasters);
        model.addAttribute("pageCount", pageNums);
        model.addAttribute("nowDate", LocalDate.now().toString());
        model.addAttribute("services" ,baseServicesRepo.findAll());
        model.addAttribute("citys", DefaultServicesService.citys);
        model.addAttribute("user", user);
        return "master-list";
    }
    @GetMapping("/MasterFrUsr/{masterId}")
    public String masterProfFrmUser( @AuthenticationPrincipal User user
                                   , @PathVariable Long masterId
                                   , Model model)
    {
        User master = userRepo.findById(masterId).get();
        if(user != null && user.getId().equals(master.getId())) {
            return "redirect:/personalArea";
        }
        if(!master.getRoles().contains(Roles.MASTER))
        {
            return "redirect:/masterList/page/1";
        }
        model.addAttribute("master", master);
        model.addAttribute("choseDate", LocalDate.now().toString());
        Collection<BarberService> all = barberServiceRepo.findAllByMstrServiceEmbed_MasterId(master.getId());
        for(BarberService service : all)
        {
            int i = Arrays.asList(DefaultServicesService.mainPageServices).indexOf(service.getServices().getName());
            service.getServices().setName(DefaultServicesService.serviceShortcut[i]);
        }
        model.addAttribute("serviseShortcuts", all);
        model.addAttribute("portfolioPhotos", masterPortfolioRepo.findAllByMasterId(master.getId()));
        if(user != null && user.getActivationCode() == null)
            model.addAttribute("user", user);
        else
            model.addAttribute("user", -1);
        return "master-portfolio-from-user";
    }

    @PostMapping("filtr")
    public String filtr( @AuthenticationPrincipal User user
                       , @RequestParam String username
                       , @RequestParam String city
                       , @RequestParam String phone
                       , @RequestParam String userService
                       , Model model)
    {
        if(!phone.equals(""))
        {
            return "redirect:/masterList/MasterFrUsr/" + userRepo.findByPhonenumber(phone).getId();
        }
        else if(!username.equals("") && !city.equals("-1") && !userService.equals("-1"))
        {
            Collection<BarberService> embed_serviceId = barberServiceRepo.findAllByMstrServiceEmbed_ServiceId(baseServicesRepo.findByName(userService).getId());
            Collection<User> users = new ArrayList<>();
            for (BarberService service:embed_serviceId
                 ) {
                users.add(userRepo.findByRatingsContaining(service));
            }
            Collection<User> users1 = userRepo.findAllByUsernameAndCityAndRolesContaining(username, city, Roles.MASTER);
            users1.retainAll(users);
            model.addAttribute("masterList", users1);
        }
        else if(!username.equals("") && !city.equals("-1"))
        {
            model.addAttribute("masterList", userRepo.findAllByUsernameAndCityAndRolesContaining(username, city, Roles.MASTER));
        }
        else if(!username.equals("") && !userService.equals("-1"))
        {
            Collection<BarberService> embed_serviceId = barberServiceRepo.findAllByMstrServiceEmbed_ServiceId(baseServicesRepo.findByName(userService).getId());
            Collection<User> users = new ArrayList<>();
            for (BarberService service:embed_serviceId
            ) {
                users.add(userRepo.findByRatingsContaining(service));
            }
            Collection<User> users1 = userRepo.findAllByUsername(username);
            users1.retainAll(users);
            model.addAttribute("masterList", users1);
        }
        else if(!city.equals("-1") && !userService.equals("-1"))
        {
            Collection<BarberService> embed_serviceId = barberServiceRepo.findAllByMstrServiceEmbed_ServiceId(baseServicesRepo.findByName(userService).getId());
            Collection<User> users = new ArrayList<>();
            for (BarberService service:embed_serviceId
            ) {
                users.add(userRepo.findByRatingsContaining(service));
            }
            Collection<User> users1 = userRepo.findAllByCityAndRolesContaining(city, Roles.MASTER);
            users1.retainAll(users);
            model.addAttribute("masterList", users1);
        }
        else if(!username.equals(""))
        {
            model.addAttribute("masterList", userRepo.findAllByUsernameAndRolesContaining(username, Roles.MASTER));
        }
        else if(!city.equals("-1"))
        {
            model.addAttribute("masterList", userRepo.findAllByCityAndRolesContaining(city, Roles.MASTER));
        }
        else if(!userService.equals("-1"))
        {
            Collection<BarberService> embed_serviceId = barberServiceRepo.findAllByMstrServiceEmbed_ServiceId(baseServicesRepo.findByName(userService).getId());
            Collection<User> users = new ArrayList<>();
            for (BarberService service:embed_serviceId
            ) {
                users.add(userRepo.findByRatingsContaining(service));
            }
            model.addAttribute("masterList", users);
        }
        else
        {
            return "redirect:/masterList/page/1";
        }
        model.addAttribute("nowDate", LocalDate.now().toString());
        model.addAttribute("user", user);
        model.addAttribute("services" ,baseServicesRepo.findAll());
        model.addAttribute("citys", DefaultServicesService.citys);
      return "master-list";
    }

    @GetMapping("/mainPage/{serviceName}")
    public String serviceFromMainPage(@AuthenticationPrincipal User user, @PathVariable String serviceName, Model model)
    {
        String fullname = "";
        for (int i = 0;i < DefaultServicesService.serviceShortcut.length; i++) {
            if(DefaultServicesService.serviceShortcut[i].equals(serviceName))
            {
                fullname = DefaultServicesService.mainPageServices[i];
                break;
            }
        }
        return filtr(user, "", "-1", "", fullname, model);
    }
}
