package ru.job4j.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByCreator(Person person);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            nativeQuery = true,
            value = "delete from participation p where p.room_id = :room and p.person_id = :person"
    )
    int removeRelation(Room room, Person person);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            nativeQuery = true,
            value = "insert into participation values(:person, :room)"
    )
    int addRelation(Person person, Room room);

}
