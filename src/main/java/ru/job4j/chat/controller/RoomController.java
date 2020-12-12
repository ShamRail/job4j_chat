package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.controller.exception.ResourceNotFoundException;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import java.util.List;

@RestController
@RequestMapping("/person/{personId}/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private PersonService personService;

    @PostMapping
    public Room createRoom(@PathVariable int personId, @RequestBody Room room) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        room.setCreator(person);
        return roomService.saveOrUpdate(room);
    }

    @PutMapping
    public Room updateRoom(@PathVariable int personId, @RequestParam int id, @RequestBody Room room) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        Room dbRoom = roomService.findById(id).orElseThrow(ResourceNotFoundException::new);
        dbRoom.setName(room.getName());
        return roomService.saveOrUpdate(dbRoom);
    }

    @DeleteMapping
    public void deleteRoom(@PathVariable int personId, @RequestParam int id) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        Room room = roomService.findById(id).orElseThrow(ResourceNotFoundException::new);
        roomService.delete(room);
    }

    @GetMapping
    public Room getById(@PathVariable int personId, @RequestParam int id) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        return roomService.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/all")
    public List<Room> getUserRooms(@PathVariable int personId) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        List<Room> userCreatedRooms = roomService.loadUserCreatedRooms(person);
        userCreatedRooms.addAll(roomService.loadUserParticipatedRooms(person));
        return userCreatedRooms;
    }

    @PutMapping("/join")
    public void joinChat(@PathVariable int personId, @RequestParam int id) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        Room room = roomService.findById(id).orElseThrow(ResourceNotFoundException::new);
        roomService.joinRoom(room, person);
    }

    @PutMapping("/leave")
    public void leaveRoom(@PathVariable int personId, @RequestParam int id) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        Room room = roomService.findById(id).orElseThrow(ResourceNotFoundException::new);
        roomService.leaveRoom(room, person);
    }

}
