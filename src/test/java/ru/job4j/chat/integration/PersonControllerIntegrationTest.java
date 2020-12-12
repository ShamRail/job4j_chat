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
public class PersonControllerIntegrationTest {

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
    public void whenCreate() throws Exception {
        Person person = new Person("name", "email", "password");
        MvcResult mvcResult = mvc.perform(
                post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConverter.writeValueAsString(person))
        ).andReturn();
        int id = jsonConverter.readValue(
                mvcResult.getResponse().getContentAsString(),
                Person.class
        ).getId();
        person.setId(id);
        Assert.assertEquals(person, personService.findById(id).orElse(new Person()));
    }

    @Test
    public void whenUpdate() throws Exception {
        Person person = personService.saveOrUpdate(new Person("name", "email", "password"));
        Person updated = new Person("name1", "email1", "password1");
        updated.setId(person.getId());
        mvc.perform(
                put("/person?id=" + updated.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(updated))
        );
        Assert.assertEquals(updated, personService.findById(updated.getId()).orElse(new Person()));
    }

    @Test
    public void whenFindByById() throws Exception {
        Person person = personService.saveOrUpdate(new Person("name", "email", "password"));
        MvcResult mvcResult = mvc.perform(
                get("/person?id=" + person.getId())
        ).andReturn();
        Person result = jsonConverter.readValue(mvcResult.getResponse().getContentAsString(), Person.class);
        Assert.assertEquals(person, result);
    }

    @Test
    public void whenFindAll() throws Exception {
        Person person1 = personService.saveOrUpdate(new Person("name1", "email1", "password1"));
        Person person2 = personService.saveOrUpdate(new Person("name2", "email2", "password2"));
        MvcResult mvcResult = mvc.perform(
                get("/person/all")
        ).andReturn();
        List<Person> result = jsonConverter.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() { }
        );
        Assert.assertEquals(List.of(person1, person2), result);
    }

    @Test
    public void whenDeletePersonThenDeleteAllRelated() {
        Person person = personService.saveOrUpdate(new Person("name", "email", "password"));
        Room personRoom = roomService.saveOrUpdate(new Room("room", person));
        Room personJoined = roomService.saveOrUpdate(new Room("room"));
        roomService.joinRoom(personJoined, person);
        Message message1 = messageService.saveOrUpdate(new Message("msg1", person, personRoom));
        Message message2 = messageService.saveOrUpdate(new Message("msg1", person, personJoined));
        personService.delete(person);
        Assert.assertEquals(Optional.empty(), messageService.findById(message1.getId()));
        Assert.assertEquals(Optional.empty(), messageService.findById(message2.getId()));
        Assert.assertEquals(Optional.empty(), roomService.findById(personRoom.getId()));
        Assert.assertEquals(List.of(), roomService.loadParticipants(personJoined));
        Assert.assertEquals(Optional.of(personJoined), roomService.findById(personJoined.getId()));
        Assert.assertEquals(Optional.empty(), personService.findById(person.getId()));
    }

}
