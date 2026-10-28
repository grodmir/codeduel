package com.codeduel.codeduelapp.auth.infrastructure.persistence.repository;

import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.ProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, Integer> {

}
