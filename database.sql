create database jiccns;
use jiccns;
create table experiments(id integer AUTO_INCREMENT  primary key, hashed varchar(64)unique,epochtime int(11));
create table nodesummary(nodename varchar(20),nodetype varchar(20),exp_id integer, foreign key(exp_id) references experiments(id) on delete cascade,numberofrequestshandled int,numberofrequestsanswered int,numberofrequestsforwarded int,numberofcachehits int,numberofcachemiss int,numberofcachelookups int,numberofhddhits int,numberofhddmiss int,numberofhddlookups int,pc float, epochtime int(11));

