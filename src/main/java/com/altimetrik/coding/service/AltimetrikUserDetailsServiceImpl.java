package com.altimetrik.coding.service;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.altimetrik.coding.model.AltimetrikUser;
import com.altimetrik.coding.model.Request;
import com.altimetrik.coding.security.repository.AltimetrikUserRepository;

@Service
public class AltimetrikUserDetailsServiceImpl implements AltimetrikUserDetailService{

	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.token.validity}")
	private String tokenValidity;

	@Autowired
    AltimetrikUserRepository altimetrikUserRepository;
	
	@Override
	public AltimetrikUser generateToken(Request userInput) throws UsernameNotFoundException {
			AltimetrikUser user= new AltimetrikUser();	
		
		    AltimetrikUser altimetrikUser = altimetrikUserRepository.findByUserName(userInput.getUserName());
			
		    if(StringUtils.isEmpty(altimetrikUser.getUserName())){
			    
		    	StringBuilder signatureBuilder = new StringBuilder();
				signatureBuilder.append(tokenValidity).append(":");
				signatureBuilder.append(userInput.getUserName());
				
				String token = encrypt(userInput.getUserName(), userInput.getPassword());
				signatureBuilder.append(token);
				
				user.setUserName(userInput.getUserName());
				//user.setPassword(userInput.getPassword());
				user.setToken(signatureBuilder.toString());
	            altimetrikUserRepository.save(user);            
		    } else {
		    	if(!validateToken(altimetrikUser.getToken())) {
		    		saveRefreshToken(altimetrikUser.getToken(), userInput);
		    	}
		    }
            return user;
	}

	@Override
	public Optional<User> getToken(String token) {
		Optional<AltimetrikUser> customer= altimetrikUserRepository.findByToken(token);
        if(customer.isPresent()){
        	AltimetrikUser altimetrikUser = customer.get();
            User user= new User(altimetrikUser.getUserName(), altimetrikUser.getPassword(), true, true, true, true,
                    AuthorityUtils.createAuthorityList("USER"));
            return Optional.of(user);
        }
        return  Optional.empty();		

	}
	
	@Override
	public boolean validateToken(String token) {
		String[] parts = token.split(":");
		long expires = Long.parseLong(parts[0]);
		String tokenString = parts[2];
		String userName = decrypt(tokenString, parts[1], secret);
		Optional<User> user = getToken(token);
		
		return expires >= System.currentTimeMillis() && userName.equals(user.get().getUsername());
	}
	
	
	 public String encrypt(String userName, String password) {
		 
		try {
			IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
			SecretKeySpec secretkeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretkeySpec, iv);
			
			byte[] encrypted = cipher.doFinal(userName.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "Exception in Encryption";
	}

	
	 String decrypt(String encrypted, String username, String secret) {
			try {
				IvParameterSpec iv = new IvParameterSpec(username.getBytes("UTF-8"));
				SecretKeySpec secretkeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");

				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.DECRYPT_MODE, secretkeySpec, iv);
				byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

				return new String(original);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return new String("Decryption Failed");
		}

	@Override
	public UserDetails loadUserByUsername(String username) {
		altimetrikUserRepository.findByUserName(username);
		return null;
	}

	@Override
	public String readRefreshToken(String tokenValue) {
        Optional<AltimetrikUser> refreshToken = altimetrikUserRepository.findByToken(tokenValue);
        return refreshToken.isPresent() ? refreshToken.get().getToken() : null;
    }

	@Override
	public void saveRefreshToken(String tokenString, Request userInput) {

		if (readRefreshToken(tokenString) != null) {
            this.removeToken(tokenString);
        }
 
		StringBuilder signatureBuilder = new StringBuilder();
		signatureBuilder.append(tokenValidity).append(":");
		signatureBuilder.append(userInput.getUserName());
		
		String token = encrypt(userInput.getUserName(), userInput.getPassword());
		signatureBuilder.append(token);

		AltimetrikUser user= new AltimetrikUser();
		user.setUserName(userInput.getUserName());
		//user.setPassword(userInput.getPassword());
		user.setToken(signatureBuilder.toString());
        altimetrikUserRepository.save(user);            
		
	}

	private void removeToken(String token) {
		Optional<AltimetrikUser> existingToken = altimetrikUserRepository.findByToken(token);
        if (existingToken.isPresent()) {
            altimetrikUserRepository.delete(existingToken.get());
        }
	}		
}
