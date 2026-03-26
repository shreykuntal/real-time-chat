package com.chat.rtc.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
	private final String SECRET = "b7d0c8c6f1e4a3d9b6c2f7e8d1a4c9b3e6f2d7a8c1b4e9f3d6c7a1e2f5b8d4c6";

	private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

	public String generateToken(String username){

		return Jwts.builder()
				.subject(username)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
				.signWith(key)
				.compact();
	}
	public String extractUsername(String token){
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	public boolean validateToken(String token, String username){
		return username.equals(extractUsername(token));
	}
}
