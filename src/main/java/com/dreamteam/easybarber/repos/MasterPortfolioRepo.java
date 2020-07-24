package com.dreamteam.easybarber.repos;

import com.dreamteam.easybarber.domain.BaseServices;
import com.dreamteam.easybarber.domain.MasterPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface MasterPortfolioRepo extends JpaRepository<MasterPortfolio, Long> {
    MasterPortfolio findByPhotoUrl(String photoUrl);
    Collection<MasterPortfolio> findAllByMasterId(Long masterId);
}
