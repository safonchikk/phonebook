package ua.nikitasafonov.phonebook.service;

import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.repository.AppUserRepository;

@Service
public class AppUserService {
    private final AppUserRepository repository;

    public AppUserService(AppUserRepository repository) {
        this.repository = repository;
    }


    public boolean createUser(AppUser user){
        if (!repository.existsByUsername(user.getUsername()) && validate(user)) {
            repository.save(user);
            return true;
        }
        return false;
    }

    private boolean validate(AppUser user){
        return true; //TODO
    }

    public boolean login(AppUser user) {
        return repository.existsByUsernameAndHashedPassword(user.getUsername(), user.getHashedPassword());
    }

    public AppUser userByName(String name) {
        return repository.findByUsername(name);
    }
}
