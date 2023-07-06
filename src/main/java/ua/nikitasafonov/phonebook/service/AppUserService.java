package ua.nikitasafonov.phonebook.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.repository.AppUserRepository;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AppUserService {
    private final AppUserRepository repository;
    private final BCryptPasswordEncoder encoder;

    public AppUserService(AppUserRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }


    public boolean createUser(AppUser user){
        if (!repository.existsByUsername(user.getUsername()) && validate(user)) {
            repository.save(user);
            return true;
        }
        return false;
    }

    private boolean validate(AppUser user){
        String regexPattern = "\\w{1,100}";
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(user.getUsername()).matches();
    }

    public boolean login(AppUser user) {
        Optional<AppUser> userOptional = repository.findByUsername(user.getUsername());
        return userOptional.filter(appUser -> encoder.matches(user.getPassword(), appUser.getPassword())).isPresent();
    }

    public Optional<AppUser> userByName(String name) {
        return repository.findByUsername(name);
    }
}
