package nl.politie.predev.stempol.auth.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
	
	
    @PostMapping("/magiknietzien")
    public ResponseEntity<?> getIets(){
    	return new ResponseEntity<String>("Dit zie ik niet als ik niet ingelogd ben", HttpStatus.OK);
    }

}
