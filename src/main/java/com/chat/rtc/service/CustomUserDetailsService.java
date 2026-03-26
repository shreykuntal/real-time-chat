package com.chat.rtc.service;

import com.chat.rtc.entity.UserInfo;
import com.chat.rtc.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserInfo> user = userRepository.findById(username);
		return new org.springframework.security.core.userdetails.User(
				user.get().getUsername(),
				user.get().getPassword(),
				new ArrayList<>()
		);
	}
}
