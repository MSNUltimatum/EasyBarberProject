package com.dreamteam.easybarber.repos;

import com.dreamteam.easybarber.domain.BarberService;
import com.dreamteam.easybarber.domain.Roles;
import com.dreamteam.easybarber.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByPhonenumber(String phonenumber);
    User findByEmail(String email);
    User findByActivationCode(String activationCode);
    Collection<User> findAllByUsername(String name);
    Collection<User> findAllByUsernameAndRolesContaining(String username, Roles roles);
    Collection<User> findAllByCityAndRolesContaining(String city, Roles role);
    User findByRatingsContaining(BarberService baseService);
    Collection<User> findAllByUsernameAndCityAndRolesContaining(String username, String city, Roles role);
    Collection<User> findAllByRolesContaining(Roles roles);
}
