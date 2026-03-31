package com.enterprise.filter;

import com.enterprise.security.JwtUtil;
import jakarta.faces.application.ResourceHandler;
import jakarta.inject.Inject;
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

@WebFilter("/*")
public class JwtFilter implements Filter {

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();

        if (isPublicPath(path, contextPath, servletPath)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        String token = session == null ? null : (String) session.getAttribute("token");

        if (token != null && jwtUtil.validarToken(token) != null) {
            chain.doFilter(request, response);
            return;
        }

        if (session != null) {
            session.invalidate();
        }
        res.sendRedirect(contextPath + "/login.xhtml");
    }

    private boolean isPublicPath(String path, String contextPath, String servletPath) {
        String normalizedServletPath = servletPath == null ? "" : servletPath.toLowerCase();

        return path.equals(contextPath)
                || path.equals(contextPath + "/")
                || path.endsWith("/login.xhtml")
                || path.contains(ResourceHandler.RESOURCE_IDENTIFIER)
                || path.contains("/javax.faces.resource/")
                || path.contains("/resources/")
                || path.contains("/primefaces/")
                || normalizedServletPath.endsWith(".css")
                || normalizedServletPath.endsWith(".js")
                || normalizedServletPath.endsWith(".png")
                || normalizedServletPath.endsWith(".jpg")
                || normalizedServletPath.endsWith(".jpeg")
                || normalizedServletPath.endsWith(".gif")
                || normalizedServletPath.endsWith(".svg")
                || normalizedServletPath.endsWith(".ico")
                || normalizedServletPath.endsWith(".woff")
                || normalizedServletPath.endsWith(".woff2");
    }
}
