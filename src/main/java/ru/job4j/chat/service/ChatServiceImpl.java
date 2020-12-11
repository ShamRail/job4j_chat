package ru.job4j.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.message.MessageService;
import ru.job4j.chat.service.person.PersonService;
import ru.job4j.chat.service.room.RoomService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private PersonService personService;

    @Autowired
    private RoomService roomService;

    private final Map<TimePeriod, Supplier<TimeDiapason>> dispatcher = new HashMap<>();

    @PostConstruct
    private void initDispatcher() {
        LocalDate toDayDate = LocalDate.now();
        LocalTime startTime = LocalTime.of(0, 0);
        LocalTime endTime = LocalTime.of(23, 59, 59);
        dispatcher.put(TimePeriod.TODAY, () -> new TimeDiapason(
                LocalDateTime.of(toDayDate, startTime),
                LocalDateTime.of(toDayDate, endTime)
        ));
        dispatcher.put(TimePeriod.YESTERDAY, () -> new TimeDiapason(
                LocalDateTime.of(toDayDate.minusDays(1), startTime),
                LocalDateTime.of(toDayDate.minusDays(1), endTime)
        ));
        dispatcher.put(TimePeriod.WEEK, () -> new TimeDiapason(
                LocalDateTime.of(toDayDate.minusWeeks(1), startTime),
                LocalDateTime.of(toDayDate, endTime)
        ));
        dispatcher.put(TimePeriod.MONTH, () -> new TimeDiapason(
                LocalDateTime.of(toDayDate.minusMonths(1), startTime),
                LocalDateTime.of(toDayDate, endTime)
        ));
        dispatcher.put(TimePeriod.ANY, () -> new TimeDiapason(
                LocalDateTime.of(toDayDate.minusYears(10), startTime),
                LocalDateTime.of(toDayDate.plusYears(10), endTime)
        ));
    }

    @Override
    public boolean sendMessage(Person person, Room room, Message message) {
        Optional<Person> personOptional = personService.findById(person.getId());
        if (personOptional.isEmpty()) {
            return false;
        }
        Optional<Room> roomOptional = roomService.findById(room.getId());
        if (roomOptional.isEmpty()) {
            return false;
        }
        message.setAuthor(person);
        message.setRoom(room);
        return messageService.saveOrUpdate(message) != null;
    }

    @Override
    public List<Message> loadMessages(Room room, TimePeriod period) {
        TimeDiapason diapason = dispatcher.get(period).get();
        return messageService.findByRoom(room).stream()
                .filter(msg -> msg.getCreateDate() != null)
                .filter(msg -> {
                    LocalDateTime dateTime = msg.getCreateDate();
                    return (dateTime.isAfter(diapason.getStart()) || dateTime.isEqual(diapason.getStart()))
                            && (dateTime.isBefore(diapason.getEnd()) || dateTime.isEqual(diapason.getEnd()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> loadUserMessagesFromRoom(Person person, Room room, TimePeriod period) {
        TimeDiapason diapason = dispatcher.get(period).get();
        return messageService.findByAuthorAndRoomAndCreateDateBetween(
                person, room,
                diapason.getStart(), diapason.getEnd()
        );
    }

    private static final class TimeDiapason {

        private final LocalDateTime start;

        private final LocalDateTime end;

        public TimeDiapason(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }
    }

}
