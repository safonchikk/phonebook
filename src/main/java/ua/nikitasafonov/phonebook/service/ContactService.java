package ua.nikitasafonov.phonebook.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.nikitasafonov.phonebook.Status;
import ua.nikitasafonov.phonebook.model.*;
import ua.nikitasafonov.phonebook.repository.AppUserRepository;
import ua.nikitasafonov.phonebook.repository.ContactRepository;
import ua.nikitasafonov.phonebook.repository.EmailRepository;
import ua.nikitasafonov.phonebook.repository.PhoneRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ContactService {
    private final ContactRepository repository;
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final AppUserRepository userRepository;


    public ContactService(ContactRepository repository, PhoneRepository phoneRepository, EmailRepository emailRepository, AppUserRepository userRepository) {
        this.repository = repository;
        this.phoneRepository = phoneRepository;
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    public static String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<ContactDTO> myContacts(){

        List<ContactDTO> res = new ArrayList<>();
        for (Contact contact: repository.findAllByAppUser_Username(getCurrentUser())){
            res.add(contactToDTO(contact));
        }
        return res;
    }

    public List<SmallContactDTO> myContactsClean(){
        List<SmallContactDTO> res = new ArrayList<>();
        for (Contact contact: repository.findAllByAppUser_Username(getCurrentUser())){
            res.add(new SmallContactDTO(contact.getId(), contact.getName()));
        }
        return res;
    }

    private boolean validate(Contact contact){
        String regexPattern = ".{1,100}";
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(contact.getName()).matches();
    }
    private boolean validate(Phone phone){
        String regexPattern = "\\+?[0-9]{1,15}";
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(phone.getPhoneNumber()).matches();
    }
    private boolean validate(Email email){
        String regexPattern = "[\\w\\-.]+@[a-zA-Z\\-]+\\.\\w{2,}";
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(email.getAddress()).matches();
    }

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

    private boolean checkContactToUser(Contact contact){
        return contact.getAppUser().getUsername().equals(getCurrentUser());
    }

    private boolean checkContactToUser(Integer contactId){
        Optional<Contact> contact = repository.findById(contactId);
        return contact.map(value -> value.getAppUser().getUsername().equals(getCurrentUser())).orElse(false);
    }

    public Optional<ContactDTO> contactById(Integer id) {
        Optional<Contact> contact = repository.findById(id);
        if (contact.isEmpty()){
            return Optional.empty();
        }
        if (!checkContactToUser(contact.get())){
            return Optional.empty();
        }
        return contact.map(this::contactToDTO);
    }

    public List<Phone> phonesOfContact(Integer contactId){
        if (!checkContactToUser(contactId)){
            return null;
        }
        return phoneRepository.findAllByContact_Id(contactId);
    }

    public List<Email> emailsOfContact(Integer contactId){
        if (!checkContactToUser(contactId)){
            return null;
        }
        return emailRepository.findAllByContact_Id(contactId);
    }

    public Status deleteContactById(Integer id) {
        Optional<Contact> optionalContact = repository.findById(id);
        if (optionalContact.isEmpty()){
            return Status.NOT_FOUND;
        }
        if (!checkContactToUser(optionalContact.get())){
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
        if (!checkContactToUser(optionalPhone.get().getContact())){
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
        if (!checkContactToUser(optionalEmail.get().getContact())){
            return Status.AUTH_ERROR;
        }
        emailRepository.deleteById(id);
        return Status.OK;
    }

    public Status addContact(Contact contact) {
        Contact savedContact = new Contact();
        savedContact.setName(contact.getName());
        savedContact.setAppUser(userRepository.findByUsername(getCurrentUser()).orElseThrow());
        if (repository.existsByNameAndAppUser_Username(savedContact.getName(), getCurrentUser())) {
            return Status.NOT_UNIQUE;
        }
        if (validate(savedContact)) {
            repository.save(savedContact);
        }
        else {
            return Status.VALIDATION_ERROR;
        }

        savedContact = repository.findByNameAndAppUser_Username(contact.getName(), getCurrentUser());
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
        if (!checkContactToUser(optionalContact.get())){
            return Status.AUTH_ERROR;
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
        if (!checkContactToUser(optionalContact.get())){
            return Status.AUTH_ERROR;
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
        if (!checkContactToUser(existingContact)){
            return Status.AUTH_ERROR;
        }
        existingContact.setName(contact.getName());
        existingContact.setAppUser(userRepository.findByUsername(getCurrentUser()).orElseThrow());

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
