package com.example.study.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.study.domain.user.dto.request.CreateUserRequestDto;
import com.example.study.domain.user.dto.request.ModifyUserRequestDto;
import com.example.study.domain.user.dto.response.GetAllUserResponseDto;
import com.example.study.domain.user.dto.response.GetUserResponseDto;
import com.example.study.domain.user.dto.response.ModifyUserResponseDto;
import com.example.study.domain.user.exception.UserDomainErrorCode;
import com.example.study.global.exception.BusinessException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @GetMapping("/api/users")
    public ResponseEntity<GetAllUserResponseDto> getAllUser() {
        final GetAllUserResponseDto result = this.userService.getAllUser();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<GetUserResponseDto> getUserById(@PathVariable long id) {
        final GetUserResponseDto result = this.userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/api/users")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        this.userService.createUser(createUserRequestDto);
        
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
    
    @PatchMapping("/api/users/{id}")
    public ResponseEntity<ModifyUserResponseDto> modifyUserById(
        @PathVariable long id,
        @Valid @RequestBody ModifyUserRequestDto modifyUserRequestDto
    ) {
        if (modifyUserRequestDto.changedName() == null) {
            throw new BusinessException(UserDomainErrorCode.NOT_VALID_REQUEST_BODY);
        }

        final ModifyUserResponseDto result = this.userService.modifyUserById(id, modifyUserRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long id) {
        this.userService.deleteUserbyId(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
