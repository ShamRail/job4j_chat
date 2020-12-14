package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.controller.exception.ResourceNotFoundException;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.service.person.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public Person savePerson(@RequestBody Person person) {
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        return personService.saveOrUpdate(person);
    }

    @PostMapping("/login")
    public Person loginPerson(@RequestBody Person person) {
        return personService.findByName(person.getName());
    }

    @PutMapping
    public void updatePerson(@RequestParam int id, @RequestBody Person updated) {
        Person person = personService.findById(id).orElseThrow(ResourceNotFoundException::new);
        person.setPassword(updated.getPassword());
        person.setName(updated.getName());
        person.setEmail(updated.getEmail());
        personService.saveOrUpdate(person);
    }

    @DeleteMapping
    public void deletePerson(@RequestParam int id) {
        if (personService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException();
        }
        personService.delete(new Person(id));
    }

    @GetMapping
    public Person getById(@RequestParam int id) {
        return personService.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public List<Person> getAll() {
        return personService.findAll();
    }

}
