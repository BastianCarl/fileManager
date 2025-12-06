package com.example.demo.config;

import com.example.demo.service.JWTService;
import com.example.demo.service.MyUserDetailsService;
import com.example.demo.utility.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final MyUserDetailsService myUserDetailsService;
    private static final String AUTHENTICATION = "Authentication";
    private static final String AUTHENTICATION_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHENTICATION);
        String jwtToken = null;
        String username = null;

        var interator =  new LinkedList<>().iterator();
        if (authHeader != null && authHeader.startsWith(AUTHENTICATION_PREFIX)) {
            jwtToken = JwtHelper.getJwtTokenValue(authHeader);
            username = jwtService.extractUserName(jwtToken);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}