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
import ru.job4j.chat.service.person.PersonService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PersonService personService;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    @Test
    public void whenPost() throws Exception {
        Person person = new Person("name", "email", "password");
        Mockito.when(personService.saveOrUpdate(person)).thenReturn(person);
        mvc.perform(
                post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConverter.writeValueAsString(person))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(person.getName())))
                .andExpect(jsonPath("$.email", is(person.getEmail())))
                .andExpect(jsonPath("$.password", is(person.getPassword())));

    }

    @Test
    public void whenPutWithWrongId() throws Exception {
        Person person = new Person("name", "email", "password");
        int id = -1;
        Mockito.when(personService.findById(id)).thenReturn(Optional.empty());
        mvc.perform(
                put("/person?id=-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(person))
        )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void whenPut() throws Exception {
        Person person = new Person("name", "email", "password");
        int id = 1;
        Mockito.when(personService.findById(id)).thenReturn(Optional.of(person));
        Mockito.when(personService.saveOrUpdate(person)).thenReturn(person);
        mvc.perform(
                put("/person?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(person))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteWithWrongId() throws Exception {
        int id = -1;
        Mockito.when(personService.findById(id)).thenReturn(Optional.empty());
        mvc.perform(
                delete("/person?id=-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void whenDelete() throws Exception {
        Person person = new Person("name", "email", "password");
        int id = 1;
        Mockito.when(personService.findById(id)).thenReturn(Optional.of(person));
        mvc.perform(
                delete("/person?id=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetById() throws Exception {
        Person person = new Person("name", "email", "password");
        Mockito.when(personService.findById(1)).thenReturn(Optional.of(person));
        mvc.perform(
                get("/person?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(person.getName())))
                .andExpect(jsonPath("$.email", is(person.getEmail())))
                .andExpect(jsonPath("$.password", is(person.getPassword())));
    }

    @Test
    public void whenGetAll() throws Exception {
        Person person1 = new Person("name1", "email1", "password1");
        Person person2 = new Person("name2", "email2", "password2");
        Mockito.when(personService.findAll()).thenReturn(List.of(person1, person2));
        mvc.perform(
                get("/person/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(person1.getName())))
                .andExpect(jsonPath("$[0].email", is(person1.getEmail())))
                .andExpect(jsonPath("$[0].password", is(person1.getPassword())))
                .andExpect(jsonPath("$[1].name", is(person2.getName())))
                .andExpect(jsonPath("$[1].email", is(person2.getEmail())))
                .andExpect(jsonPath("$[1].password", is(person2.getPassword())));
    }

}