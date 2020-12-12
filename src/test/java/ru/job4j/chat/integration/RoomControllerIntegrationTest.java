package ru.job4j.chat.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MessageService messageService;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    @Test
    public void whenCreateRoom() throws Exception {
        Person person = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = new Room("room");
        MvcResult result = mvc.perform(
                post("/person/" + person.getId() + "/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConverter.writeValueAsString(room))
        ).andReturn();
        int id = jsonConverter.readValue(result.getResponse().getContentAsString(), Room.class).getId();
        room.setId(id);
        room.setCreator(person);
        Assert.assertEquals(room, roomService.findById(id).orElse(new Room()));
    }

    @Test
    public void whenUpdateRoom() throws Exception {
        Person person = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", person));
        Room updated = new Room("updated");
        mvc.perform(
                put("/person/" + person.getId() + "/room?id=" + room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(updated))
        );
        updated.setId(room.getId());
        updated.setCreator(person);
        Assert.assertEquals(updated, roomService.findById(updated.getId()).orElse(new Room()));
    }

    @Test
    public void whenGetById() throws Exception {
        Person person = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room", person));
        MvcResult result = mvc.perform(
                get("/person/" + person.getId() + "/room?id=" + room.getId())
        ).andReturn();
        Room retrieved = jsonConverter.readValue(result.getResponse().getContentAsString(), Room.class);
        Assert.assertEquals(room, retrieved);
    }

    @Test
    public void whenGetUserCreatedAndParticipatedRooms() throws Exception {
        Person person1 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room1 = roomService.saveOrUpdate(new Room("room1", person1));
        Room room2 = roomService.saveOrUpdate(new Room("room2", person1));
        Room room3 = roomService.saveOrUpdate(new Room("room2", person2));
        roomService.joinRoom(room3, person1);
        MvcResult result = mvc.perform(
                get("/person/" + person1.getId() + "/room/all")
        ).andReturn();
        List<Room> rooms = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<Room>>() { }
        );
        Assert.assertEquals(List.of(room1, room2, room3), rooms);
    }

    @Test
    public void whenJoinChat() throws Exception {
        Person person1 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person3 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room1", person1));
        mvc.perform(put("/person/" + person2.getId() + "/room/join?id=" + room.getId()));
        mvc.perform(put("/person/" + person3.getId() + "/room/join?id=" + room.getId()));
        Assert.assertEquals(List.of(person1, person2, person3), roomService.loadParticipants(room));
    }

    @Test
    public void whenLeaveChat() throws Exception {
        Person person1 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person3 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room1", person1));
        roomService.joinRoom(room, person2);
        roomService.joinRoom(room, person3);
        mvc.perform(put("/person/" + person2.getId() + "/room/leave?id=" + room.getId()));
        Assert.assertEquals(List.of(person1, person3), roomService.loadParticipants(room));
    }

    @Test
    public void whenDeleteThenDeleteAllRelated() throws Exception {
        Person person1 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person2 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person person3 = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room room = roomService.saveOrUpdate(new Room("room1", person1));
        messageService.saveOrUpdate(new Message("msg1", person1, room));
        messageService.saveOrUpdate(new Message("msg2", person2, room));
        messageService.saveOrUpdate(new Message("msg3", person3, room));
        roomService.joinRoom(room, person2);
        roomService.joinRoom(room, person3);
        mvc.perform(delete("/person/" + person1.getId() + "/room?id=" + room.getId()));
        Assert.assertEquals(List.of(), messageService.findByRoom(room));
        Assert.assertEquals(List.of(), roomService.loadUserParticipatedRooms(person1));
        Assert.assertEquals(List.of(), roomService.loadUserParticipatedRooms(person2));
        Assert.assertEquals(List.of(), roomService.loadUserParticipatedRooms(person3));
        Assert.assertEquals(Optional.empty(), roomService.findById(room.getId()));
    }

}
