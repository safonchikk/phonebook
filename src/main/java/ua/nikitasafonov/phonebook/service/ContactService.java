package ua.nikitasafonov.phonebook.service;

import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.CurrentUser;
import ua.nikitasafonov.phonebook.Status;
import ua.nikitasafonov.phonebook.model.*;
import ua.nikitasafonov.phonebook.repository.ContactRepository;
import ua.nikitasafonov.phonebook.repository.EmailRepository;
import ua.nikitasafonov.phonebook.repository.PhoneRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {
    private final ContactRepository repository;
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;


    public ContactService(ContactRepository repository, PhoneRepository phoneRepository, EmailRepository emailRepository) {
        this.repository = repository;
        this.phoneRepository = phoneRepository;
        this.emailRepository = emailRepository;
    }

    public List<ContactDTO> myContacts(){
        List<ContactDTO> res = new ArrayList<>();
        for (Contact contact: repository.findAllByAppUser_Id(CurrentUser.getCurrentUser().getId())){
            res.add(contactToDTO(contact));
        }
        return res;
    }

    public List<SmallContactDTO> myContactsClean(){
        List<SmallContactDTO> res = new ArrayList<>();
        for (Contact contact: repository.findAllByAppUser_Id(CurrentUser.getCurrentUser().getId())){
            res.add(new SmallContactDTO(contact.getId(), contact.getName()));
        }
        return res;
    }

    private boolean validate(Contact contact){
        return true;
    } //TODO
    private boolean validate(Phone phone){
        return true;
    } //TODO
    private boolean validate(Email email){
        return true;
    } //TODO

    private ContactDTO contactToDTO(Contact contact){

        List<Phone> phones = phonesOfContact(contact.getId());
        List<PhoneDTO> phoneDTOs = new ArrayList<>();
        for (Phone phone : phones) {
            phoneDTOs.add(new PhoneDTO(phone.getId(), phone.getPhoneNumber()));
        }

        List<Email> emails = emailsOfContact(contact.getId());
        List<EmailDTO> emailDTOs = new ArrayList<>();
        for (Email email : emails) {
            emailDTOs.add(new EmailDTO(email.getId(), email.getAddress()));
        }

        return new ContactDTO(contact.getId(), contact.getName(), phoneDTOs, emailDTOs);
    }

    public Optional<ContactDTO> contactById(Integer id) {
        Optional<Contact> contact = repository.findById(id);
        return contact.map(this::contactToDTO);
    }

    public List<Phone> phonesOfContact(Integer contactId){
        return phoneRepository.findAllByContact_Id(contactId);
    }

    public List<Email> emailsOfContact(Integer contactId){
        return emailRepository.findAllByContact_Id(contactId);
    }

    public Status deleteContactById(Integer id) {
        Optional<Contact> optionalContact = repository.findById(id);
        if (optionalContact.isEmpty()){
            return Status.NOT_FOUND;
        }
        Contact contact = optionalContact.get();
        if (contact.getAppUser().getId() != CurrentUser.getCurrentUser().getId()){
            return Status.AUTH_ERROR;
        }
        repository.deleteById(id);
        return Status.OK;
    }
    public Status deletePhoneById(Integer id) {
        Optional<Phone> optionalPhone = phoneRepository.findById(id);
        if (optionalPhone.isEmpty()){
            return Status.NOT_FOUND;
        }
        Phone phone = optionalPhone.get();
        if (phone.getContact().getAppUser().getId() != CurrentUser.getCurrentUser().getId()){
            return Status.AUTH_ERROR;
        }
        phoneRepository.deleteById(id);
        return Status.OK;
    }
    public Status deleteEmailById(Integer id) {
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (optionalEmail.isEmpty()){
            return Status.NOT_FOUND;
        }
        Email email = optionalEmail.get();
        if (email.getContact().getAppUser().getId() != CurrentUser.getCurrentUser().getId()){
            return Status.AUTH_ERROR;
        }
        emailRepository.deleteById(id);
        return Status.OK;
    }

    public Status addContact(Contact contact) {
        Contact savedContact = new Contact();
        savedContact.setName(contact.getName());
        savedContact.setAppUser(CurrentUser.getCurrentUser());
        if (repository.existsByNameAndAppUser_Id(savedContact.getName(), CurrentUser.getCurrentUser().getId())) {
            return Status.NOT_UNIQUE;
        }
        if (validate(savedContact)) {
            repository.save(savedContact);
        }
        else {
            return Status.VALIDATION_ERROR;
        }

        savedContact = repository.findByName(contact.getName());
        for (Phone phone: contact.getPhones()){
            _addPhone(phone, savedContact);
        }
        for (Email email: contact.getEmails()){
            _addEmail(email, savedContact);
        }
        return Status.OK;
    }

    public Status addPhone(Phone phone, Integer contactId){
        Optional<Contact> optionalContact = repository.findById(contactId);
        if (optionalContact.isEmpty()){
            return Status.NOT_FOUND;
        }
        return _addPhone(phone, optionalContact.get());
    }

    private Status _addPhone(Phone phone, Contact contact) {
        if (repository.findById(contact.getId()).isEmpty()){
            return Status.NOT_FOUND;
        }
        phone.setContact(contact);
        if (phoneRepository.existsByPhoneNumberAndContact_Id(phone.getPhoneNumber(), contact.getId())){
            return Status.NOT_UNIQUE;
        }
        if (validate(phone)) {
            phoneRepository.save(phone);
            return Status.OK;
        }
        else {
            return Status.VALIDATION_ERROR;
        }
    }

    public Status addEmail(Email email, Integer contactId){
        Optional<Contact> optionalContact = repository.findById(contactId);
        if (optionalContact.isEmpty()){
            return Status.NOT_FOUND;
        }
        return _addEmail(email, optionalContact.get());
    }

    private Status _addEmail(Email email, Contact contact) {
        if (repository.findById(contact.getId()).isEmpty()){
            return Status.NOT_FOUND;
        }
        email.setContact(contact);
        if (emailRepository.existsByAddressAndContact_Id(email.getAddress(), contact.getId())){
            return Status.NOT_UNIQUE;
        }
        if (validate(email)) {
            emailRepository.save(email);
            return Status.OK;
        }
        else {
            return Status.VALIDATION_ERROR;
        }
    }

    public Status editContact(Integer id, Contact contact) {
        Contact existingContact = repository.findById(id).orElse(null);
        if (existingContact == null) {
            return Status.NOT_FOUND;
        }

        existingContact.setName(contact.getName());
        existingContact.setAppUser(CurrentUser.getCurrentUser());

        existingContact.getPhones().clear();
        existingContact.getEmails().clear();

        repository.save(existingContact);

        for (Phone phone: contact.getPhones()){
            _addPhone(phone, existingContact);
        }
        for (Email email: contact.getEmails()){
            _addEmail(email, existingContact);
        }

        return Status.OK;
    }
}
