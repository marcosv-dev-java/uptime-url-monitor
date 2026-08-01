package dev.marcos.uptime.monitor.config;

import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.model.UserDetailsImpl;
import dev.marcos.uptime.monitor.repository.UserDetailsServiceImpl;
import dev.marcos.uptime.monitor.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserDetailsServiceImpl repository;

    public JwtFilter(TokenService tokenService, UserDetailsServiceImpl repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        if(token != null){
            var username = this.tokenService.validateToken(token);
            UserDetailsImpl user = (UserDetailsImpl) repository.loadUserByUsername(username);
            var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request,response);
    }
    private String recoverToken(HttpServletRequest request){
        var token = request.getHeader("Authorization");
        return token == null ? null : token.replace("Bearer ", "");
    }
}
