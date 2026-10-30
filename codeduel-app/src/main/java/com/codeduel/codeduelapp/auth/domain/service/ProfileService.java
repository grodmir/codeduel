package com.codeduel.codeduelapp.auth.domain.service;

import com.codeduel.codeduelapp.auth.domain.dto.ProfileUpdate;
import com.codeduel.codeduelapp.auth.domain.exception.InvalidProfileDataException;
import com.codeduel.codeduelapp.auth.domain.exception.ProfileNotFoundException;
import com.codeduel.codeduelapp.auth.domain.model.Profile;
import com.codeduel.codeduelapp.auth.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Управление данными в профиле пользователя.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    @Value("${elo.initial}")
    private int initialRating;

    protected void createFor(Integer userId) {
        profileRepository.create(
                Profile.builder()
                        .userId(userId)
                        .wins(0)
                        .losses(0)
                        .rating(initialRating)
                        .build()
        );
        log.info("Profile created: userId={}", userId);
    }

    @Transactional(readOnly = true)
    public Profile getByUserId(Integer userId) {
        return profileRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileNotFoundException(userId)
        );
    }

    /**
     * Записывает значения, рассчитанные внешним модулем. Сам ничего не вычисляет.
     *
     * @throws InvalidProfileDataException если rating, wins или losses отрицательные
     * @throws ProfileNotFoundException если профиля нет
     */
    @Transactional
    public Profile update(ProfileUpdate update) {
        if (update.rating() < 0 || update.wins() < 0 || update.losses() < 0) {
            throw new InvalidProfileDataException(update.userId());
        }
        return profileRepository.update(Profile.builder()
                .userId(update.userId())
                .rating(update.rating())
                .wins(update.wins())
                .losses(update.losses())
                .build());
    }
}
