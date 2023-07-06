package ua.nikitasafonov.phonebook.repository;

import org.springframework.data.repository.ListCrudRepository;
import ua.nikitasafonov.phonebook.model.AppUser;

public interface AppUserRepository extends ListCrudRepository<AppUser, Integer> {
    boolean existsByUsername(String username);
    boolean existsByUsernameAndHashedPassword(String username, String hashedPassword);

    AppUser findByUsername(String username);
}
