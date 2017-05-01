/* This is the create table file with initial data values */
/* When needing to clear everything here is the command sequence
   to run for a local machine: */
/*
DROP TABLE Bill;
DROP TABLE Employee;
DROP TABLE ResXFee;
DROP TABLE Reservation;
DROP TABLE Fees;
DROP TABLE SpecialRates;
DROP TABLE HotelWideRates;
DROP TABLE Room;
DROP TABLE Banking;
DROP TABLE Customer;
DROP TABLE Login; */

CREATE TABLE Login(
	id       SERIAL PRIMARY KEY,
	username TEXT   NOT NULL UNIQUE,
	password TEXT   NOT NULL,
	title    TEXT   NOT NULL -- admin, employee, or customer
);

CREATE TABLE Customer(
	cid       INT  PRIMARY KEY REFERENCES Login(id),
	firstName TEXT NOT NULL,
	lastName  TEXT NOT NULL,
	email     TEXT NOT NULL,
	address   TEXT NOT NULL
);

CREATE TABLE Banking(
	cid     INT    REFERENCES Customer(cid),
	ccNum   TEXT   NOT NULL,
	cvc     DECIMAL(3,0) NOT NULL,
	expDate DATE   NOT NULL,
	type    TEXT   NOT NULL -- visa, mastercard
);

CREATE TABLE Room(
	rmNum INT   PRIMARY KEY,
	view  TEXT  NOT NULL, -- ocean view or pool view
	bed   TEXT  NOT NULL, -- king or queen
	price DECIMAL(6, 2) NOT NULL
);

CREATE TABLE Reservation(
	resId     SERIAL PRIMARY KEY,
	roomId    INT    REFERENCES Room(rmNum),
	custId    INT    REFERENCES Customer(cid),
	startDate DATE   NOT NULL,
	endDate   DATE   NOT NULL CHECK(endDate > startDate),
	baseCost  DECIMAL(6, 2) NOT NULL,
        checkedIn BOOLEAN NOT NULL DEFAULT False
);

CREATE TABLE SpecialRates (
	rmNum     INT  REFERENCES Room(rmNum),
	date DATE NOT NULL,
	price     DECIMAL(6, 2) NOT NULL CHECK(price > 0)
);

CREATE TABLE HotelWideRates (
   date DATE NOT NUlL,
   price DECIMAL(6, 2) NOT NULL CHECK(price > 0)
);

CREATE TABLE Fees (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    price DECIMAL(6, 2) NOT NULL
);

CREATE TABLE ResXFee (
    id SERIAL PRIMARY KEY,
    resId INT REFERENCES Reservation(resId),
    feeId INT REFERENCES Fees(id)
);

CREATE TABLE Bill (
    ccNum  TEXT REFERENCES Banking(ccNum),
    date   DATE NOT NULL,
    cost   DECIMAL(6,2) NOT NULL,
    custId INT REFERENCES Customer(cid)
);

CREATE TABLE Employee(
    eid INT REFERENCES Login(id),
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL
);
-- Test data
-- Rooms
INSERT INTO Room(rmNum, view, bed, price)
VALUES (101, 'ocean', 'single king', 100.00),
	(102, 'ocean', 'double queen', 100.00),
	(103, 'ocean', 'single king', 100.00),
	(104, 'ocean', 'double queen', 100.00),
	(105, 'ocean', 'single king', 100.00),
	(106, 'ocean', 'double queen', 100.00),
	(107, 'pool', 'single king', 100.00),
	(108, 'pool', 'double queen', 100.00),
	(109, 'pool', 'single king', 100.00),
	(110, 'pool', 'double queen', 100.00),
	(111, 'pool', 'single king', 100.00),
	(112, 'pool', 'double queen', 100.00),

	(201, 'ocean', 'single king', 100.00),
	(202, 'ocean', 'double queen', 100.00),
	(203, 'ocean', 'single king', 100.00),
	(204, 'ocean', 'double queen', 100.00),
	(205, 'ocean', 'single king', 100.00),
	(206, 'ocean', 'double queen', 100.00),
	(207, 'pool', 'single king', 100.00),
	(208, 'pool', 'double queen', 100.00),
	(209, 'pool', 'single king', 100.00),
	(210, 'pool', 'double queen', 100.00),
	(211, 'pool', 'single king', 100.00),
	(212, 'pool', 'double queen', 100.00),

	(301, 'ocean', 'single king', 100.00),
	(302, 'ocean', 'double queen', 100.00),
	(303, 'ocean', 'single king', 100.00),
	(304, 'ocean', 'double queen', 100.00),
	(305, 'ocean', 'single king', 100.00),
	(306, 'ocean', 'double queen', 100.00),
	(307, 'pool', 'single king', 100.00),
	(308, 'pool', 'double queen', 100.00),
	(309, 'pool', 'single king', 100.00),
	(310, 'pool', 'double queen', 100.00),
	(311, 'pool', 'single king', 100.00),
	(312, 'pool', 'double queen', 100.00),

	(401, 'ocean', 'single king', 100.00),
	(402, 'ocean', 'double queen', 100.00),
	(403, 'ocean', 'single king', 100.00),
	(404, 'ocean', 'double queen', 100.00),
	(405, 'ocean', 'single king', 100.00),
	(406, 'ocean', 'double queen', 100.00),
	(407, 'pool', 'single king', 100.00),
	(408, 'pool', 'double queen', 100.00),
	(409, 'pool', 'single king', 100.00),
	(410, 'pool', 'double queen', 100.00),
	(411, 'pool', 'single king', 100.00),
	(412, 'pool', 'double queen', 100.00),

	(501, 'ocean', 'single king', 100.00),
	(502, 'ocean', 'double queen', 100.00),
	(503, 'ocean', 'single king', 100.00),
	(504, 'ocean', 'double queen', 100.00),
	(505, 'ocean', 'single king', 100.00),
	(506, 'ocean', 'double queen', 100.00),
	(507, 'pool', 'single king', 100.00),
	(508, 'pool', 'double queen', 100.00),
	(509, 'pool', 'single king', 100.00),
	(510, 'pool', 'double queen', 100.00),
	(511, 'pool', 'single king', 100.00),
	(512, 'pool', 'double queen', 100.00);
-- Logins
INSERT INTO Login(username, password, title)
VALUES ('admin', 'admin', 'admin');

-- Standard fee entries
INSERT INTO Fees(id, name, price)
VALUES (0, 'init', 0.00);
INSERT INTO Fees(name, price)
VALUES ('breakfast', 10.00), 
    ('wifi', 20.00), 
    ('cleaning', 25.00),
    ('alcohol', 5.00);

GRANT ALL PRIVILEGES ON ALL TABLES
IN SCHEMA PUBLIC TO edybsett;
GRANT ALL PRIVILEGES ON ALL SEQUENCES
IN SCHEMA PUBLIC TO edybsett;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS
IN SCHEMA PUBLIC TO edybsett;
GRANT ALL PRIVILEGES ON ALL TABLES
IN SCHEMA PUBLIC TO jkmar;
GRANT ALL PRIVILEGES ON ALL SEQUENCES
IN SCHEMA PUBLIC TO jkmar;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS
IN SCHEMA PUBLIC TO jkmar;
GRANT ALL PRIVILEGES ON ALL TABLES
IN SCHEMA PUBLIC TO mhetrick;
GRANT ALL PRIVILEGES ON ALL SEQUENCES
IN SCHEMA PUBLIC TO mhetrick;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS
IN SCHEMA PUBLIC TO mehtrick;