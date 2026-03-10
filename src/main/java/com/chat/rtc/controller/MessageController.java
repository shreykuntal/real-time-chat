package com.chat.rtc.controller;

import com.chat.rtc.dto.LoginResponseDto;
import com.chat.rtc.entity.UserInfo;
import com.chat.rtc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final UserRepository userRepository;
	@PostMapping
	public ResponseEntity<LoginResponseDto> login(@RequestBody Map<String, String> cred) {
		Optional<UserInfo> user = userRepository.findById(cred.get("username"));
		LoginResponseDto loginResponseDto = new LoginResponseDto();
		if (user.isPresent()){
			if (!cred.get("password").equals(user.get().getPassWord())){
				loginResponseDto.setSuccess(false);
				loginResponseDto.setMessage("Password incorrect!");
				return ResponseEntity.ok(loginResponseDto);
			}
		}else{
			UserInfo newUser = new UserInfo();
			newUser.setUserName(cred.get("username"));
			newUser.setPassWord(cred.get("password"));
			userRepository.save(newUser);
		}
		loginResponseDto.setSuccess(true);
		loginResponseDto.setMessage("");
		return ResponseEntity.ok(loginResponseDto);
	}
}
