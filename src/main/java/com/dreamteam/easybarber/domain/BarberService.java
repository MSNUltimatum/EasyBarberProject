package com.dreamteam.easybarber.domain;

import lombok.Data;

import javax.persistence.*;

// класс услуги
@Entity
public class BarberService
{
    @EmbeddedId
    MstrServiceEmbed mstrServiceEmbed;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    User master;

    @ManyToOne
    @MapsId("service_id")
    @JoinColumn(name = "service_id")
    BaseServices services;

    private String description;
    private String imageFileName;
    private Long cost;
    private Long count;

    public MstrServiceEmbed getMstrServiceEmbed() {
        return mstrServiceEmbed;
    }

    public void setMstrServiceEmbed(MstrServiceEmbed mstrServiceEmbed) {
        this.mstrServiceEmbed = mstrServiceEmbed;
    }

    public User getMaster() {
        return master;
    }

    public void setMaster(User master) {
        this.master = master;
    }

    public BaseServices getServices() {
        return services;
    }

    public void setServices(BaseServices services) {
        this.services = services;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
