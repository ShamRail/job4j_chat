package ru.job4j.chat.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;

    private LocalDateTime createDate;

    @ManyToOne
    @Column(name = "author_id")
    private Person author;

    @ManyToOne
    @Column(name = "room_id")
    private Room room;

    public Message() { }

    public Message(String text, LocalDateTime createDate, @NotNull Person author, @NotNull Room room) {
        this.text = text;
        this.createDate = createDate;
        this.author = author;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return id == message.id
                && author.equals(message.author)
                && room.equals(message.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, room);
    }
}
