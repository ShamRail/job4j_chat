package ru.job4j.chat.service.message;

import org.springframework.stereotype.Service;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.CRUD;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService extends CRUD<Message> {

    List<Message> findByAuthor(Person person);

    List<Message> findByRoom(Room room);

    List<Message> findByCreateDateBetween(LocalDateTime begin, LocalDateTime end);

    List<Message> findByAuthorAndRoomAndCreateDateBetween(
            Person author, Room room, LocalDateTime begin, LocalDateTime end
    );

    int deleteAllByAuthor(Person author);

    int deleteAllByRoom(Room room);

}
