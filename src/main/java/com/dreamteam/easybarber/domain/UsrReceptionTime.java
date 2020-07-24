package com.dreamteam.easybarber.domain;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class UsrReceptionTime implements Serializable {

    @Column(name = "user_id")
    Long masterId;

    @Column(name = "DATE")
    String date;

    @Column(name = "time")
    String time;

    public UsrReceptionTime(){}

    public UsrReceptionTime(Long masterId, String date, String time)
    {
        this.masterId = masterId;
        this.time = time;
        this.date = date;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
