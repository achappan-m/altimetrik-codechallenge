package com.altimetrik.coding.controller;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.altimetrik.coding.model.AltimetrikUser;
import com.altimetrik.coding.model.Request;
import com.altimetrik.coding.service.AltimetrikUserDetailService;

@CrossOrigin
@RestController
public class AltimetrikController {

	@Autowired
	private AltimetrikUserDetailService userDetailsService;
	
	@RequestMapping(value = "/cc/auth", method = RequestMethod.POST)
	public ResponseEntity<String> createToken(@RequestBody Request authenticationRequest) throws Exception {
		
		AltimetrikUser user = userDetailsService.generateToken(authenticationRequest);
		if(user.getId()!=0L) {
			return new ResponseEntity<String>(HttpStatus.CREATED);
		}
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	}

	@GetMapping(value = "/cc/getToken")
	public ResponseEntity<Optional<User>> findToken(@RequestBody Request tokenequest) throws Exception {
		
		Optional<User> user = userDetailsService.getToken(tokenequest.getToken());
		
		return new ResponseEntity<Optional<User>>(user, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping(value = "/cc/getUser")
	public ResponseEntity<String> loadUserByName(@RequestBody Request resquest) throws Exception {
		
		UserDetails user = userDetailsService.loadUserByUsername(resquest.getUserName());
		
		if(StringUtils.isNotEmpty(user.getUsername())) {
			return new ResponseEntity<String>(user.getUsername(), HttpStatus.ACCEPTED);
		}
		
		return new ResponseEntity<String>(user.getUsername(), HttpStatus.CONFLICT);
	}
}
