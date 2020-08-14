package com.altimetrik.coding.security.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.altimetrik.coding.config.exception.CustomAuthenticationException;
import com.altimetrik.coding.service.AltimetrikUserDetailService;

public class AltimetrikRequestFilter extends AbstractAuthenticationProcessingFilter {

	public AltimetrikRequestFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }
	
	@Autowired
	private AltimetrikUserDetailService userDetailsService;
	
	@Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
		Authentication requestAuthentication = null;
        String token= httpServletRequest.getHeader(AUTHORIZATION);
        token= StringUtils.removeStart(token, "Bearer").trim();

        boolean validToken = userDetailsService.validateToken(token);
        
        if (validToken) {
            requestAuthentication = new UsernamePasswordAuthenticationToken(token, token);
            return getAuthenticationManager().authenticate(requestAuthentication);
        } 
        else {
        	throw new CustomAuthenticationException("Token Expired");
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }
	
}
