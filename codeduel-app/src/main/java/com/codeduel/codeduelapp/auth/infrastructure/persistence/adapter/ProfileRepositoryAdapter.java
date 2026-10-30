package com.codeduel.codeduelapp.auth.infrastructure.persistence.adapter;

import com.codeduel.codeduelapp.auth.domain.exception.ProfileNotFoundException;
import com.codeduel.codeduelapp.auth.domain.model.Profile;
import com.codeduel.codeduelapp.auth.domain.repository.ProfileRepository;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.ProfileJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper.ProfileMapper;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.repository.ProfileJpaRepository;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA-реализация {@link ProfileRepository}
 * */
@Component
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {

    private final ProfileJpaRepository profileRepository;
    private final UserJpaRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public Profile create(Profile profile) {
        UserJpaEntity userRef = userRepository.getReferenceById(profile.getUserId());
        ProfileJpaEntity profileEntity = profileMapper.toNewEntity(profile, userRef);
        return profileMapper.toModel(profileRepository.save(profileEntity));
    }

    @Override
    @Transactional
    public Profile update(Profile profile) {
        ProfileJpaEntity profileEntity = profileRepository.findById(profile.getUserId()).orElseThrow(
                () -> new ProfileNotFoundException(profile.getUserId())
        );
        profileMapper.updateEntity(profileEntity, profile);
        return profileMapper.toModel(profileEntity);
    }

    @Override
    public Optional<Profile> findByUserId(Integer userId) {
        return profileRepository.findById(userId).map(profileMapper::toModel);
    }
}
