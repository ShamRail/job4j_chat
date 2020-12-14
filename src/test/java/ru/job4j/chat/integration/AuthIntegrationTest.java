package ru.job4j.chat.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.chat.config.Profiles;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.service.person.PersonService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {Profiles.JWT})
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonService personService;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    public Person register(Person person) throws Exception {
        MvcResult mvcResult = mvc.perform(
                post("/person/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(person))
        ).andReturn();
        Person registered = jsonConverter.readValue(mvcResult.getResponse().getContentAsString(), Person.class);
        registered.setPassword(person.getPassword());
        return registered;
    }

    public String login(Person person) throws Exception {
        return mvc.perform(
                post("/person/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.writeValueAsString(person)))
                .andReturn().getResponse().getHeader("Authorization");
    }

    @Test
    public void whenCreate() throws Exception {
        Person registered = register(new Person("name", "email", "password"));
        Person fromDB = personService.findById(registered.getId()).orElse(new Person());
        Assert.assertEquals("name", fromDB.getName());
        Assert.assertEquals("email", fromDB.getEmail());
    }

    @Test
    public void whenLogin() throws Exception {
        Assert.assertNotNull(login(register(new Person("name", "email", "password"))));
    }

    @Test
    public void whenTryToDoOperation() throws Exception {
        Person registered = register(new Person("John", "john@email.ru", "password123"));
        String token = login(registered);
        MvcResult result = mvc.perform(
                get("/person?id=" + registered.getId())
                .header("Authorization", token)
        ).andReturn();
        Person resultPerson = jsonConverter.readValue(
                result.getResponse().getContentAsString(),
                Person.class
        );
        Assert.assertEquals(registered.getName(), resultPerson.getName());
        Assert.assertEquals(registered.getEmail(), resultPerson.getEmail());
    }



}
