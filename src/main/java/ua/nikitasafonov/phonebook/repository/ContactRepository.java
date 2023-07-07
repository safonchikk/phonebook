package ua.nikitasafonov.phonebook.repository;

import org.springframework.data.repository.ListCrudRepository;
import ua.nikitasafonov.phonebook.model.Contact;

import java.util.List;

public interface ContactRepository extends ListCrudRepository<Contact, Integer> {

    //boolean existsByNameAndAppUser_Id(String name, Integer userId);
    boolean existsByNameAndAppUser_Username(String name, String username);
    Contact findByNameAndAppUser_Username(String name, String username);
    //List<Contact> findAllByAppUser_Id(Integer userId);
    List<Contact> findAllByAppUser_Username(String name);
}
