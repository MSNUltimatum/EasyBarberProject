package com.dreamteam.easybarber.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class BaseServices {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "services")
    Set<BarberService> ratings;

    public BaseServices() {
    }

    public BaseServices(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
