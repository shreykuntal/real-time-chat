package com.chat.rtc.controller;

import com.chat.rtc.dto.LoginResponseDto;
import com.chat.rtc.dto.RegisterResponseDto;
import com.chat.rtc.entity.UserInfo;
import com.chat.rtc.repository.UserRepository;
import com.chat.rtc.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final UserRepository userRepository;

	private final JwtService jwtService;

	private boolean authenticate(Map<String, String> cred){
		Optional<UserInfo> result = userRepository.findById(cred.get("username"));
		if (result.isPresent() && result.get().getPassword().equals(cred.get("password"))){
			return true;
		}
		return false;
	}
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody Map<String, String> cred){
		LoginResponseDto reply = new LoginResponseDto();
		if (ChatWebSocketHandler.sessions.containsKey(cred.get("username"))){
			reply.setSuccess(false);
			reply.setMessage("User already logged in on another tab or device!");
		}else if (!authenticate(cred)){
			reply.setSuccess(false);
			reply.setMessage("Username/Password is incorrect!");
		}else {
			reply.setSuccess(true);
			reply.setMessage(jwtService.generateToken(cred.get("username")));
		}
		return ResponseEntity.ok(reply);
	}
	@PostMapping("/register")
	public ResponseEntity<RegisterResponseDto> register(@RequestBody Map<String, String> cred){
		RegisterResponseDto reply = new RegisterResponseDto();
		if (userRepository.existsById(cred.get("username"))){
			reply.setSuccess(false);
			reply.setMessage("Username already exists!");
		}else{
			UserInfo newUser = new UserInfo();
			newUser.setUsername(cred.get("username"));
			newUser.setPassword(cred.get("password"));
			userRepository.save(newUser);
			reply.setSuccess(true);
			reply.setMessage(jwtService.generateToken(cred.get("username")));
		}
		return ResponseEntity.ok(reply);
	}
}
