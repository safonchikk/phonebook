package ua.nikitasafonov.phonebook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public final class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp="^\\+?[0-9]{3,15}$")
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    public Phone(
            Integer id,
            String phoneNumber
    ) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public Phone() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Contact getContact() {
        return contact;
    }
    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
