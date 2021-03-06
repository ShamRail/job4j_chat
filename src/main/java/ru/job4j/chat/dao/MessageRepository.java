package ru.job4j.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByAuthor(Person person);

    List<Message> findByRoom(Room room);

    List<Message> findByCreateDateBetween(LocalDateTime begin, LocalDateTime end);

    List<Message> findByAuthorAndRoomAndCreateDateBetween(
            Person author, Room room, LocalDateTime begin, LocalDateTime end
    );

    int deleteAllByAuthor(Person author);

    int deleteByRoom(Room room);

}
