package com.dreamteam.easybarber.domain;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class MstrServiceEmbed implements Serializable {
    @Column(name = "user_id")
    Long masterId;

    @Column(name = "service_id")
    Long serviceId;

    public MstrServiceEmbed(){}

    public MstrServiceEmbed(Long masterId, Long serviceId)
    {
        this.masterId = masterId;
        this.serviceId = serviceId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
