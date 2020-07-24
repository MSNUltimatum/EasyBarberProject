package com.dreamteam.easybarber.config;

import com.dreamteam.easybarber.domain.User;
import com.dreamteam.easybarber.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class Securityhandler implements AuthenticationSuccessHandler {

    @Autowired
    UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        User user = (User)authentication.getPrincipal();
        if(user.getActivationCode() == null)
        {
            httpServletResponse.sendRedirect("/");
            return;
        }
        HttpSession session = httpServletRequest.getSession(false);
        session.setAttribute("message", "Activate your account");
        httpServletResponse.sendRedirect("/login");
        return;
    }
}
