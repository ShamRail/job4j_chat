package ru.job4j.chat.service.room;

import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.CRUD;

import java.util.List;

public interface RoomService extends CRUD<Room> {

    boolean leaveRoom(Room room, Person person);

    boolean joinRoom(Room room, Person person);

    List<Person> loadParticipants(Room room);

    List<Room> loadUserCreatedRooms(Person person);

    List<Room> loadUserParticipatedRooms(Person person);

}
