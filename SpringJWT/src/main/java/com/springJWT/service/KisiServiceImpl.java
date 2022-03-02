package com.springJWT.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springJWT.model.Kisi;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
//Service implemettaion anotasyonu koymuyoruz cunku bu service laywrin alt dali
public class KisiServiceImpl implements UserDetails {

    private static final long serialVersionUID=1L; //java classlarini baska formata cevirmeye serilization deniyor. Bu islemi
    //yapabilmek icin JBM bir seri numrasi verior. Verirsen daha hizli calisiyr uygulama
    private Long id;
    private String username;
    private String email;
    @JsonIgnore  // Password bilgisini json dosyasına saklamamasi için koyulan anotasyon
    private String password;

    private Collection<? extends GrantedAuthority> otoriteler; //roller bu isimle saklaniyor ve grant otority olarak saklaniyor

    // Constructor
    public KisiServiceImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> otoriteler) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.otoriteler = otoriteler;
    }

   //burda veri tabanindaki user bilgilerinin kopyasini olusturuyorz ve service katmaninda isliyoruz kullaniyoz ve gerekirse
    //veri tabanina ulasip kaydediyoruz
    public static KisiServiceImpl kisiOlustur(Kisi kisi) {
        List<GrantedAuthority> otoriteler = kisi.getRoller().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        //Constructorla yeni bir kisi olusturup onu donduruor
        return new KisiServiceImpl(
                kisi.getId(),
                kisi.getUsername(),
                kisi.getEmail(),
                kisi.getPassword(),
                otoriteler);
    }

    public Long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return otoriteler;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
