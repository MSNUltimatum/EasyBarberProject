package com.dreamteam.easybarber.controller;

import com.dreamteam.easybarber.domain.*;
import com.dreamteam.easybarber.repos.BarberServiceRepo;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import com.dreamteam.easybarber.repos.ReceptionTimeRepoE;
import com.dreamteam.easybarber.repos.UserRepo;
import org.dom4j.rule.Mode;
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
import java.sql.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


@Controller
@PreAuthorize("hasAuthority('MASTER')")
@RequestMapping("receptiontimeE")
public class ReceptionTimeControllerE {
    @Autowired
    ReceptionTimeRepoE receptionTimeRepoE;

    @Autowired
    BaseServicesRepo baseServicesRepo;

    @Autowired
    BarberServiceRepo barberServiceRepo;

    @Autowired
    UserRepo userRepo;

    @Value("${upload.path}")
    private String uploadPath;

    public ReceptionTimeControllerE()
    {
        for(int i = 10; i <= 45; i+=5)
            spans.put(String.valueOf(i) + " минут", LocalTime.of(0, i));
        spans.put("1 час", LocalTime.of(1,0));
        spans.put("1.5 часа", LocalTime.of(1,30));
        spans.put("2 часa", LocalTime.of(2,0));
    }

    @GetMapping("timetable/{choseDate}")
    public String receptionCustom(@AuthenticationPrincipal User user,@PathVariable String choseDate ,Model model)
    {
        java.util.Date utilDate = new java.util.Date();
        Date date = new Date(utilDate.getTime());
        LocalTime time =  LocalTime.now(ZoneId.systemDefault());
        String currTime = time.toString().substring(0,5);
        List<LocalDate> dates = new ArrayList<LocalDate>();
        LocalDate currentDate = LocalDate.now();
        for(int i = 0;i < 30; i++)
        {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        Collection<ReceptionTimeE> timeDate = receptionTimeRepoE.findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_Date(user.getId(), choseDate);
        model.addAttribute("dates", dates);
        model.addAttribute("currentTime", currTime);
        model.addAttribute("currentDate", date);
        model.addAttribute("statuses", Statuses.values());
        model.addAttribute("spans", spans.keySet());
        model.addAttribute("servises", barberServiceRepo.findAllByMstrServiceEmbed_MasterId(user.getId()));
        model.addAttribute("reseptionTime", receptionTimeRepoE.findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndStatus(user.getId(), choseDate, Statuses.WAITING));
        return "master-schedule";
    }

    @PostMapping("/receptionRegistration")
    public String saveService( @RequestParam String date
                             , @RequestParam String time
                             , @RequestParam String userName
                             , @RequestParam String userPhone
                             , @RequestParam String serviceType
                             , @AuthenticationPrincipal User user
                             , Model model) {

        LocalDate myDate = LocalDate.parse(date);
        LocalTime myTime = LocalTime.parse(time);
        LocalDateTime dateTime = LocalDateTime.of(myDate, myTime);

        LocalDateTime currentDate = LocalDateTime.now(ZoneId.systemDefault());
        UsrReceptionTime recep = new UsrReceptionTime(user.getId(), date, time);
        ReceptionTimeE receptionTimeE = receptionTimeRepoE.findByUsrReceptionTime(recep);

        if(receptionTimeE == null && !dateTime.isBefore(currentDate) && !userPhone.equals(user.getPhonenumber()))
        {
            ReceptionTimeE timeE = new ReceptionTimeE();
            timeE.setClientName(userName);
            timeE.setClientPhonenumber(userPhone);
            timeE.setServiceId(baseServicesRepo.findByName(serviceType).getId());
            timeE.setUsrReceptionTime(recep);
            timeE.setStatus(Statuses.WAITING);
            User client = userRepo.findByPhonenumber(userPhone);
            if(client != null)
                timeE.setClientEmail(client.getEmail());
            receptionTimeRepoE.save(timeE);
        }
        return receptionCustom(user,LocalDate.now().toString() ,model);
    }
    @PostMapping("receptionRegistrationArabian")
    public String recepTimeArabian( @RequestParam String date
                                  , @RequestParam String beginTime
                                  , @RequestParam String endTime
                                  , @RequestParam Map<String, String> wds
                                  , @AuthenticationPrincipal User user
                                  , Model model)
    {
        LocalTime myBegTime = LocalTime.parse(beginTime);
        LocalTime myEndTime = LocalTime.parse(endTime);

        HashSet<String> allSpans = new HashSet<>();
        for(String s : wds.values())
            if(spans.containsKey(s))
                allSpans.add(s);
        LocalTime delta = spans.get(allSpans.iterator().next());
        while (myBegTime.isBefore(myEndTime))
        {
            String s = myBegTime.toString();
            UsrReceptionTime recep = new UsrReceptionTime(user.getId(), date, s);
            ReceptionTimeE receptionTimeE = receptionTimeRepoE.findByUsrReceptionTime(recep);
            if(receptionTimeE == null)
            {
                ReceptionTimeE timeE = new ReceptionTimeE();
                timeE.setClientName(null);
                timeE.setClientPhonenumber(null);
                timeE.setServiceId(null);
                timeE.setUsrReceptionTime(recep);
                timeE.setStatus(Statuses.WAITING);
                receptionTimeRepoE.save(timeE);
            }
            if(delta.getHour() == 0)
               myBegTime =  myBegTime.plusMinutes(delta.getMinute());
            else
                myBegTime =  myBegTime.plusHours(delta.getHour());
        }
        return "redirect:/receptiontimeE/timetable/" + LocalDate.now().toString();
    }

    @PostMapping("/timetable")
    public String getRecipTime(@RequestParam Map<String, String> wds, @AuthenticationPrincipal User user, Model model)
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
        return "redirect:/receptiontimeE/timetable/" + date;
    }

    @PostMapping(value = "saveRecep", params = "save")
    public String recepAdmin( @RequestParam String userName
            , @RequestParam String userPhone
            , @RequestParam String date
            , @RequestParam String userTime
            , @RequestParam String userService
            , @AuthenticationPrincipal User user
            , Model model)
    {
        ReceptionTimeE receptionTimeE = receptionTimeRepoE.findByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(user.getId(), date, userTime);
        if (!userName.equals("") && !userPhone.equals("") && !userService.equals("-1") && !userPhone.equals(user.getPhonenumber()) ) {
            receptionTimeE.setClientName(userName);
            receptionTimeE.setClientPhonenumber(userPhone);
            receptionTimeE.getUsrReceptionTime().setDate(date);
            receptionTimeE.getUsrReceptionTime().setTime(userTime);
            receptionTimeE.setServiceId(baseServicesRepo.findByName(userService).getId());
            receptionTimeRepoE.save(receptionTimeE);
        }
        return "redirect:/receptiontimeE/timetable/" + date;
    }

    @PostMapping(value = "saveRecep", params = "delete")
    public String delRecep( @RequestParam String userName
            , @RequestParam String userPhone
            , @RequestParam String date
            , @RequestParam String userTime
            , @RequestParam String userService
            , @AuthenticationPrincipal User user
            , Model model)
    {
        ReceptionTimeE time = receptionTimeRepoE.findByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(user.getId(), date, userTime);
        time.setStatus(Statuses.DELETED_MASTER);
        receptionTimeRepoE.save(time);
        return "redirect:/receptiontimeE/timetable/" + date;
    }

     @GetMapping("serviceEditor")
     public String serviceEditor(@AuthenticationPrincipal User user,Model model)
     {
         model.addAttribute("services",baseServicesRepo.findAll());
         model.addAttribute("choseDate", LocalDate.now().toString());
         model.addAttribute("addedServices", barberServiceRepo.findAllByMstrServiceEmbed_MasterId(user.getId()));
         return "master-services";
     }

    @PostMapping("serviceEditor/newService")
    public String barberService( @RequestParam String userService
                               , @RequestParam String description
                               , @RequestParam String cost
                               , @AuthenticationPrincipal User user
                               , Model model)
    {
        MstrServiceEmbed mstrServiceEmbed = new MstrServiceEmbed(user.getId(), baseServicesRepo.findByName(userService).getId());
        if(barberServiceRepo.findByMstrServiceEmbed(mstrServiceEmbed) == null && tryParseLong(cost))
        {
            BarberService barberService = new BarberService();
            barberService.setMstrServiceEmbed(mstrServiceEmbed);
            barberService.setCost(Long.parseLong(cost));
            barberService.setCount((long) 0);
            barberService.setMaster(user);
            barberService.setServices(baseServicesRepo.findByName(userService));
            barberService.setDescription(description);
            barberService.setCount((long) 0);
            barberServiceRepo.save(barberService);
        }
        return "redirect:/receptiontimeE/serviceEditor";
    }

    @PostMapping("serviceEditor/saveService")
    public String saveService( @AuthenticationPrincipal User user
                             , @RequestParam String serviceName
                             , @RequestParam String serviceDescription
                             , @RequestParam String cost
                             , Model model)
    {
        if(tryParseLong(cost)) {
            BarberService currService = barberServiceRepo.findByMstrServiceEmbed(new MstrServiceEmbed(user.getId(), baseServicesRepo.findByName(serviceName).getId()));
            currService.setCost(Long.parseLong(cost));
            currService.setDescription(serviceDescription);
            barberServiceRepo.save(currService);
        }
        return "redirect:/receptiontimeE/serviceEditor";
    }

    @GetMapping("myClients")
    public String myClients(@AuthenticationPrincipal User user, Model model)
    {
        Collection<ReceptionTimeE> time_masterId = receptionTimeRepoE.findAllByUsrReceptionTime_MasterIdAndStatus(user.getId(), Statuses.COMPLIT);
        Long totalIncome = 0L;
        Long monthIncome = 0L;
        for (ReceptionTimeE recep: time_masterId
             ) {
            Long serviceCost = barberServiceRepo.findByMstrServiceEmbed(new MstrServiceEmbed(user.getId(), recep.getServiceId())).getCost();
            totalIncome += serviceCost;
            if(LocalDate.parse(recep.getUsrReceptionTime().getDate()).getMonth().equals(LocalDate.now().getMonth()))
                monthIncome += serviceCost;
        }
        model.addAttribute("receps", time_masterId);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("monthIncome", monthIncome);
        model.addAttribute("choseDate", LocalDate.now().toString());
        return "master-client-statistic";
    }

    boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLegalDate(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(s, new ParsePosition(0)) != null;
    }
    private HashMap<String, LocalTime> spans = new HashMap<String, LocalTime>();
}
