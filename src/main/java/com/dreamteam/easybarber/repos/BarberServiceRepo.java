package com.dreamteam.easybarber.repos;

import com.dreamteam.easybarber.domain.BarberService;
import com.dreamteam.easybarber.domain.MstrServiceEmbed;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface BarberServiceRepo extends CrudRepository<BarberService, Long>
{
    Collection<BarberService> findAllByMstrServiceEmbed_MasterId(Long mstrServiceEmbed_masterId);
    BarberService findByMstrServiceEmbed(MstrServiceEmbed mstrServiceEmbed);
    Collection<BarberService> findAllByMstrServiceEmbed_ServiceId(Long mstrServiceEmbed_serviceId);
}
