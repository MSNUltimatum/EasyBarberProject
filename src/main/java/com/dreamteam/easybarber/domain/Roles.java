package com.dreamteam.easybarber.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    USER,
    MASTER,
    ADMINISTRATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
