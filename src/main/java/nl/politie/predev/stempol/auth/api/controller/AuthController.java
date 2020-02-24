package nl.politie.predev.stempol.auth.api.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.politie.predev.stempol.auth.api.model.ApiResponse;
import nl.politie.predev.stempol.auth.api.model.JwtAuthenticationResponse;
import nl.politie.predev.stempol.auth.api.model.LoginRequest;
import nl.politie.predev.stempol.auth.api.model.ValidateTokenRequest;
import nl.politie.predev.stempol.auth.api.security.JwtTokenProvider;
import nl.politie.predev.stempol.auth.api.util.AppConstants;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/generatetoken")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    	
    	if(loginRequest.getUsername().isEmpty() || loginRequest.getPassword().isEmpty()) {
    		 return new ResponseEntity(new ApiResponse(false, AppConstants.USERNAME_OR_PASSWORD_INVALID),
                     HttpStatus.BAD_REQUEST);
    	}
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/validatetoken")
    public ResponseEntity<?> getTokenByCredentials(@Valid @RequestBody ValidateTokenRequest validateToken) {
    	
    	 String username = null;
    	 String jwt =validateToken.getToken();
 
         if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                username = tokenProvider.getUsernameFromJWT(jwt);
                
	          //If required we can have one more check here to load the user from LDAP server
             return ResponseEntity.ok(new ApiResponse(true, AppConstants.VALID_TOKEN + username));
           }else {
        	   return new ResponseEntity(new ApiResponse(false, AppConstants.INVALID_TOKEN),
                       HttpStatus.BAD_REQUEST);
           }
         
    }
    

}
