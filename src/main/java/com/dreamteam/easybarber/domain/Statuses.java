package com.dreamteam.easybarber.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Statuses implements GrantedAuthority {
    WAITING,
    COMPLIT,
    DELETED_MASTER,
    DELETED_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
