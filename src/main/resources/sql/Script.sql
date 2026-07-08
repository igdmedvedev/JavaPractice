create sequence contribution_serial start  500


create table contribution(
	contribution_code integer primary key default nextval ('contribution_serial'),
	contribution_name text not null,
	months integer not null,
	rate real not null
)

insert into contribution (contribution_name, months, rate) 
values ('Hot', 12, 13.5)

insert into contribution (contribution_name, months, rate) 
values ('Sale', 24, 15.5)

select * from contribution

drop  table client
drop  table client_account
drop  table contribution cascade

create sequence client_serial start  100

create table client(
	client_code integer primary key default nextval('client_serial'),
	first_name text not null,
	middle_name text,
	last_name text  not null,
	passport_number text not null,
	client_adress text not null,
	phone_number text not null
)

drop table client cascade 

select * from client
insert into client (client_code) values (5)

insert into client (first_name, middle_name, last_name, passport_number, client_adress, phone_number) 
values ('Dima', 'Ivanovich', 'Ivanov', '789258', 'Lenin Street 20', '89544563653')

insert into client (first_name, last_name, passport_number, client_adress, phone_number) 
values ('Petya', 'Soloveyv', '810210', 'Lenin Street 81', '89632145635')


create sequence client_account_serial start  1001

create table client_account(
	account_number integer primary key not null default nextval ('client_account_serial'),
	client_code integer references client not null unique,
	contribution_code integer references contribution not null unique,
	opening_date date not null,
	closing_date date not null,
	invested_amount real not null
)

insert into client_account (client_code, contribution_code, opening_date, closing_date, invested_amount) 
values (123, 451, '2025-12-12', '2026-06-06', 100000)

insert into client_account (client_code, contribution_code, opening_date, closing_date, invested_amount) 
values (536, 862, '2025-04-05', '2026-04-12', 500000)


select * from client_account
insert into client_account (client_code) values (5)























