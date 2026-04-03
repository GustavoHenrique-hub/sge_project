package com.enterprise.filter;

import com.enterprise.controller.admin.AuthBean;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("*.xhtml")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        boolean loginPage = requestUri.endsWith("/login.xhtml");
        boolean resourceRequest = requestUri.contains("/jakarta.faces.resource/")
                || requestUri.contains("/javax.faces.resource/");

        AuthBean authBean = CDI.current().select(AuthBean.class).get();
        boolean autenticado = authBean != null && authBean.isAutenticado();

        if (resourceRequest) {
            chain.doFilter(request, response);
            return;
        }

        if (autenticado && loginPage) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/pages/alunos/pageDashboard.xhtml");
            return;
        }

        if (!loginPage && !autenticado) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}
