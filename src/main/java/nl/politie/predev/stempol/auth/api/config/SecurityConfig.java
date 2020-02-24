package nl.politie.predev.stempol.auth.api.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import nl.politie.predev.stempol.auth.api.security.JwtAuthenticationEntryPoint;
import nl.politie.predev.stempol.auth.api.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	  @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http
	        		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        		.and()
	        		.csrf()
	        			.disable()
	        		.cors()
	        			.disable()
	        		.exceptionHandling()
	        			.authenticationEntryPoint(unauthorizedHandler)
	        		.and()
	        		.addFilter(new JwtTokenProvider(authenticationManager()))
	                .authorizeRequests()
	                    .antMatchers("/api/auth/**").permitAll()
	                    .anyRequest().authenticated();
	    }

	    @Override
	    public void configure(AuthenticationManagerBuilder auth) throws Exception {
	    	//Good to use BcryptEncoder for spring 5.0
	        auth
	                .ldapAuthentication()
	           
	                    .userDnPatterns("uid={0},ou=Peons")
	                    //.groupSearchBase("ou=groups")
	                .contextSource(contextSource())
	              
	                .passwordCompare()
	                    //.passwordEncoder(new BCryptPasswordEncoder())
	                    .passwordAttribute("userPassword");
	       
	    }
	    
	    

  @Autowired
  private JwtAuthenticationEntryPoint unauthorizedHandler;

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
  }

  @Bean
  public DefaultSpringSecurityContextSource contextSource() {
      return  new DefaultSpringSecurityContextSource(
              Collections.singletonList("ldap://52.157.73.165:389"), "dc=example,dc=org");
  }
  
}
