package com.rahul.auth.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class RouteLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RouteLoggingFilter.class);

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, 
                                  jakarta.servlet.http.HttpServletResponse response, 
                                  jakarta.servlet.FilterChain filterChain) 
            throws jakarta.servlet.ServletException, IOException {
        
        log.info("Incoming request: {} {}", 
            request.getMethod(), 
            request.getRequestURI());
        
        filterChain.doFilter(request, response);
    }
}