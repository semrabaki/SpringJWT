package com.springJWT.repository;

import com.springJWT.model.Kisi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface KisiRepository extends JpaRepository<Kisi, Long> {

    Boolean existByUsername(String username);
    Boolean existByEmail(String email);


}
