package com.dreamteam.easybarber.repos;

import com.dreamteam.easybarber.domain.ReceptionTimeE;
import com.dreamteam.easybarber.domain.Statuses;
import com.dreamteam.easybarber.domain.UsrReceptionTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


public interface ReceptionTimeRepoE extends JpaRepository<ReceptionTimeE, Long> {
    ReceptionTimeE findByUsrReceptionTime(UsrReceptionTime usrReceptionTime);
    ReceptionTimeE findByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(Long usrReceptionTime_masterId, String usrReceptionTime_date, String usrReceptionTime_time);
    Collection<ReceptionTimeE> findAllByUsrReceptionTime_MasterIdAndStatus(Long usrReceptionTime_masterId, Statuses status);
    Collection<ReceptionTimeE> findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_Date(Long usrReceptionTime_masterId, String date);
    void deleteByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndUsrReceptionTime_Time(Long usrReceptionTime_masterId, String usrReceptionTime_date, String usrReceptionTime_time);
    Collection<ReceptionTimeE> findAllByUsrReceptionTime_MasterIdAndUsrReceptionTime_DateAndStatus(Long usrReceptionTime_masterId, String usrReceptionTime_date, Statuses status);
    Collection<ReceptionTimeE> findAllByClientName(String clientName);
    Collection<ReceptionTimeE> findAllByUsrReceptionTime_Date(String usrReceptionTime_date);
    Collection<ReceptionTimeE> findAllByClientPhonenumberAndStatus(String clientPhonenumber, Statuses status);

}
