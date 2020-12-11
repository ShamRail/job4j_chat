package ru.job4j.chat.service;

import com.sun.istack.NotNull;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;

import java.util.List;

public interface ChatService {

    /**
     * Представляет собой временной промежуток за который грузим сообщение
     */
    enum TimePeriod {
        TODAY, YESTERDAY, WEEK, MONTH, ANY
    }

    /**
     * Отправляет сообщение в беседу
     * @param person отправитель
     * @param room беседа
     * @param message сообщение
     * @return true - удалось отправить, false - не удалось
     */
    boolean sendMessage(@NotNull Person person, @NotNull Room room, @NotNull Message message);

    /**
     * Загружает сообщения за определенный промежуток времени
     * @param room беседа
     * @param period промежуток времени
     * @return сообщения за этот промежуток
     */
    List<Message> loadMessages(@NotNull Room room, TimePeriod period);

    /**
     * Загружает сообщения выбранного пользователя из определенной беседы
     * @param person пользовательно, чьи сообщения ищем
     * @param room беседа откуда грузим сообщения
     * @param period временный промежуток
     * @return сообщения за этот промежуток
     */
    List<Message> loadUserMessagesFromRoom(@NotNull Person person, @NotNull Room room, TimePeriod period);

}
