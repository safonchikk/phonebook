package ua.nikitasafonov.phonebook.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public final class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts;

    public AppUser(
            Integer id,
            String username,
            String password
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public AppUser(
            String username,
            String password
    ) {
        this.username = username;
        this.password = password;
    }

    public AppUser() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
