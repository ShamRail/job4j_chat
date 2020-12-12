package ru.job4j.chat.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@RunWith(SpringRunner.class)
public class MessageRepositoryTest {

    @Autowired
    private PersonRepository personDB;

    @Autowired
    private RoomRepository roomDB;

    @Autowired
    private MessageRepository messageDB;

    @Test
    public void whenCreate() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message = new Message("msg", author, room);
        messageDB.save(message);
        Assert.assertEquals(message, messageDB.findById(message.getId()).orElse(new Message()));
    }

    @Test
    public void whenUpdate() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message = new Message("msg", author, room);
        messageDB.save(message);
        Message updated = new Message("msg2", author, room);
        updated.setId(message.getId());
        messageDB.save(updated);
        Assert.assertEquals(
                updated,
                messageDB.findById(message.getId()).orElse(new Message())
        );
    }

    @Test
    public void whenDelete() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message = new Message("msg", author, room);
        messageDB.save(message);
        messageDB.deleteById(message.getId());
        Assert.assertEquals(
                Optional.empty(),
                messageDB.findById(message.getId())
        );
    }

    @Test
    public void whenFindAll() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message1 = new Message("msg1", author, room);
        Message message2 = new Message("msg2", author, room);
        Message message3 = new Message("msg3", author, room);
        List<Message> messages = List.of(message1, message2, message3);
        messageDB.saveAll(messages);
        Assert.assertEquals(
                messages,
                messageDB.findAll()
        );
    }

    @Test
    public void whenFindByAuthor() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message1 = new Message("msg1", author, room);
        Message message2 = new Message("msg2", author, room);
        Message message3 = new Message("msg3", author, room);
        List<Message> messages = List.of(message1, message2, message3);
        messageDB.saveAll(messages);
        Assert.assertEquals(
                messages,
                messageDB.findByAuthor(author)
        );
    }

    @Test
    public void wheDeleteByAuthor() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message1 = new Message("msg1", author, room);
        Message message2 = new Message("msg2", author, room);
        Message message3 = new Message("msg3", author, room);
        List<Message> messages = List.of(message1, message2, message3);
        messageDB.saveAll(messages);
        Assert.assertEquals(3, messageDB.deleteAllByAuthor(author));
        Assert.assertEquals(List.of(), messageDB.findByAuthor(author));
    }

    @Test
    public void wheDeleteByRoom() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message1 = new Message("msg1", author, room);
        Message message2 = new Message("msg2", author, room);
        Message message3 = new Message("msg3", author, room);
        List<Message> messages = List.of(message1, message2, message3);
        messageDB.saveAll(messages);
        Assert.assertEquals(3, messageDB.deleteByRoom(room));
        Assert.assertEquals(List.of(), messageDB.findByRoom(room));
    }

    @Test
    public void whenFindByTimePeriod() {
        Person author = personDB.save(personDB.save(new Person(1)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message1 = new Message("msg1", LocalDateTime.now().minusDays(5), author, room);
        Message message2 = new Message("msg1", LocalDateTime.now().minusHours(1), author, room);
        Message message3 = new Message("msg1", LocalDateTime.now().plusDays(1), author, room);
        Message message4 = new Message("msg1", LocalDateTime.now().plusDays(5), author, room);
        messageDB.saveAll(List.of(message1, message2, message3, message4));
        Assert.assertEquals(
                List.of(message2, message3),
                messageDB.findByCreateDateBetween(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1)
                )
        );
    }

    @Test
    public void whenFindByTimePeriodAndAuthor() {
        Person author1 = personDB.save(personDB.save(new Person(1)));
        Person author2 = personDB.save(personDB.save(new Person(2)));
        Room room = roomDB.save(roomDB.save(new Room(1)));
        Message message1 = new Message("msg1", LocalDateTime.now().minusDays(3), author1, room);
        Message message2 = new Message("msg1", LocalDateTime.now().minusDays(2), author1, room);
        Message message3 = new Message("msg1", LocalDateTime.now().minusDays(1), author1, room);
        Message message4 = new Message("msg1", LocalDateTime.now().plusDays(1), author2, room);
        Message message5 = new Message("msg1", LocalDateTime.now().plusDays(2), author2, room);
        messageDB.saveAll(List.of(message1, message2, message3, message4, message5));
        Assert.assertEquals(
                List.of(message3),
                messageDB.findByAuthorAndRoomAndCreateDateBetween(
                        author1, room,
                        LocalDateTime.now().minusDays(1).minusHours(1),
                        LocalDateTime.now().plusDays(1)
                )
        );
    }

}