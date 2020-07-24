package com.dreamteam.easybarber.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class ReceptionTimeE {

    @EmbeddedId
    UsrReceptionTime usrReceptionTime;

    private String clientName; //имя клиента
    private String clientPhonenumber; // id клиента
    private String clientEmail; // id клиента
    private Long serviceId; // id услуги
    private Statuses status;

    public UsrReceptionTime getUsrReceptionTime() {
        return usrReceptionTime;
    }

    public void setUsrReceptionTime(UsrReceptionTime usrReceptionTime) {
        this.usrReceptionTime = usrReceptionTime;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhonenumber() {
        return clientPhonenumber;
    }

    public void setClientPhonenumber(String clientPhonenumber) {
        this.clientPhonenumber = clientPhonenumber;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }
}
