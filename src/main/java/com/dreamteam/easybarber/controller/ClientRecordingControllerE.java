package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.*;
import com.dreamteam.easybarber.repos.BarberServiceRepo;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import com.dreamteam.easybarber.repos.ReceptionTimeRepoE;
import com.dreamteam.easybarber.repos.UserRepo;
import com.dreamteam.easybarber.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/client-recording")
@PreAuthorize("hasAuthority('USER')")
public class ClientRecordingControllerE {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BarberServiceRepo barberServiceRepo;
    @Autowired
    private ReceptionTimeRepoE receptionTimeRepoE;
    @Autowired
    private BaseServicesRepo baseServicesRepo;
    @Autowired
    private MailService mailService;

    @GetMapping
    public String searchMaster() {
        return "SearchMaster";
    }

    @GetMapping("seachMaster")
    public String getImageRefs(@RequestParam(required = false, defaultValue = "-1") Long serviceId, Model model)
    {
        return seachMoney("", "", serviceId, model);
    }

    @PostMapping("seachMaster")
    public String seachMoney( @RequestParam(required = false, defaultValue = "") String name
                            , @RequestParam(required = false, defaultValue = "") String phone
                            , @RequestParam(required = false, defaultValue = "-1") Long serviceId
                            , Model model)
    {
        if(!phone.isEmpty())
        {
            User master = userRepo.findByPhonenumber(phone);
            if(master != null && master.getRoles().contains(Roles.MASTER))
                return "redirect:/client-recording/" + master.getId() + "/timetable/" + LocalDate.now().toString();
        }
        if(!name.isEmpty())
        {
            Collection<User> masterWithName = userRepo.findAllByUsername(name).stream().filter(u -> u.getRoles().contains(Roles.MASTER)).collect(Collectors.toList());
            if(masterWithName.size() > 1)
            {
                model.addAttribute("sameMasters", masterWithName);
                model.addAttribute("choseDate", LocalDate.now().toString());
                return "SearchMaster";
            }
            else if(masterWithName.size() == 1)
            {
                return "redirect:/client-recording/" + masterWithName.iterator().next().getId() + "/timetable/" + LocalDate.now().toString();
            }
        }
        if(serviceId != -1)
        {
            // этой витиеватой конструкцией мы ищем мастеров, удовлетворяющих данному id услуги
            // (если бы у нас не было композитного ключа, это писалось бы несколько проще)
            Collection<User> mastersWithService = userRepo.findAll().stream().filter(m -> m.getRatings().stream().anyMatch(bs -> bs.getMstrServiceEmbed().getServiceId().equals(serviceId))).collect(Collectors.toList());
            if(mastersWithService.size() > 1)
            {
                model.addAttribute("sameMasters", mastersWithService);
                model.addAttribute("choseDate", LocalDate.now().toString());
                return "SearchMaster";
            }
            else if(mastersWithService.size() == 1)
            {
                return "redirect:/client-recording/" + mastersWithService.iterator().next().getId() + "/timetable/" + LocalDate.now().toString();
            }
        }
        return "redirect:/client-recording";
    }

    @GetMapping("{masterId}/timetable/{choseDate}")
    public String addRecepFromUsr( @AuthenticationPrincipal User user
                                 , @PathVariable("masterId") Long masterId
                                 , @PathVariable("choseDate") String choseDate
                                 , Model model)
    {
        if(user != null && !user.getId().equals(masterId)) {
            User master = userRepo.findById(masterId).get();
            Collection<ReceptionTimeE> receptionTimeControllerES = receptionTimeRepoE
                    .findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndStatus(master.getId(), choseDate, Statuses.WAITING);
            List<ReceptionTimeE> freeTimes = new ArrayList<>();
            List<LocalDate> dates = new ArrayList<LocalDate>();
            LocalDate currentDate = LocalDate.now();

            for (int i = 0; i < 30; i++) {
                dates.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
            for ( var i : receptionTimeControllerES
            ) {
                if (i.getClientPhonenumber() == null)
                    freeTimes.add(i);
            }

            model.addAttribute("dates", dates);
            model.addAttribute("choseDate", choseDate);
            model.addAttribute("masterServices", barberServiceRepo.findAllByMstrServiceEmbed_MasterId(master.getId()));
            model.addAttribute("times", freeTimes);
            model.addAttribute("master", master);
            return "reception-add";
        }
        else
        {
            return "redirect:/masterList/page/1";
        }
    }

    @PostMapping("{masterId}/timetable")
    public String getRecipTime(@RequestParam Map<String, String> wds,
                               @PathVariable("masterId") Long masterId ,
                               @AuthenticationPrincipal User user, Model model)
    {
        User master = userRepo.findById(masterId).get();
        //unused??? HashMap<String, String> all = new HashMap<>();
        String date = "";
        for(String k : wds.keySet())
            if(isLegalDate(k))
            {
                date = k;
                model.addAttribute("reseptionTime", receptionTimeRepoE.findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_Date(user.getId(), k));
                break;
            }
        return "redirect:/client-recording/" + master.getId() + "/timetable/" + date;
    }

    @PostMapping("{masterId}/saveRecep")
    public String addedReception( @PathVariable("masterId") Long masterId
                                 , @RequestParam String choseDate
                                 , @RequestParam String userTime
                                 , @RequestParam String userService
                                 , @AuthenticationPrincipal User user)
    {
        if(!userTime.equals("-1") && !userService.equals("-1"))
        {
            User master = userRepo.findById(masterId).get();
            ReceptionTimeE timeE = new ReceptionTimeE();
            timeE.setClientName(user.getUsername());
            timeE.setClientPhonenumber(user.getPhonenumber());
            timeE.setServiceId(baseServicesRepo.findByName(userService).getId());
            timeE.setUsrReceptionTime(new UsrReceptionTime(master.getId(), choseDate, userTime));
            timeE.setStatus(Statuses.WAITING);
            timeE.setClientEmail(user.getEmail());
            receptionTimeRepoE.save(timeE);
            if(!master.getEmail().isEmpty())
            {
                String message = String.format(
                        "Hellow, %s! \n" +
                                "You have a new entry! \n" +
                                "Date: %s \n" +
                                "Time: %s \n" +
                                "Client name: %s \n" +
                                "Client phone: %s \n" +
                                "Have a good day!\n" +
                                "Regards, \"Easy barber\".",
                        master.getUsername(),
                        choseDate,
                        userTime,
                        user.getUsername(),
                        user.getPhonenumber()
                );
                mailService.send(user.getEmail(), "New entry", message);
            }

        }
        return "redirect:/client-recording/" + masterId + "/timetable/" +  choseDate;
    }
    private boolean isLegalDate(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(s, new ParsePosition(0)) != null;
    }
    private HashMap<String, LocalTime> spans = new HashMap<String, LocalTime>();

    @GetMapping("clientReceps")
    public String clientReceps(@AuthenticationPrincipal User user, Model model)
    {
        Collection<ReceptionTimeE> allReceps = receptionTimeRepoE.findAllByClientPhonenumberAndStatus(user.getPhonenumber(), Statuses.WAITING);
        List<User> users = new ArrayList<>();
        for (ReceptionTimeE recep: allReceps
             ) {
            users.add(userRepo.findById(recep.getUsrReceptionTime().getMasterId()).get());
        }
        Set<User> set = new HashSet<>(users);
        users.clear();
        users.addAll(set);
        model.addAttribute("times", allReceps);
        model.addAttribute("choswDate", LocalDate.now().toString());
        model.addAttribute("user", user);
        model.addAttribute("masterRole", Roles.MASTER);
        model.addAttribute("masters", users);
        model.addAttribute("baseServices", baseServicesRepo.findAll());
        return "client-recep";
    }

    @PostMapping("delRecepFromUser")
    public String delRecepFrmUser(@AuthenticationPrincipal User user, @RequestParam String masterId, @RequestParam String date, @RequestParam String userTime)
    {
        ReceptionTimeE time = receptionTimeRepoE.findByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(Long.parseLong(masterId), date, userTime);
        if(user.getPhonenumber().equals(time.getClientPhonenumber()))
        {
            time.setStatus(Statuses.DELETED_USER);
            receptionTimeRepoE.save(time);
        }
        return "redirect:/client-recording/clientReceps";
    }
}
