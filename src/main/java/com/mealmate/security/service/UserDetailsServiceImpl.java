package com.mealmate.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mealmate.entity.User;
import com.mealmate.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> {
					LOGGER.error("Exception occurred while logging in user: User email not found, Email: {}", email);
					return new UsernameNotFoundException("User email not found: " + email);
				});

		return UserDetailsImpl.build(user);
	}
}
