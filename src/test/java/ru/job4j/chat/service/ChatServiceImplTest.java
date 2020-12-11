package ru.job4j.chat.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ChatServiceImplTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ChatService chatService() {
            return new ChatServiceImpl();
        }
    }

    @Autowired
    private ChatService chatService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private PersonService personService;

    @MockBean
    private RoomService roomService;

    @Test
    public void whenNoPersonWhenSendMessage() {
        Person stubPerson = new Person(1);
        Room stubRoom = new Room(1);
        Mockito.when(personService.findById(stubPerson.getId())).thenReturn(Optional.empty());
        Mockito.when(roomService.findById(stubRoom.getId())).thenReturn(Optional.of(stubRoom));
        assertFalse(chatService.sendMessage(stubPerson, stubRoom, new Message()));
    }

    @Test
    public void whenNoRoomWhenSendMessage() {
        Person stubPerson = new Person(1);
        Room stubRoom = new Room(1);
        Mockito.when(personService.findById(stubPerson.getId())).thenReturn(Optional.of(stubPerson));
        Mockito.when(roomService.findById(stubRoom.getId())).thenReturn(Optional.empty());
        assertFalse(chatService.sendMessage(stubPerson, stubRoom, new Message()));
    }

    @Test
    public void whenSendMessageWithPersonAndRoom() {
        Person stubPerson = new Person(1);
        Room stubRoom = new Room(1);
        Message stubMessage = new Message();
        Mockito.when(personService.findById(stubPerson.getId())).thenReturn(Optional.of(stubPerson));
        Mockito.when(roomService.findById(stubRoom.getId())).thenReturn(Optional.of(stubRoom));
        Mockito.when(messageService.saveOrUpdate(stubMessage)).thenReturn(stubMessage);
        assertTrue(chatService.sendMessage(stubPerson, stubRoom, stubMessage));
    }

    @Test
    public void whenFindByTimeTODAY() {
        Room stubRoom = new Room(1);
        Person stubPerson = new Person(1);
        Message msg1 = new Message("msg1", LocalDateTime.now().minusDays(100), stubPerson, stubRoom);
        Message msg2 = new Message("msg2", LocalDateTime.now().minusDays(15), stubPerson, stubRoom);
        Message msg3 = new Message("msg3", LocalDateTime.now().minusDays(5), stubPerson, stubRoom);
        Message msg4 = new Message("msg4", LocalDateTime.now().minusDays(1), stubPerson, stubRoom);
        Message msg5 = new Message("msg5", LocalDateTime.now(), stubPerson, stubRoom);
        List<Message> messages = List.of(msg1, msg2, msg3, msg4, msg5);
        Mockito.when(messageService.findByRoom(stubRoom)).thenReturn(messages);
        assertEquals(List.of(msg5), chatService.loadMessages(stubRoom, ChatService.TimePeriod.TODAY));
    }

    @Test
    public void whenFindByTimeYESTERDAY() {
        Room stubRoom = new Room(1);
        Person stubPerson = new Person(1);
        Message msg1 = new Message("msg1", LocalDateTime.now().minusDays(100), stubPerson, stubRoom);
        Message msg2 = new Message("msg2", LocalDateTime.now().minusDays(15), stubPerson, stubRoom);
        Message msg3 = new Message("msg3", LocalDateTime.now().minusDays(5), stubPerson, stubRoom);
        Message msg4 = new Message("msg4", LocalDateTime.now().minusDays(1), stubPerson, stubRoom);
        Message msg5 = new Message("msg5", LocalDateTime.now(), stubPerson, stubRoom);
        List<Message> messages = List.of(msg1, msg2, msg3, msg4, msg5);
        Mockito.when(messageService.findByRoom(stubRoom)).thenReturn(messages);
        assertEquals(List.of(msg4), chatService.loadMessages(stubRoom, ChatService.TimePeriod.YESTERDAY));
    }

    @Test
    public void whenFindByTimeWEEK() {
        Room stubRoom = new Room(1);
        Person stubPerson = new Person(1);
        Message msg1 = new Message("msg1", LocalDateTime.now().minusDays(100), stubPerson, stubRoom);
        Message msg2 = new Message("msg2", LocalDateTime.now().minusDays(15), stubPerson, stubRoom);
        Message msg3 = new Message("msg3", LocalDateTime.now().minusDays(5), stubPerson, stubRoom);
        Message msg4 = new Message("msg4", LocalDateTime.now().minusDays(1), stubPerson, stubRoom);
        Message msg5 = new Message("msg5", LocalDateTime.now(), stubPerson, stubRoom);
        List<Message> messages = List.of(msg1, msg2, msg3, msg4, msg5);
        Mockito.when(messageService.findByRoom(stubRoom)).thenReturn(messages);
        assertEquals(List.of(msg3, msg4, msg5), chatService.loadMessages(stubRoom, ChatService.TimePeriod.WEEK));
    }


    @Test
    public void whenFindByTimeMonth() {
        Room stubRoom = new Room(1);
        Person stubPerson = new Person(1);
        Message msg1 = new Message("msg1", LocalDateTime.now().minusDays(100), stubPerson, stubRoom);
        Message msg2 = new Message("msg2", LocalDateTime.now().minusDays(15), stubPerson, stubRoom);
        Message msg3 = new Message("msg3", LocalDateTime.now().minusDays(5), stubPerson, stubRoom);
        Message msg4 = new Message("msg4", LocalDateTime.now().minusDays(1), stubPerson, stubRoom);
        Message msg5 = new Message("msg5", LocalDateTime.now(), stubPerson, stubRoom);
        List<Message> messages = List.of(msg1, msg2, msg3, msg4, msg5);
        Mockito.when(messageService.findByRoom(stubRoom)).thenReturn(messages);
        assertEquals(List.of(msg2, msg3, msg4, msg5), chatService.loadMessages(stubRoom, ChatService.TimePeriod.MONTH));
    }

    @Test
    public void whenFindByTimeAny() {
        Room stubRoom = new Room(1);
        Person stubPerson = new Person(1);
        Message msg1 = new Message("msg1", LocalDateTime.now().minusDays(100), stubPerson, stubRoom);
        Message msg2 = new Message("msg2", LocalDateTime.now().minusDays(15), stubPerson, stubRoom);
        Message msg3 = new Message("msg3", LocalDateTime.now().minusDays(5), stubPerson, stubRoom);
        Message msg4 = new Message("msg4", LocalDateTime.now().minusDays(1), stubPerson, stubRoom);
        Message msg5 = new Message("msg5", LocalDateTime.now(), stubPerson, stubRoom);
        List<Message> messages = List.of(msg1, msg2, msg3, msg4, msg5);
        Mockito.when(messageService.findByRoom(stubRoom)).thenReturn(messages);
        assertEquals(List.of(msg1, msg2, msg3, msg4, msg5), chatService.loadMessages(stubRoom, ChatService.TimePeriod.ANY));
    }


}