package com.example.study.domain.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.study.domain.user.dto.common.UserInfo;
import com.example.study.domain.user.dto.request.CreateUserRequestDto;
import com.example.study.domain.user.dto.request.ModifyUserRequestDto;
import com.example.study.domain.user.dto.response.GetAllUserResponseDto;
import com.example.study.domain.user.dto.response.GetUserResponseDto;
import com.example.study.domain.user.dto.response.ModifyUserResponseDto;
import com.example.study.domain.user.exception.UserDomainErrorCode;
import com.example.study.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetAllUserResponseDto getAllUser() {
        final List<User> allUser = this.userRepository.findAll();
        
        final List<UserInfo> userInfos = allUser
            .stream()
            .map(UserInfo::from)
            .toList();

        return new GetAllUserResponseDto(userInfos);
    }

    @Transactional(readOnly = true)
    public GetUserResponseDto getUserById(long id) {
        final User user = this.userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(UserDomainErrorCode.NOT_FOUND_USER));

        final UserInfo userInfo = UserInfo.from(user);

        return new GetUserResponseDto(userInfo);
    }

    @Transactional
    public long createUser(CreateUserRequestDto createUserRequestDto) {
        final String newUserName = createUserRequestDto.name();
        final User newUser = User.builder()
            .name(newUserName)
            .build();
        
        this.userRepository.save(newUser);

        return newUser.getId();
    }

    @Transactional
    public ModifyUserResponseDto modifyUserById(long id, ModifyUserRequestDto modifyUserRequestDto) {
        final User targetUser = this.userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(UserDomainErrorCode.NOT_FOUND_USER));
        
        final String changedName = modifyUserRequestDto.changedName();
        if (changedName != null) {
            targetUser.updateName(changedName);
        }
        
        this.userRepository.save(targetUser);

        return new ModifyUserResponseDto(UserInfo.from(targetUser));
    }

    @Transactional
    public void deleteUserbyId(long id) {
        this.userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(UserDomainErrorCode.NOT_FOUND_USER));
        
        this.userRepository.deleteById(id);

        return;
    }
}
