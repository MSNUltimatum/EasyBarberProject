package com.dreamteam.easybarber.repos;

import com.dreamteam.easybarber.domain.Reception;
import org.springframework.data.repository.CrudRepository;

public interface ReceptionRepo extends CrudRepository<Reception, Long>
{
    Reception findByReceptionTimeId(Long receptionTimeId);
}
