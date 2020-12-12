package ru.job4j.chat.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.model.dto.MessageDTO;
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonConverter;

    @Autowired
    private PersonService personService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RoomService roomService;

    @Test
    public void whenCreate() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        MessageDTO messageDTO = new MessageDTO("msg");
        mvc.perform(
                post(String.format("/person/%d/room/%d/message", author.getId(), room.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConverter.writeValueAsString(messageDTO))
        ).andReturn();
        Message fromDB = messageService.findAll().get(0);
        Assert.assertEquals(messageDTO.getText(), fromDB.getText());
        Assert.assertEquals(author, fromDB.getAuthor());
        Assert.assertEquals(room, fromDB.getRoom());
    }

    @Test
    public void whenUpdate() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message = messageService.saveOrUpdate(new Message("msg", author, room));
        MessageDTO messageDTO = new MessageDTO("updated");

        mvc.perform(
                put(String.format("/person/%d/room/%d/message?id=%d", author.getId(), room.getId(), message.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConverter.writeValueAsString(messageDTO))
        ).andExpect(status().isOk());

        Message fromDB = messageService.findById(message.getId()).orElse(null);
        Assert.assertEquals(messageDTO.getText(), fromDB.getText());
        Assert.assertEquals(author, fromDB.getAuthor());
        Assert.assertEquals(room, fromDB.getRoom());
    }

    @Test
    public void whenDelete() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message = messageService.saveOrUpdate(new Message("msg", author, room));
        mvc.perform(
                delete(String.format("/person/%d/room/%d/message?id=%d", author.getId(), room.getId(), message.getId()))
        ).andExpect(status().isOk());
        Assert.assertEquals(Optional.empty(), messageService.findById(message.getId()));
    }

    @Test
    public void whenLoadRoomMessages() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message1 = messageService.saveOrUpdate(new Message("msg1", author, room));
        Message message2 = messageService.saveOrUpdate(new Message("msg2", author, room));
        Message message3 = messageService.saveOrUpdate(new Message("msg3", author, room));

        MvcResult result = mvc.perform(
                get(String.format("/person/%d/room/%d/message", author.getId(), room.getId()))
        ).andReturn();
        List<Message> messages = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<Message>>() { }
        );
        Assert.assertEquals(List.of(message1, message2, message3), messages);
    }

    @Test
    public void whenLoadRoomMessagesByAuthor() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person author2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message1 = messageService.saveOrUpdate(new Message("msg1", author, room));
        Message message2 = messageService.saveOrUpdate(new Message("msg2", author, room));
        Message message3 = messageService.saveOrUpdate(new Message("msg3", author2, room));

        MvcResult result = mvc.perform(
                get(String.format("/person/%d/room/%d/message?authorId=%d", author.getId(), room.getId(), author.getId()))
        ).andReturn();
        List<Message> messages = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<Message>>() { }
        );
        Assert.assertEquals(List.of(message1, message2), messages);
    }

    @Test
    public void whenLoadRoomMessagesTODAY() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person author2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message1 = messageService.saveOrUpdate(new Message("msg1", LocalDateTime.now().minusDays(2), author, room));
        Message message2 = messageService.saveOrUpdate(new Message("msg2", LocalDateTime.now().minusDays(1), author, room));
        Message message3 = messageService.saveOrUpdate(new Message("msg3", LocalDateTime.now(), author2, room));

        MvcResult result = mvc.perform(
                get(String.format("/person/%d/room/%d/message?period=TODAY", author.getId(), room.getId()))
        ).andReturn();

        List<Message> messages = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<Message>>() { }
        );
        Assert.assertEquals(List.of(message3), messages);
    }

    @Test
    public void whenLoadRoomMessagesYESTERDAY() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person author2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message1 = messageService.saveOrUpdate(new Message("msg1", LocalDateTime.now().minusDays(2), author, room));
        Message message2 = messageService.saveOrUpdate(new Message("msg2", LocalDateTime.now().minusDays(1), author, room));
        Message message3 = messageService.saveOrUpdate(new Message("msg3", LocalDateTime.now(), author2, room));

        MvcResult result = mvc.perform(
                get(String.format("/person/%d/room/%d/message?period=YESTERDAY", author.getId(), room.getId()))
        ).andReturn();

        List<Message> messages = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<Message>>() { }
        );
        Assert.assertEquals(List.of(message2), messages);
    }

    @Test
    public void whenLoadRoomMessagesANY() throws Exception {
        Person author = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person author2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", author));
        Message message1 = messageService.saveOrUpdate(new Message("msg1", LocalDateTime.now().minusDays(2), author, room));
        Message message2 = messageService.saveOrUpdate(new Message("msg2", LocalDateTime.now().minusDays(1), author, room));
        Message message3 = messageService.saveOrUpdate(new Message("msg3", LocalDateTime.now(), author2, room));

        MvcResult result = mvc.perform(
                get(String.format("/person/%d/room/%d/message?period=ANY", author.getId(), room.getId()))
        ).andReturn();

        List<Message> messages = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<Message>>() { }
        );
        Assert.assertEquals(List.of(message1, message2, message3), messages);
    }

}
