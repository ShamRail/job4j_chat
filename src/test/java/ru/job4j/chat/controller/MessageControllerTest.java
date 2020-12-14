package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.chat.config.Profiles;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.model.dto.MessageDTO;
import ru.job4j.chat.service.ChatService;
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MessageController.class)
@ActiveProfiles(value = {Profiles.NO_AUTH})
public class MessageControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private PersonService personService;

    @MockBean
    private RoomService roomService;

    @MockBean
    private ChatService chatService;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    @Test
    public void whenCreateAndUserNotFound() throws Exception {
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(post("/person/1/room/1/message")).andExpect(status().is4xxClientError());
    }

    @Test
    public void whenCreateAndRoomNotFound() throws Exception {
        Person person = new Person(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(post("/person/1/room/1/message")).andExpect(status().is4xxClientError());
    }

    @Test
    public void whenSuccessCreate() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message = new Message("msg");
        message.setAuthor(person);
        message.setRoom(room);
        message.setText("msg");
        Mockito.when(messageService.saveOrUpdate(message)).thenReturn(message);
        mvc.perform(
                post("/person/1/room/1/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(message))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text", is(message.getText())))
                .andExpect(jsonPath("$.author.id", is(person.getId())))
                .andExpect(jsonPath("$.room.id", is(room.getId())));
    }

    @Test
    public void whenSuccessUpdate() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message = new Message("msg");
        message.setAuthor(person);
        message.setRoom(room);
        message.setText("updated");
        Mockito.when(messageService.findById(1)).thenReturn(Optional.of(message));
        Mockito.when(messageService.saveOrUpdate(message)).thenReturn(message);
        mvc.perform(
                put("/person/1/room/1/message?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(new MessageDTO("updated")))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void whenSuccessDelete() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message = new Message("msg");
        message.setAuthor(person);
        message.setRoom(room);
        message.setText("updated");
        Mockito.when(messageService.findById(1)).thenReturn(Optional.of(message));
        mvc.perform(
                delete("/person/1/room/1/message?id=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenSuccessGetByRoom() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message1 = new Message("msg1", LocalDateTime.now(), person, room);
        Message message2 = new Message("msg2", LocalDateTime.now(), person, room);
        Message message3 = new Message("msg3", LocalDateTime.now(), person, room);
        List<Message> messages = List.of(message1, message2, message3);
        Mockito.when(messageService.findByRoom(room)).thenReturn(messages);
        mvc.perform(
                get("/person/1/room/1/message"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].text", is(message1.getText())))
                .andExpect(jsonPath("$.[0].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[0].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[1].text", is(message2.getText())))
                .andExpect(jsonPath("$.[1].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[1].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[2].text", is(message3.getText())))
                .andExpect(jsonPath("$.[2].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[2].room.id", is(room.getId())));
    }

    @Test
    public void whenSuccessGetByAuthor() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message1 = new Message("msg1", LocalDateTime.now(), person, room);
        Message message2 = new Message("msg2", LocalDateTime.now(), person, room);
        Message message3 = new Message("msg3", LocalDateTime.now(), person, room);
        List<Message> messages = List.of(message1, message2, message3);
        Mockito.when(messageService.findByAuthor(person)).thenReturn(messages);
        mvc.perform(
                get("/person/1/room/1/message?authorId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].text", is(message1.getText())))
                .andExpect(jsonPath("$.[0].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[0].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[1].text", is(message2.getText())))
                .andExpect(jsonPath("$.[1].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[1].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[2].text", is(message3.getText())))
                .andExpect(jsonPath("$.[2].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[2].room.id", is(room.getId())));
    }

    @Test
    public void whenSuccessGetPeriod() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message1 = new Message("msg1", LocalDateTime.now(), person, room);
        Message message2 = new Message("msg2", LocalDateTime.now(), person, room);
        Message message3 = new Message("msg3", LocalDateTime.now(), person, room);
        List<Message> messages = List.of(message1, message2, message3);
        Mockito.when(chatService.loadMessages(room, ChatService.TimePeriod.TODAY)).thenReturn(messages);
        mvc.perform(
                get("/person/1/room/1/message?period=TODAY"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].text", is(message1.getText())))
                .andExpect(jsonPath("$.[0].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[0].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[1].text", is(message2.getText())))
                .andExpect(jsonPath("$.[1].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[1].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[2].text", is(message3.getText())))
                .andExpect(jsonPath("$.[2].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[2].room.id", is(room.getId())));
    }

    @Test
    public void whenSuccessGetPeriodAndUser() throws Exception {
        Person person = new Person(1);
        Room room = new Room(1);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        Message message1 = new Message("msg1", LocalDateTime.now(), person, room);
        Message message2 = new Message("msg2", LocalDateTime.now(), person, room);
        Message message3 = new Message("msg3", LocalDateTime.now(), person, room);
        List<Message> messages = List.of(message1, message2, message3);
        Mockito.when(chatService.loadUserMessagesFromRoom(person, room, ChatService.TimePeriod.TODAY)).thenReturn(messages);
        mvc.perform(
                get("/person/1/room/1/message?period=TODAY&authorId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].text", is(message1.getText())))
                .andExpect(jsonPath("$.[0].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[0].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[1].text", is(message2.getText())))
                .andExpect(jsonPath("$.[1].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[1].room.id", is(room.getId())))

                .andExpect(jsonPath("$.[2].text", is(message3.getText())))
                .andExpect(jsonPath("$.[2].author.id", is(person.getId())))
                .andExpect(jsonPath("$.[2].room.id", is(room.getId())));
    }


}