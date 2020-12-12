package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.controller.exception.ResourceNotFoundException;
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

@RestController
@RequestMapping("/person/{personId}/room/{roomId}/message")
public class MessageController {

    @Autowired
    private PersonService personService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @PostMapping
    public Message createMessage(@PathVariable int personId, @PathVariable int roomId, @RequestBody MessageDTO messageDTO) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        Room room = roomService.findById(roomId).orElseThrow(ResourceNotFoundException::new);
        Message message = new Message(messageDTO.getText());
        message.setRoom(room);
        message.setAuthor(person);
        message.setCreateDate(LocalDateTime.now());
        return messageService.saveOrUpdate(message);
    }

    @PutMapping
    public void updateMessage(
            @PathVariable int personId, @PathVariable int roomId,
            @RequestParam int id, @RequestBody MessageDTO messageDTO) {
        personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        roomService.findById(roomId).orElseThrow(ResourceNotFoundException::new);
        Message message = messageService.findById(id).orElseThrow(ResourceNotFoundException::new);
        message.setText(messageDTO.getText());
        messageService.saveOrUpdate(message);
    }

    @DeleteMapping
    public void deleteMessage(
            @PathVariable int personId, @PathVariable int roomId,
            @RequestParam int id) {
        personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        roomService.findById(roomId).orElseThrow(ResourceNotFoundException::new);
        Message message = messageService.findById(id).orElseThrow(ResourceNotFoundException::new);
        messageService.delete(message);
    }

    @GetMapping
    public List<Message> getBy(
            @PathVariable int personId, @PathVariable int roomId,
            @RequestParam(required = false) Integer authorId,
            @RequestParam(required = false) ChatService.TimePeriod period) {
        Person person = personService.findById(personId).orElseThrow(ResourceNotFoundException::new);
        Room room = roomService.findById(roomId).orElseThrow(ResourceNotFoundException::new);
        if (authorId == null && period == null) {
            return messageService.findByRoom(room);
        }
        if (authorId == null && period != null) {
            return chatService.loadMessages(room, period);
        }
        if (authorId != null && period == null) {
            return messageService.findByAuthor(
                    personService.findById(authorId).orElseThrow(ResourceNotFoundException::new)
            );
        }
        if (authorId != null && period != null) {
            return chatService.loadUserMessagesFromRoom(
                    personService.findById(authorId).orElseThrow(ResourceNotFoundException::new),
                    room, period
            );
        }
        return List.of();
    }


}
