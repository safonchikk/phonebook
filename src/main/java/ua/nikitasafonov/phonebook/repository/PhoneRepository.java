package ua.nikitasafonov.phonebook.repository;

import org.springframework.data.repository.ListCrudRepository;
import ua.nikitasafonov.phonebook.model.Phone;

import java.util.List;

public interface PhoneRepository extends ListCrudRepository<Phone, Integer> {

    boolean existsByPhoneNumberAndContact_Id(String phoneNumber, Integer contactId);
    List<Phone> findAllByContact_Id(Integer contactId);
}
