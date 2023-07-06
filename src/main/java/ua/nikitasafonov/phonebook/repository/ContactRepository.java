package ua.nikitasafonov.phonebook.repository;

import org.springframework.data.repository.ListCrudRepository;
import ua.nikitasafonov.phonebook.model.Contact;

import java.util.List;

public interface ContactRepository extends ListCrudRepository<Contact, Integer> {

    boolean existsByNameAndAppUser_Id(String name, Integer userId);
    List<Contact> findAllByAppUser_Id(Integer userId);

    Contact findByName(String name);
}
