package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private PersonService personService;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    @Test
    public void whenCreateAndUserNotFound() throws Exception {
        Room room = new Room("room");
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                post("/person/1/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConverter.writeValueAsString(room).getBytes())
        )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void whenSuccessCreate() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.saveOrUpdate(room)).thenReturn(room);
        mvc.perform(
                post("/person/1/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(room).getBytes())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(room.getName())))
                .andExpect(jsonPath("$.creator.name", is(person.getName())))
                .andExpect(jsonPath("$.creator.email", is(person.getEmail())))
                .andExpect(jsonPath("$.creator.password", is(person.getPassword())));

    }

    @Test
    public void whenPutAndUserNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person/1/room?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(room).getBytes())
        )
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenPutAndRoomNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person/1/room?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(room).getBytes())
        )
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenSuccessPut() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        room.setName("updated");
        Mockito.when(roomService.saveOrUpdate(room)).thenReturn(room);
        Room updated = new Room("updated");
        mvc.perform(
                put("/person/1/room?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(updated).getBytes())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(updated.getName())))
                .andExpect(jsonPath("$.creator.name", is(person.getName())))
                .andExpect(jsonPath("$.creator.email", is(person.getEmail())))
                .andExpect(jsonPath("$.creator.password", is(person.getPassword())));

    }

    @Test
    public void whenDeleteAndUserNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                delete("/person/1/room?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenDeleteAndRoomNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                delete("/person/1/room?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenSuccessDelete() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        mvc.perform(
                delete("/person/1/room?id=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetByIdAndUserNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                get("/person/1/room?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenGetByIdAndRoomNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                get("/person/1/room?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenSuccessGetById() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        mvc.perform(
                get("/person/1/room?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(room.getName())))
                .andExpect(jsonPath("$.creator.name", is(person.getName())))
                .andExpect(jsonPath("$.creator.email", is(person.getEmail())))
                .andExpect(jsonPath("$.creator.password", is(person.getPassword())));

    }

    @Test
    public void whenGetUserRoomsAndUserNotFound() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        room.setCreator(person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                get("/person/1/room/all"))
                .andExpect(status().is4xxClientError());

    }


    @Test
    public void whenSuccessGetUserRooms() throws Exception {
        Person person = new Person("name", "email", "password");
        Room room1 = new Room("room1", person);
        Room room2 = new Room("room2", person);
        Room room3 = new Room("room3", person);
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        List<Room> rooms = List.of(room1, room2, room3);
        Mockito.when(roomService.loadUserCreatedRooms(person)).thenReturn(rooms);
        mvc.perform(
                get("/person/1/room/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is(room1.getName())))
                .andExpect(jsonPath("$[0].creator.name", is(person.getName())))
                .andExpect(jsonPath("$[0].creator.email", is(person.getEmail())))
                .andExpect(jsonPath("$[0].creator.password", is(person.getPassword())))

                .andExpect(jsonPath("$[1].name", is(room2.getName())))
                .andExpect(jsonPath("$[1].creator.name", is(person.getName())))
                .andExpect(jsonPath("$[1].creator.email", is(person.getEmail())))
                .andExpect(jsonPath("$[1].creator.password", is(person.getPassword())))

                .andExpect(jsonPath("$[2].name", is(room3.getName())))
                .andExpect(jsonPath("$[2].creator.name", is(person.getName())))
                .andExpect(jsonPath("$[2].creator.email", is(person.getEmail())))
                .andExpect(jsonPath("$[2].creator.password", is(person.getPassword())));

    }

    @Test
    public void whenJoinAndUserNotFound() throws Exception {
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person/1/room/join?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenJoinAndRoomNotFound() throws Exception {
        Person person = new Person("name", "email", "password");
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person/1/room/join?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenSuccessJoin() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        mvc.perform(
                put("/person/1/room/join?id=1"))
                .andExpect(status().isOk());

    }

    @Test
    public void whenLeaveAndUserNotFound() throws Exception {
        Mockito.when(personService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person/1/room/leave?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenLeaveAndRoomNotFound() throws Exception {
        Person person = new Person("name", "email", "password");
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person/1/room/leave?id=1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenSuccessLeave() throws Exception {
        Room room = new Room("room");
        Person person = new Person("name", "email", "password");
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        Mockito.when(roomService.findById(1)).thenReturn(Optional.of(room));
        mvc.perform(
                put("/person/1/room/leave?id=1"))
                .andExpect(status().isOk());

    }

}