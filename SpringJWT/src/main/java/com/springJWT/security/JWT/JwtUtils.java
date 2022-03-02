package com.springJWT.security.JWT;

import com.springJWT.service.KisiServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils { //internette Jwtutil hazir kodlari var kullanabilirsn


    @Value("${springJWT.app.jwtExpirationMs}") //saniyeyi nerden alcagini gosteriyoruz(application propertiesden al deidk)
    private int jwtExpirationMs;

    @Value("${springJWT.app.jwtSecret}")
    private String jwtSecret; //gizli sifre yine application propertiesden cekiyoruz

    public String JwtOlustur(Authentication authentication){ //autehntication icinde username and password vs var
       //authentication nesnesinin icindeki bilgileri cekiyor be KisiService Iml taysinda bir nesneye atiyor
        KisiServiceImpl kisiBilgiler = (KisiServiceImpl) authentication.getPrincipal();

        return Jwts.builder().  //JWT olusturmak icin
                setSubject(kisiBilgiler.getUsername()).
                setIssuedAt(new Date()).
                setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).
                signWith(SignatureAlgorithm.HS512, jwtSecret).
                compact();
    }

    public String usernameAl(String token) { //JWT nin icindeki user name ve password u almak isteidginde
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean JwtTokenGecerle(String authToken) { //JWT olusturulup token gonderildekten sonra client yeni bir istek gonderdiginde
        //bizim o tokenin gecerli olup olmadgini anlamamiz lazim o zmn bu method kullaniliyor
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            System.out.println("JWT Hatasi:" + e.getMessage());
        }
        return false;
    }



}
