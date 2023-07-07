package ua.nikitasafonov.phonebook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AppUserController {
    private final AuthService service;

    public AppUserController(AuthService service) {
        this.service = service;
    }

    @PostMapping( "/register")
    public ResponseEntity<String> register(@RequestBody AppUser user){
        String token = service.createUser(user);
        if (token != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username taken or invalid");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AppUser user){
        String token = service.login(user);
        if (token != null){
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
        }
    }
}
