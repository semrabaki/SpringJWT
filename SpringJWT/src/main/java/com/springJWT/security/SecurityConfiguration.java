package com.springJWT.security;

import com.springJWT.security.JWT.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //method seviyede izin veriirsek preauthorize gibi
public class SecurityConfiguration extends WebSecurityConfigurerAdapter { //Hazir methodlari kulllanabilmek icin
   // extend etmemis laizm


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//passwprdun tipini belirliyoruz.
    }  //obje yonetimi acisindan @bean kullanmaliyiz

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthFilter jwtTokenFilter(){
        return new JwtAuthFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.   //iu ve backend arasinda carpraz iletisim icin  gereklidir
              cors().//cors-cross origin resource sharing-iki uc arasinda veri transferinde capraz olarak adreslerin ayarlanmasini sagliyor
              and().
              csrf().disable(). //veritabanina yazabilmek icin disable ettik
              sessionManagement().
              sessionCreationPolicy(SessionCreationPolicy.STATELESS). //Oturum demek session-stateles bir session kullandigimizi soyluyoruz JWT oturumunun durumsz oldugunu belirtiyrouz
              and().
              authorizeRequests().
              antMatchers("/api/test/**").permitAll().  //sifresiz alanlara izin verdik
              antMatchers("/api/auth/**").permitAll().
              anyRequest().authenticated().and().httpBasic();

      http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }


}
