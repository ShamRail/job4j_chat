package ru.job4j.chat.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.chat.dao.PersonRepository;
import ru.job4j.chat.model.Person;

import java.util.Collections;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Person person = personRepository.findByName(name);
        if (person == null) {
            throw new UsernameNotFoundException(name);
        }
        return new User(person.getName(), person.getPassword(), Collections.emptyList());
    }
}
