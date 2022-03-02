package com.springJWT.controller;

import com.springJWT.model.ERoller;
import com.springJWT.model.Kisi;
import com.springJWT.model.KisiRole;
import com.springJWT.repository.KisiRepository;
import com.springJWT.repository.RoleRepository;
import com.springJWT.reqres.LoginRequest;
import com.springJWT.reqres.JwtResponse;
import com.springJWT.reqres.MesajResponse;
import com.springJWT.reqres.RegisterRequest;
import com.springJWT.security.JWT.JwtUtils;
import com.springJWT.service.KisiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController { //Kimlik denetimi yapan kontroller

    @Autowired
    KisiRepository kisiRepository;
    //ResponseEntity- http paketinin icindkei tum elemenlari iceriyor header params,etc etc

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager; //Kimlik denetimi islemleri icin eklemeliyiz bunu ve ayarlarinida yapmaliyiz

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> girisYap(@RequestBody LoginRequest loginRequest) {

        //Kimlik denetiminin yapılmasi-
        Authentication authentication = authenticationManager.
                authenticate( new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));  //username and passswordu al authentication yap

        //Kisiye gore JWT olusturulmasi ve Security context in guncellenmesi
        SecurityContextHolder.getContext().setAuthentication(authentication); //log in kisisiinin bilgileri ile secirty contect holderi guncelledik
        String jwt = jwtUtils.JwtOlustur(authentication);

        //Kimlik denetimi yapılan kisinin bilgilerinin Service katmanından alinmasi (service katmani veri tabanindan aliyor)
        KisiServiceImpl loginKisi = (KisiServiceImpl) authentication.getPrincipal();

        // login olan kisinin Rollerinin elde edilmesi
        List<String> roller = loginKisi.getAuthorities().stream().
                map(item -> item.getAuthority()).
                collect(Collectors.toList());

        //Ekrana log in olan kisinin ekrana bilgilerinin yadilirmasi
        return ResponseEntity.ok( new JwtResponse(
                jwt, //tokeni zaten burda jwt ye atdik
                loginKisi.getId(),
                loginKisi.getUsername(),
                loginKisi.getEmail(),
                roller
        ));
    }

    //ResponseEntity -butun http request verisini getirir ? isareti icindeki veri tipi farkli farkli olabilir
    @PostMapping("/register")
    public ResponseEntity<?> kayitOl(@RequestBody RegisterRequest registerRequest){  //? donus veri tipi herhangi bisey olabilir
                                                 //burda class verdik cunku tum verileri bir seferede almak istedik
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

        //Yeni kullaniiciyi kaydet constructor la olusturdk
        Kisi kisi= new Kisi(registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail());

        Set<String>stringRoller=registerRequest.getRole(); //kullnicyi olusturduktan sonra role de girmemiz lazim
        Set<KisiRole> roller=new HashSet<>();// burda ekrandan gelen rol string cinsinde ama bnm rollerim Kisirole cinsinde
      // sete ceviirp roller in icine ekliyourz o sebeple cevrmem lazim.

        if(stringRoller==null)
        {
            KisiRole userRole=roleRepository.findByName(ERoller.ROLE_USER).//user eger rol girmezse otomati olarak user rolu atanizak
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

            kisi.setRoller(roller);//cevirdigimiz rolleri gonderiyoruz
            //Veritabanına yeni kaydı ekle.
            kisiRepository.save(kisi);

        }
        return ResponseEntity.ok(new MesajResponse("Kullanıcı başarıyla kaydedildi."));
    }

}
