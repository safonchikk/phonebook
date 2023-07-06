package ua.nikitasafonov.phonebook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.nikitasafonov.phonebook.CurrentUser;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.service.AppUserService;

@RestController
@RequestMapping("")
@CrossOrigin
public class AppUserController {
    private final AppUserService service;
    private final BCryptPasswordEncoder encoder;

    public AppUserController(AppUserService service, BCryptPasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @PostMapping( "/register")
    public ResponseEntity<String> register(@RequestBody AppUser user){
        user.setPassword(encoder.encode(user.getPassword()));
        boolean success = service.createUser(user);
        if (success){
            return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username taken");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AppUser user){
        boolean success = service.login(user);
        if (success){
            CurrentUser.setCurrentUser(service.userByName(user.getUsername()).get());
            return ResponseEntity.status(HttpStatus.OK).body("Logged in successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(){
        CurrentUser.setCurrentUser(null);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
}
