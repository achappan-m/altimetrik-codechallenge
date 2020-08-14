package com.altimetric.coding.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.altimetrik.coding.service.AltimetrikUserDetailService;

@Component
public class AltimetrikAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	AltimetrikUserDetailService altimetrikUserDetailService;
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		Object token= authentication.getCredentials();
        return Optional
                .ofNullable(token)
                .map(String::valueOf)
                .flatMap(altimetrikUserDetailService::getToken)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with token=" + token));
    	}
}


