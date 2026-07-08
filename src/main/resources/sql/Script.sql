create table contribution(
	contribution_code integer primary key,
	contribution_name text not null,
	months integer not null,
	rate real not null
)


select * from contribution

drop  table client
drop  table client_account
drop  table contribution


create table client(
	client_code integer primary key,
	first_name text not null,
	middle_name text,
	last_name text  not null,
	passport_number text not null,
	client_adress text not null,
	phone_number text not null
)

select * from client
insert into client (client_code) values (5)



create table client_account(
	account_number integer primary key not null,
	client_code integer references client not null unique,
	contribution_code integer references contribution not null unique,
	opening_date date not null,
	closing_date date not null,
	invested_amount real not null
)

select * from client_account
insert into client_account (client_code) values (5)























