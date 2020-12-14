package ru.job4j.chat.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findByRoomsContains(Room room);
    Person findByName(String name);

}
