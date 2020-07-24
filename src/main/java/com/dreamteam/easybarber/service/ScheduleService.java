package com.dreamteam.easybarber.service;

import com.dreamteam.easybarber.domain.ReceptionTimeE;
import com.dreamteam.easybarber.domain.Statuses;
import com.dreamteam.easybarber.repos.BaseServicesRepo;
import com.dreamteam.easybarber.repos.ReceptionTimeRepoE;
import com.dreamteam.easybarber.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
// пробою для себя, проблемы с форком, добавлю один коммит
@Component
public class ScheduleService {
    @Autowired
    ReceptionTimeRepoE receptionTimeRepoE;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BaseServicesRepo baseServicesRepo;

    @Scheduled(cron = "0 0 0 * * *")
    private void rebootServices()
    {
        LocalDate dateYesterday = LocalDate.now().minusDays(1);//вчера дата
        Collection<ReceptionTimeE> receptionTimeRepoEAll = receptionTimeRepoE.findAllByUsrReceptionTime_Date(dateYesterday.toString());
        for (ReceptionTimeE recep: receptionTimeRepoEAll
        ) {
            if(recep.getStatus() == Statuses.WAITING)
                recep.setStatus(Statuses.COMPLIT);
        }
    }

    @Scheduled(cron = "0 0 21 * * *")
    private void emailSenderRecep()
    {
        LocalDate dateTomorrow = LocalDate.now().plusDays(1);
        Collection<ReceptionTimeE> receptionTimeES = receptionTimeRepoE.findAllByUsrReceptionTime_Date(dateTomorrow.toString());
        for ( ReceptionTimeE recep: receptionTimeES
        ) {
            if(recep.getClientEmail() != null)
            {
                String message = String.format(
                        "Добрый вечер, %s! \n" +
                                "Напоминаем вам про запись. Вы записаны в %s к мастеру %s на услугу %s \n" +
                                "Желаем Вам хорошего дня! \n" +
                                "С уважением, Easy barber",
                        recep.getClientName(),
                        recep.getUsrReceptionTime().getTime(),
                        userRepo.findById(recep.getUsrReceptionTime().getMasterId()).get().getUsername(),
                        baseServicesRepo.findById(recep.getServiceId()).get().getName()
                );
                mailService.send(recep.getClientEmail(), "New entry", message);
            }
        }
    }

}
