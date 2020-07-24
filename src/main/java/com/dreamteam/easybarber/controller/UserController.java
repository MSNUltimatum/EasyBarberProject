package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.*;
import com.dreamteam.easybarber.repos.*;
import com.dreamteam.easybarber.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ReceptionTimeRepoE receptionTimeRepoE;

    @Autowired
    private BarberServiceRepo barberServiceRepo;

    @Autowired
    private BaseServicesRepo baseServicesRepo;

    @Autowired
    private ReceptionRepo receptionRepo;

    @GetMapping
    public String userList(Model model)
    {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("date", LocalDate.now().toString());
        return"userList";
    }

    @GetMapping("{user}/timetable/{date}")
    public String userEditForm(@PathVariable User user,@PathVariable String date, Model model) {
            List<LocalDate> dates = new ArrayList<LocalDate>();
            LocalDate currentDate = LocalDate.now();
            for(int i = 0;i < 20; i++)
            {
                dates.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
            model.addAttribute("dates", dates);
            model.addAttribute("servises", barberServiceRepo.findAllByMstrServiceEmbed_MasterId(user.getId()));
            model.addAttribute("reseptionTime", receptionTimeRepoE.findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_Date(user.getId(), date));

        return "UserEdit";
    }

    @GetMapping("{user}/editProfile")
    public String profile(@PathVariable User user, Model model)
    {
        model.addAttribute("user", user);
        model.addAttribute("roles", Roles.values());
        return "profileEditor";
    }

    @PostMapping("{user}/timetable")
    public String getRecipTime(@RequestParam Map<String, String> wds, @PathVariable User user, Model model)
    {
        HashMap<String, String> all = new HashMap<>();
        String date = "";
        for(String k : wds.keySet())
            if(isLegalDate(k))
            {
                date = k;
                model.addAttribute("reseptionTime", receptionTimeRepoE.findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_Date(user.getId(), k));
                break;
            }
        return "redirect:/user/{user}/timetable/" + date;
    }

    @PostMapping(value = "{user}/edit", params = "save")
    public String recepAdmin( @RequestParam String userName
                            , @RequestParam String userPhone
                            , @RequestParam String userTime
                            , @RequestParam String id
                            , @RequestParam String userService
                            , @PathVariable User user
                            , Model model)
    {
        ReceptionTimeE receptionTimeE = receptionTimeRepoE.findByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(user.getId(), id, userTime);
        if (userName != "" && userPhone != "" &&  userService != "-1") {
            receptionTimeE.setClientName(userName);
            receptionTimeE.setClientPhonenumber(userPhone);
            receptionTimeE.getUsrReceptionTime().setDate(id);
            receptionTimeE.getUsrReceptionTime().setTime(userTime);
            receptionTimeE.setServiceId(baseServicesRepo.findByName(userService).getId());
            receptionTimeRepoE.save(receptionTimeE);
        }
        return "redirect:/user/{user}/timetable/" + LocalDate.now().toString();
    }

    @Transactional
    @PostMapping(value = "{user}/edit", params = "delete")
    public String delRecep( @RequestParam String userTime
                          , @RequestParam String id
                          , @PathVariable User user)
    {
        receptionTimeRepoE.deleteByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(user.getId(), id, userTime);
        return "redirect:/user/{user}/timetable/" + LocalDate.now().toString();
    }

    @PostMapping("{user}/editProfileSave")
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Roles.values())
                .map(Roles::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Roles.valueOf(key));
            }
        }

        userRepo.save(user);

        return "redirect:/user";
    }
    private boolean isLegalDate(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(s, new ParsePosition(0)) != null;
    }
}

