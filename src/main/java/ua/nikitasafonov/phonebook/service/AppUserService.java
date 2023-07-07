package ua.nikitasafonov.phonebook.service;

import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.model.AppUser;
import ua.nikitasafonov.phonebook.repository.AppUserRepository;

import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository repository;

    public AppUserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public Optional<AppUser> userByName(String name) {
        return repository.findByUsername(name);
    }
}
