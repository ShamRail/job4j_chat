package ru.job4j.chat.service.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.chat.dao.RoomRepository;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.person.PersonService;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomDB;

    @Autowired
    private PersonService personService;

    @Autowired
    private MessageService messageService;

    @Override
    public Room saveOrUpdate(Room entity) {
        Room result = roomDB.saveAndFlush(entity);
        joinRoom(entity, entity.getCreator());
        return result;
    }

    @Override
    @Transactional
    public void delete(Room entity) {
        messageService.deleteAllByRoom(entity);
        roomDB.removeRelationByRoom(entity);
        roomDB.delete(entity);
    }

    @Override
    public List<Room> findAll() {
        return roomDB.findAll();
    }

    @Override
    public Optional<Room> findById(int id) {
        return roomDB.findById(id);
    }

    @Override
    public boolean leaveRoom(Room room, Person person) {
        return roomDB.removeRelation(room, person) != 0;
    }

    @Override
    public boolean joinRoom(Room room, Person person) {
        return roomDB.addRelation(person, room) != 0;
    }

    @Override
    public List<Person> loadParticipants(Room room) {
        return personService.findByRoomsContains(room);
    }

    @Override
    public List<Room> loadUserCreatedRooms(Person person) {
        return roomDB.findByCreator(person);
    }

    @Override
    public List<Room> loadUserParticipatedRooms(Person person) {
        return roomDB.findByParticipantsContains(person);
    }
}
