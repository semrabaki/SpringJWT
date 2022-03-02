package com.springJWT.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
//kisilerin oldugu tablo
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="personel")
public class Kisi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank //user name bos birakilamaz anatasyonu spring boot validation dependency enables this, Java Validation kutuphanesinden geliyor
    @Size(min=3, max=30)//bu kutuphaneyi dependencylere ekliyoruz -Spring boot starter validation
    private String username;

    @NotBlank
    @Size(min=6,max=120) //encoded password can be very long that is why we entered 120
    private String password;

    @NotBlank
    @Email //Kilasik @ isratei olmazsa hata verior
    private String email;

//    @ManyToMany(fetch= FetchType.LAZY)
//    @JoinTable(name="kisi_roller", joinColumns=@JoinColumn(name="kisi_id",referencedColumnName ="id"),
//            inverseJoinColumns = @JoinColumn(name="role_id",referencedColumnName = "id"))
    //burda hangi colum ile birlesecegini bu sekilde de belirtebiliriz eger belirtmezsek iki tablanunda otomatik olarak id sini alip birlestirior
    //o sebeple altta yazmadik ama usttekide alttakide dogry

   //Join table ile roller ile kisileri birlestirip yeni bir tablo olusturuyrz.kisi-id ile role id  ekleniyor bu tabloya yeni tablonun adi kisi roller
    //bir user in birden fazla rolu olabiliyor yada bir rol biren fazla kisiye ait olabiliyor bu sebebpel many to many
    @ManyToMany(fetch= FetchType.LAZY)  //Joim column default olarak her tablodan id yi alir ama referencedColumnName dersin istediginin columi birlestirebilirsn
    @JoinTable(name="kisi_roller", joinColumns=@JoinColumn(name="kisi_id"), //columlari birlestir ve bu tabladoki id nin ismi kisi_is olsun
                                   inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<KisiRole> roller= new HashSet<>();
  //Roller genelde setde saklanir cunku tekrraszdir.Yukarida iki tabloyu birlestiriorzRolleri constructorda olusturmadik cunku
    //roller ayni zamanda veri tabaninda tablo olusturcak o sebeple ayri variable tanimladik



    public Kisi(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
