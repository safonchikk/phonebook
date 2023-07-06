package ua.nikitasafonov.phonebook.model;

import jakarta.persistence.*;

@Entity
public final class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @jakarta.validation.constraints.Email
    private String address;
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    public Email(
            Integer id,
            String address
    ) {
        this.id = id;
        this.address = address;
    }

    public Email() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }
    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
