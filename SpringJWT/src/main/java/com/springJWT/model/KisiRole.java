package com.springJWT.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
//Rollerin oldugu tablo
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="roller") //databasede roller diye gozukcek
public class KisiRole {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY )
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ERoller name; //ENum turunden oldugu iicn bunu veri tabbaina nsl kaydedicegini bilmior bu sebeple @enumerated annotation kullaniyoruz ve belirtiyorz hangi tur olcagini

    public KisiRole(ERoller name){
        this.name=name;
    } //parametreli constructor (is otomortik olusturuldugu icin parametre olarak vermedik)


}
