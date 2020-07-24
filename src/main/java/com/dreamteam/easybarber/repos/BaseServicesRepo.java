package com.dreamteam.easybarber.repos;

import com.dreamteam.easybarber.domain.BaseServices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseServicesRepo extends JpaRepository<BaseServices, Long> {
    BaseServices findByName(String name);
}
