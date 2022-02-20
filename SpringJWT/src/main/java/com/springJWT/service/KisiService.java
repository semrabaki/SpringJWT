package com.springJWT.service;

import com.springJWT.model.Kisi;
import com.springJWT.repository.KisiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class KisiService implements UserDetailsService {

    @Autowired
    KisiRepository kisiRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Kisi kisi=kisiRepository.findByUsername(username).
                orElseThrow(()-> new UsernameNotFoundException("Kullanici bulunamadi "+ username));

        return KisiServiceImp.kisiOlustur(kisi);
    }
}
