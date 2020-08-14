package com.altimetrik.coding.security.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.altimetrik.coding.model.AltimetrikUser;

@Repository
public interface AltimetrikUserRepository extends CrudRepository<AltimetrikUser, Long>{

    Optional<AltimetrikUser> findByToken(String token);

    AltimetrikUser findByUserName(String username);
}
