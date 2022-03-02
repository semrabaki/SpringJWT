package com.springJWT.security.JWT;

import com.springJWT.service.KisiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    KisiService kisiService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Request ilk olarak filtera gelcek artik
        try{
            //Request icerisindeki Header a git ve Autorization kismindaki tokeni ayikla
            String jwt=jwtAl(request);
            //Tokene `i gecerle
            if(jwt!=null&&jwtUtils.JwtTokenGecerle(jwt)) {
                String username = jwtUtils.usernameAl(jwt);  //burda token dogrulandi
                //Istekde bulunan kisinin bilgilerini service layerdan cek
                //Bundan sonrasi standart ama classlarina uyarliyorsn
                UserDetails kisiDetaylar = kisiService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        kisiDetaylar, null, kisiDetaylar.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Kimlik denetim bilgilerini tutan Security context in guncellenmesi
                SecurityContextHolder.getContext().setAuthentication(authentication);}
            }catch(Exception e){
                System.out.println("Kimlik denetimi gerceklestirilmedi "+e.getMessage());
            }

        //
            filterChain.doFilter(request,response);
        }

    public String jwtAl(HttpServletRequest request) {
        //tokenlar genelde header icine konuldugu icin hedaer bakiyoruz
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) &&(headerAuth.startsWith("Bearer ")))
        {
                return headerAuth.substring(7, headerAuth.length());
            }
        return null;
    }
}
