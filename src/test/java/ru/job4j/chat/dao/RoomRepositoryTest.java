package ru.job4j.chat.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@RunWith(SpringRunner.class)
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomDB;

    @Autowired
    private PersonRepository personDB;

    @Test
    public void whenCreate() {
        Room room = new Room("room1", personDB.save(new Person(1)));
        roomDB.save(room);
        Assert.assertEquals(
                room, roomDB.findById(room.getId()).orElse(new Room())
        );
    }

    @Test
    public void whenUpdate() {
        Room room = new Room("room1", personDB.save(new Person(1)));
        roomDB.save(room);
        Room updated = new Room("room2", personDB.save(new Person(2)));
        updated.setId(room.getId());
        roomDB.save(updated);
        Assert.assertEquals(
                updated, roomDB.findById(room.getId()).orElse(new Room())
        );
    }

    @Test
    public void whenDelete() {
        Room room = new Room("room1", personDB.save(new Person(1)));
        roomDB.save(room);
        roomDB.deleteById(room.getId());
        Assert.assertEquals(
                Optional.empty(), roomDB.findById(room.getId())
        );
    }

    @Test
    public void whenFindAll() {
        Person person = personDB.save(new Person(1));
        Room room1 = new Room("room1", person);
        Room room2 = new Room("room1", person);
        Room room3 = new Room("room1", person);
        List<Room> input = List.of(room1, room2, room3);
        roomDB.saveAll(input);
        Assert.assertEquals(
                input, roomDB.findAll()
        );
    }

    @Test
    public void whenInsertIntoParticipation() {
        Person person1 = personDB.save(new Person(1));
        Person person2 = personDB.save(new Person(2));
        Room room = roomDB.save(new Room(1));
        Assert.assertTrue(roomDB.addRelation(person1, room) != 0);
        Assert.assertTrue(roomDB.addRelation(person2, room) != 0);
        Assert.assertEquals(
                List.of(person1, person2),
                personDB.findByRoomsContains(room)
        );
    }

    @Test
    public void whenDeleteFromParticipation() {
        Person person1 = personDB.save(new Person(1));
        Person person2 = personDB.save(new Person(2));
        Room room = roomDB.save(new Room(1));
        roomDB.addRelation(person1, room);
        roomDB.addRelation(person2, room);
        Assert.assertTrue(roomDB.removeRelation(room, person1) != 0);
        Assert.assertEquals(
                List.of(person2),
                personDB.findByRoomsContains(room)
        );
    }

    @Test
    public void whenLoadByCreator() {
        Person person1 = personDB.save(new Person(1));
        Person person2 = personDB.save(new Person(2));
        Room room1 = new Room(1);
        Room room2 = new Room(2);
        Room room3 = new Room(3);
        room1.setCreator(person1);
        room2.setCreator(person1);
        room3.setCreator(person2);
        roomDB.saveAll(List.of(room1, room2, room3));
        Assert.assertEquals(
                List.of(room1, room2), roomDB.findByCreator(person1)
        );
        Assert.assertEquals(
                List.of(room3), roomDB.findByCreator(person2)
        );
    }

}