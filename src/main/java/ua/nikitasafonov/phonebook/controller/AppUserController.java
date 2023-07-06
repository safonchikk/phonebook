package ua.nikitasafonov.phonebook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nikitasafonov.phonebook.CurrentUser;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.service.AppUserService;

@RestController
@RequestMapping("")
@CrossOrigin
public class AppUserController {
    private final AppUserService service;

    public AppUserController(AppUserService service) {
        this.service = service;
    }

    @PostMapping( "/register")
    public ResponseEntity<String> register(@RequestBody AppUser user){
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
            //TODO gen token
            CurrentUser.setCurrentUser(service.userByName(user.getUsername()));
            return ResponseEntity.status(HttpStatus.OK).body("Logged in successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(){
        //TODO logout for actual security
        CurrentUser.setCurrentUser(null);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
}
