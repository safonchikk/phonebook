package ua.nikitasafonov.phonebook.repository;

import org.springframework.data.repository.ListCrudRepository;
import ua.nikitasafonov.phonebook.model.Email;

import java.util.List;

public interface EmailRepository extends ListCrudRepository<Email, Integer> {

    boolean existsByAddressAndContact_Id(String address, Integer contactId);
    List<Email> findAllByContact_Id(Integer contactId);
}
