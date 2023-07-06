package ua.nikitasafonov.phonebook.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.model.SecurityUser;
import ua.nikitasafonov.phonebook.repository.AppUserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final AppUserRepository repository;

    public JpaUserDetailsService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
                .findByUsername(username)
                .map(SecurityUser::new).
                orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }
}
