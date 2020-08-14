package com.altimetric.coding.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.altimetrik.coding.security.filter.AltimetrikRequestFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(
			  new AntPathRequestMatcher("/cc/**")
			);		
	
	@Autowired
    AltimetrikAuthenticationProvider  altimetrikAuthenticationProvider;

    public SecurityConfig(final AltimetrikAuthenticationProvider authenticationProvider) {
        super();
        this.altimetrikAuthenticationProvider=authenticationProvider;
    }
    
	@Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(altimetrikAuthenticationProvider);
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling().and()
				.authenticationProvider(altimetrikAuthenticationProvider)
				.addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter.class).authorizeRequests()
				.requestMatchers(PROTECTED_URLS).authenticated().and().csrf().disable().formLogin().loginPage("/index").and()
				.httpBasic().disable().logout().disable();
	}
	
	 @Bean
	  AltimetrikRequestFilter authenticationFilter() throws Exception {
		  AltimetrikRequestFilter filter = new AltimetrikRequestFilter(PROTECTED_URLS);
		  filter.setAuthenticationManager(authenticationManager());
	  return filter;
	 }
}
