package com.mealmate.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mealmate.security.service.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${security.jwt.secret-key}")
	private String jwtSecret;

	@Value("${jwt.token.access-lifespan}")
	private long accessTokenExpiration;

	public String generateAccessToken(UserDetailsImpl userDetails) {
		return createToken(userDetails.getEmail(), accessTokenExpiration);
	}

	private String createToken(String subject, long expiration) {
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + expiration);

		return Jwts.builder()
				.setSubject(subject)
				.setIssuedAt(currentDate)
				.setExpiration(expireDate)
				.signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	public String getUsername(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
