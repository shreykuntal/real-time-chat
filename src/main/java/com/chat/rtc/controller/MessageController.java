package com.chat.rtc.controller;

import com.chat.rtc.dto.LoginResponseDto;
import com.chat.rtc.dto.MessageDto;
import com.chat.rtc.dto.RegisterResponseDto;
import com.chat.rtc.entity.UserInfo;
import com.chat.rtc.repository.UserRepository;
import com.chat.rtc.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final UserRepository userRepository;

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody Map<String, String> cred){
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(cred.get("username"),
						cred.get("password"))
		);
		LoginResponseDto reply = new LoginResponseDto();
		reply.setSuccess(true);
		reply.setMessage(jwtService.generateToken(cred.get("username")));
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
			newUser.setPassword("{noop}"+cred.get("password"));
			userRepository.save(newUser);
			reply.setSuccess(true);
			reply.setMessage(jwtService.generateToken(cred.get("username")));
		}
		return ResponseEntity.ok(reply);
	}
}
