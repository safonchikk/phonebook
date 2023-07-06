package ua.nikitasafonov.phonebook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nikitasafonov.phonebook.Status;
import ua.nikitasafonov.phonebook.model.*;
import ua.nikitasafonov.phonebook.service.ContactService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contacts")
@CrossOrigin
public class ContactController{

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<ContactDTO> myContacts(){
        return service.myContacts();
    }

    @GetMapping("/clean")
    public List<SmallContactDTO> myContactsClean(){
        return service.myContactsClean();
    }

    @GetMapping("/{id}")
    public Optional<ContactDTO> contactById(@PathVariable Integer id){
        return service.contactById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addContact(@RequestBody Contact contact){
        Status e = service.addContact(contact);
        return getResponseEntity(e);
    }

    @PostMapping("/add/{id}/phone")
    public ResponseEntity<Void> addPhone(@PathVariable Integer id, @RequestBody Phone phone){
        Status e = service.addPhone(phone, id);
        return getResponseEntity(e);
    }

    @PostMapping("/add/{id}/email")
    public ResponseEntity<Void> addEmail(@PathVariable Integer id, @RequestBody Email email){
        Status e = service.addEmail(email, id);
        return getResponseEntity(e);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Void> editContact(@PathVariable Integer id, @RequestBody Contact contact){
        Status e = service.editContact(id, contact);
        return getResponseEntity(e);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Integer id){
        Status e = service.deleteContactById(id);
        return getResponseEntity(e);
    }

    @DeleteMapping("/delete/phone/{id}")
    public ResponseEntity<Void> deletePhone(@PathVariable Integer id){
        Status e = service.deletePhoneById(id);
        return getResponseEntity(e);
    }

    @DeleteMapping("/delete/email/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Integer id){
        Status e = service.deleteEmailById(id);
        return getResponseEntity(e);
    }

    private ResponseEntity<Void> getResponseEntity(Status e) {
        if (e == Status.OK){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        if (e == Status.AUTH_ERROR){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (e == Status.NOT_FOUND){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return null;
    }
}
