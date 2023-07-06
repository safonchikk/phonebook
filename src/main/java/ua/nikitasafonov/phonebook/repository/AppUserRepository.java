package ua.nikitasafonov.phonebook.repository;

import org.springframework.data.repository.ListCrudRepository;
import ua.nikitasafonov.phonebook.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends ListCrudRepository<AppUser, Integer> {
    boolean existsByUsername(String username);
    Optional<AppUser> findByUsername(String username);
}
