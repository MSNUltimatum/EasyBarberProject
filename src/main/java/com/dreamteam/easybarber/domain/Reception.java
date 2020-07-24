package com.dreamteam.easybarber.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

// класс собственно записи
// и он сводит фактически все наши сущности воедино
// клиента client : User
// к мастеру master : User
// на оказание услуги service : BarberService
// по типу времени записи time : ReceptionTime
// [здесь вместо них проставлю id]
// на дату (мы же решили, что нужно предусмотреть записи и на месяц (или 4 недели) вперёд)
// date : java.sql.Date
@Entity
public class Reception
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String clientName; //имя клиента
    private String clientPhonenumber; // id клиента
    private Long masterId; // id мастера
    private Long serviceId; // id услуги
    private Long receptionTimeId; // id времени записи
    private Date date; // дата, на которую мы записываем
    // её совпадение по дню недели будем проверять в контроллере

    public Reception() {}
    public Reception ( String clientName
                     , String clientPhonenumber
                     , Long masterId
                     , Long serviceId
                     , Long receptionTimeId
                     , Date date
                     )
    {
        this.clientName = clientName;
        this.clientPhonenumber = clientPhonenumber;
        this.masterId = masterId;
        this.serviceId = serviceId;
        this.receptionTimeId = receptionTimeId;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientPhonenumber() {
        return clientPhonenumber;
    }

    public void setClientPhonenumber(String clientId) {
        this.clientPhonenumber = clientId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public Long getReceptionTimeId() {
        return receptionTimeId;
    }

    public void setReceptionTimeId(Long receptionTimeId) {
        this.receptionTimeId = receptionTimeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getClientName() { return clientName; }

    public void setClientName(String clientName) { this.clientName = clientName; }

}
