package ua.nikitasafonov.phonebook.model;

import java.util.List;

public record ContactDTO(
        Integer id,
        String name,
        List<PhoneDTO> phones,
        List<EmailDTO> emails
) {
}
