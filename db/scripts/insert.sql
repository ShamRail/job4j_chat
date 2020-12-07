insert into person(name, email, password) values('Alex', 'email1@mail.ru', '123');
insert into person(name, email, password) values('Mark', 'email2@mail.ru', '123');
insert into person(name, email, password) values('Vanessa', 'email3@mail.ru', '123');

insert into room(name, creator_id) values('room1', 1);
insert into room(name, creator_id) values('room2', 2);
insert into room(name, creator_id) values('room3', 3);

insert into message(text, author_id, room_id) values('msg1', 1, 1);
insert into message(text, author_id, room_id) values('msg2', 2, 1);
insert into message(text, author_id, room_id) values('msg3', 3, 1);

insert into message(text, author_id, room_id) values('msg4', 1, 2);
insert into message(text, author_id, room_id) values('msg5', 2, 2);
insert into message(text, author_id, room_id) values('msg6', 3, 2);

insert into message(text, author_id, room_id) values('msg7', 1, 3);
insert into message(text, author_id, room_id) values('msg8', 2, 3);
insert into message(text, author_id, room_id) values('msg9', 3, 3);

insert into participation(person_id, room_id) values(1, 1);
insert into participation(person_id, room_id) values(1, 2);
insert into participation(person_id, room_id) values(1, 3);

insert into participation(person_id, room_id) values(2, 1);
insert into participation(person_id, room_id) values(2, 2);
insert into participation(person_id, room_id) values(2, 3);

insert into participation(person_id, room_id) values(3, 1);
insert into participation(person_id, room_id) values(3, 2);
insert into participation(person_id, room_id) values(3, 3);
