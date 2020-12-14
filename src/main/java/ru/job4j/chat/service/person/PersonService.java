package ru.job4j.chat.service.person;

import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.CRUD;

import java.util.List;

public interface PersonService extends CRUD<Person> {
    List<Person> findByRoomsContains(Room room);
    Person findByName(String name);
}
