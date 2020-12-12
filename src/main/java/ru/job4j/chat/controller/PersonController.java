package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public Person savePerson(@RequestBody Person person) {
        return personService.saveOrUpdate(person);
    }

    @PutMapping
    public void updatePerson(@RequestParam int id) {
        if (personService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException();
        }
        personService.saveOrUpdate(new Person(id));
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
