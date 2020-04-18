package com.devfactory.processautomation.qa.rwa.sfapi.infrastructure;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String AD_DOMAIN = "devfactory.local";
    private static final String AD_URL = "ldap://dc-saas.devfactory.com:389/";
    private static final String ACTUATOR_HEALTH_PATH = "/actuator/info";
    private static final String HOME = "/index.html";
    private static final String ROOT = "/";
    private static final String DASHBOARD = "/dashboard";
    private static final String LOGIN = "/login";
    private static final String GROUP_FILTER =
            "(&(sAMAccountName={1})(memberOf=cn=SaaSOps-Rundeck-Project-XORWA-Ops,cn=Users,DC=devfactory,DC=local))";

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(ACTUATOR_HEALTH_PATH, HOME, ROOT, DASHBOARD, LOGIN)
                .permitAll().anyRequest().authenticated().and()
                .httpBasic()
                .and().cors().and()
                .csrf().disable();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider())
                .userDetailsService(userDetailsService());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(activeDirectoryLdapAuthenticationProvider()));
    }

    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        final ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
                AD_DOMAIN,
                AD_URL);
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        provider.setSearchFilter(GROUP_FILTER);
        return provider;
    }
}
