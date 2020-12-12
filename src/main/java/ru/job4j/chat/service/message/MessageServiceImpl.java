package ru.job4j.chat.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.job4j.chat.dao.MessageRepository;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageDB;

    @Override
    public Message saveOrUpdate(Message entity) {
        return messageDB.saveAndFlush(entity);
    }

    @Override
    public void delete(Message entity) {
        messageDB.delete(entity);
    }

    @Override
    public List<Message> findAll() {
        return messageDB.findAll();
    }

    @Override
    public Optional<Message> findById(int id) {
        return messageDB.findById(id);
    }

    @Override
    public List<Message> findByAuthor(Person person) {
        return messageDB.findByAuthor(person);
    }

    @Override
    public List<Message> findByRoom(Room room) {
        return messageDB.findByRoom(room);
    }

    @Override
    public List<Message> findByCreateDateBetween(LocalDateTime begin, LocalDateTime end) {
        return messageDB.findByCreateDateBetween(begin, end);
    }

    @Override
    public List<Message> findByAuthorAndRoomAndCreateDateBetween(Person author, Room room, LocalDateTime begin, LocalDateTime end) {
        return messageDB.findByAuthorAndRoomAndCreateDateBetween(author, room, begin, end);
    }

    @Override
    public int deleteAllByAuthor(Person author) {
        return messageDB.deleteAllByAuthor(author);
    }

    @Override
    public int deleteAllByRoom(Room room) {
        return messageDB.deleteByRoom(room);
    }

}
