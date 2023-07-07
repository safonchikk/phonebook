package ua.nikitasafonov.phonebook.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.repository.AppUserRepository;
import ua.nikitasafonov.phonebook.security.JwtService;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private final AppUserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(AppUserRepository repository, BCryptPasswordEncoder encoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public String createUser(AppUser user){
        if (!repository.existsByUsername(user.getUsername()) && validate(user)) {
            user.setPassword(encoder.encode(user.getPassword()));
            repository.save(user);
            return jwtService.generateToken(user);
        }
        return null;
    }

    private boolean validate(AppUser user){
        String regexPattern = "\\w{1,100}";
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(user.getUsername()).matches();
    }

    public String login(AppUser user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
        );
        AppUser foundUser = repository.findByUsername(user.getUsername()).orElseThrow();
        return jwtService.generateToken(foundUser);
    }

    public Optional<AppUser> userByName(String name) {
        return repository.findByUsername(name);
    }
}
