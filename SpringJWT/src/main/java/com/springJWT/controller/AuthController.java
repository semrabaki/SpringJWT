package com.springJWT.controller;

import com.springJWT.model.ERoller;
import com.springJWT.model.Kisi;
import com.springJWT.model.KisiRole;
import com.springJWT.repository.KisiRepository;
import com.springJWT.repository.RoleRepository;
import com.springJWT.reqres.LoginRequest;
import com.springJWT.reqres.LoginResponse;
import com.springJWT.reqres.MesajResponse;
import com.springJWT.reqres.RegisterRequest;
import com.springJWT.service.KisiServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Authenticator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    KisiRepository kisiRepository;
    //ResponseEntity- http paketinin icindkei tum elemenlari iceriyor header params,etc etc

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager; //Kimlik denetimi islemleri icin eklemeyiz bunu ve ayarlarinida yapmaliyiz


    @PostMapping("/login")
    public ResponseEntity<?> girisYap(@RequestBody LoginRequest loginRequest) {

        //Kimlik denetiminin yapılmasi
        Authentication authentication = authenticationManager.
                authenticate( new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        //Kimlik denetimi yapılan kisinin bilgilerinin Service katmanından alinmasi
        KisiServiceImp loginKisi = (KisiServiceImp) authentication.getPrincipal();

        // login olan kisinin Rollerinin elde edilmesi
        List<String> roller = loginKisi.getAuthorities().stream().
                map(item -> item.getAuthority()).
                collect(Collectors.toList());

        return ResponseEntity.ok( new LoginResponse(
                loginKisi.getId(),
                loginKisi.getUsername(),
                loginKisi.getEmail(),
                roller
        ));
    }
    @PostMapping("/register")
    public ResponseEntity<?> kayitOl(@RequestBody RegisterRequest registerRequest){  //? donus veri tipi herhangi bisey olabilir

        //Kayit olan kullanicinin username ini kontrol et daha onceden kullanilmis ise hata dondur
        if(kisiRepository.existsByUsername(registerRequest.getUsername())){

            return ResponseEntity.
                    badRequest().
                    body(new MesajResponse("Hata: username kullaniliyor"));

        }

        //Kayit olan kullanicinin emailini ini kontrol et daha onceden kullanilmis ise hata dondur
        if(kisiRepository.existsByEmail(registerRequest.getEmail())){

            return ResponseEntity.
                    badRequest().
                    body(new MesajResponse("Hata: email kullaniliyor"));

        }

        //Yeni kullaniiciyi kaydet
        Kisi kisi= new Kisi(registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail());

        Set<String>stringRoller=registerRequest.getRole();
        Set<KisiRole> roller=new HashSet<>();

        if(stringRoller==null)
        {
            KisiRole userRole=roleRepository.findByName(ERoller.ROLE_USER).
                    orElseThrow(()-> new RuntimeException(" hata: Veritabaninda Role kayitli degil"));

            roller.add(userRole);
        }else{
            stringRoller.forEach(role -> {
                switch (role) {
                    case "admin":
                        KisiRole adminRole = roleRepository.findByName(ERoller.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Hata: Role mevcut değil."));
                        roller.add(adminRole);
                        break;
                    case "mod":
                        KisiRole modRole = roleRepository.findByName(ERoller.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("EHata: Role mevcut değil."));
                        roller.add(modRole);
                        break;
                    default:
                        KisiRole userRole = roleRepository.findByName(ERoller.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Hata: Role mevcut değil."));
                        roller.add(userRole);
                }
            });

            kisi.setRoller(roller);
            //Veritabanına yeni kaydı ekle.
            kisiRepository.save(kisi);

        }
        return ResponseEntity.ok(new MesajResponse("Kullanıcı başarıyla kaydedildi."));
    }

}
