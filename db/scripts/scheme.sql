create table if not exists person(
	id serial primary key,
	name text,
	email text,
	password text
);

create table if not exists room(
	id serial primary key,
	name text default 'unknown',
	creator_id int references person(id) on delete no action
);

create table if not exists message(
	id serial primary key,
	text text default '',
	create_date timestamp default now(),
	author_id int references person(id) on delete no action, 
	room_id int references room(id) on delete no action
);

create table if not exists participation(
	id serial primary key,
	room_id int references room(id) on delete no action,
	person_id int references person(id) on delete no action
);
