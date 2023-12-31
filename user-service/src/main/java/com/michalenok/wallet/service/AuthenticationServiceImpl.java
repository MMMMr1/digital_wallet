package com.michalenok.wallet.service;

import com.michalenok.wallet.feign.AccountServiceFeignClient;
import com.michalenok.wallet.keycloak.KeycloakService;
import com.michalenok.wallet.mapper.UserMapper;
import com.michalenok.wallet.model.constant.UserStatus;
import com.michalenok.wallet.model.dto.request.UserRegistrationDto;
import com.michalenok.wallet.model.entity.UserEntity;
import com.michalenok.wallet.model.entity.VerificationEntity;
import com.michalenok.wallet.model.exception.VerificationUserException;
import com.michalenok.wallet.repository.api.VerificationRepository;
import com.michalenok.wallet.service.api.AuthenticationService;
import com.michalenok.wallet.service.api.UserService;
import com.michalenok.wallet.service.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final VerificationRepository verificationRepository;
    private final UserService userService;
    private final AccountServiceFeignClient accountServiceFeignClient;
    private final UserMapper userMapper;
    private final UuidUtil uuidUtil;

    @Override
    @Transactional
    public void register(UserRegistrationDto user) {
        userService.create(userMapper.userRegistrationDtoToUserCreateDto(user));
        verificationRepository.save(generateVerificationEntity(user.mail()));
    }

    @Override
    @Transactional
    public void verifyUser(String code, String mail) {
        isCodeValid(code, mail);
        UUID userUuid = getUser(mail).getUuid();
        userService.changeStatus(userUuid, UserStatus.ACTIVATED);
        verificationRepository.deleteByMail(mail);
        createDefaultAccount(userUuid);
        log.info("Successful verification user [{}, {}]", userUuid, mail);
    }

    private UserEntity getUser(String mail) {
        log.info("get user with mail {}", mail);
        return userMapper.toUserEntity(userService.findByMail(mail));
    }

    private void isCodeValid(String code, String mail) {
        VerificationEntity verificationCode = verificationRepository.findById(mail)
                .orElseThrow(() ->
                        new VerificationUserException(String.format("There is no code for mail %s", mail)));
        if (!code.equals(verificationCode.getCode())) {
            log.error("Unsuccessful verification: {} , {}", mail, code);
            throw new VerificationUserException("Incorrect mail and code");
        }
    }

    private VerificationEntity generateVerificationEntity(String mail) {
        VerificationEntity verificationEntity = new VerificationEntity();
        verificationEntity.setMail(mail);
        verificationEntity.setCode(uuidUtil.generateUuidCode());
        log.info("Create verification code {}  to user: {}", verificationEntity.getCode(), verificationEntity.getMail());
        return verificationEntity;
    }

    private void createDefaultAccount(UUID userUuid) {
        log.info("Create default account for user: {}", userUuid);
        accountServiceFeignClient.createAccount(userUuid);
    }
}
