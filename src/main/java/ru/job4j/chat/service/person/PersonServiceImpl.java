package ru.job4j.chat.service.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.chat.dao.PersonRepository;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.room.RoomService;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personDB;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RoomService roomService;

    @Override
    public Person saveOrUpdate(Person entity) {
        return personDB.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void delete(Person entity) {
        messageService.deleteAllByAuthor(entity);
        List<Room> userParticipated = roomService.loadUserParticipatedRooms(entity);
        userParticipated.forEach(room -> roomService.leaveRoom(room, entity));
        List<Room> userCreated = roomService.loadUserCreatedRooms(entity);
        userCreated.forEach(room -> roomService.delete(room));
        personDB.delete(entity);
    }

    @Override
    public List<Person> findAll() {
        return personDB.findAll();
    }

    @Override
    public Optional<Person> findById(int id) {
        return personDB.findById(id);
    }

    @Override
    public List<Person> findByRoomsContains(Room room) {
        return personDB.findByRoomsContains(room);
    }

    @Override
    public Person findByName(String name) {
        return personDB.findByName(name);
    }
}
