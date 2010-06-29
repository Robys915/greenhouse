package com.springsource.greenhouse.signin;


import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springsource.greenhouse.utils.EmailUtils;

public class GreenhouseUserService implements UserDetailsService {

    private JdbcTemplate jdbcTemplate;
	
	@Inject
	public GreenhouseUserService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException, DataAccessException {
		GreenhouseUserDetails details = EmailUtils.isEmail(username) ? findByEmail(username) : findByUsername(username);
		if (details == null) {
			throw new UsernameNotFoundException("No such user '" + username + "' exists");
		}
		return details;
	}
	
	// internal helpers
	
	private GreenhouseUserDetails findByEmail(String email) {
		try {
			return jdbcTemplate.queryForObject("select id, firstName, username, password from User where email = ?", new GreenhouseUserDetailsRowMapper(), email);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}		
	}
	
	private GreenhouseUserDetails findByUsername(String username) {
		try {
			return jdbcTemplate.queryForObject("select id, firstName, username, password from User where username = ?", new GreenhouseUserDetailsRowMapper(), username);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}		
	}
}
