DROP  table COMMON_TABLE CASCADE;
CREATE table COMMON_TABLE(ID INTEGER, BRANDNAME varchar(25), PRICE REAL, constraint COMMON_TABLE primary key(ID));

DROP  table COMMON_TABLE2 CASCADE;
CREATE table COMMON_TABLE2(ID INTEGER, BRANDNAME varchar(25), PRICE REAL, constraint COMMON_TABLE2 primary key(ID));

DROP  table COMMON_TABLE3 CASCADE;
CREATE table COMMON_TABLE3(ID INTEGER, BRANDNAME varchar(25), PRICE REAL, constraint COMMON_TABLE3 primary key(ID));

DROP table LOCALACCESSTEST_TABLE1 CASCADE;
CREATE table LOCALACCESSTEST_TABLE1(id  INTEGER,name  varchar(25),value  REAL, constraint LOCALACCESSTEST_TABLE1 primary key(id));

DROP table EBACCESSTEST_TABLE2 CASCADE;
CREATE table EBACCESSTEST_TABLE2(id  INTEGER,name  varchar(25),value REAL, constraint EBACCESSTEST_TABLE2 primary key(id));

DROP  table ENTITY_BEAN_TABLE1 CASCADE;
CREATE table ENTITY_BEAN_TABLE1 (KEY_ID  INTEGER,BRAND_NAME  varchar(25),PRICE  REAL, constraint ENTITY_BEAN_TABLE1 primary key(KEY_ID));

DROP  table ADDRESSEJB_TABLE cascade;
CREATE TABLE ADDRESSEJB_TABLE (id VARCHAR2(255) PRIMARY KEY,        street VARCHAR2(255),        city VARCHAR2(255),        state VARCHAR2(255),zip VARCHAR2(255),FK5_FOR_CUSTOMEREJB_TABLE VARCHAR2(255),FK6_FOR_CUSTOMEREJB_TABLE VARCHAR2(255));

DROP table PHONEEJB_TABLE cascade;
CREATE TABLE PHONEEJB_TABLE(id VARCHAR2(255) PRIMARY KEY,area VARCHAR2(255),phone_number VARCHAR2(255),FK_FOR_ADDRESSEJB_TABLE VARCHAR2(255));

DROP table CUSTOMEREJB_TABLE cascade;
CREATE TABLE CUSTOMEREJB_TABLE(id VARCHAR2(255) PRIMARY KEY,name VARCHAR2(255) ,country blob(4056));

DROP table ALIASEJB_TABLE cascade;
CREATE TABLE ALIASEJB_TABLE(id VARCHAR2(255) PRIMARY KEY,         alias VARCHAR2(255),         FK1_FOR_CUSTOMEREJB_TABLE  VARCHAR2(255),         FK2_FOR_CUSTOMEREJB_TABLE  VARCHAR2(255));

DROP table FKS_FOR_ALIAS_CUSTOMER cascade;
CREATE TABLE FKS_FOR_ALIAS_CUSTOMER( FK_FOR_CUSTOMEREJB_TABLE VARCHAR2(255) ,           FK_FOR_ALIASEJB_TABLE VARCHAR2(255));

DROP table FKS_ALIASNOOP_CUSTNOOP cascade;
CREATE TABLE FKS_ALIASNOOP_CUSTNOOP( FK8_FOR_CUSTOMEREJB_TABLE VARCHAR2(255) ,           FK2_FOR_ALIASEJB_TABLE VARCHAR2(255));

DROP table CREDITCARDEJB_TABLE cascade;
CREATE TABLE CREDITCARDEJB_TABLE(id VARCHAR2(255) PRIMARY KEY, type VARCHAR2(255), expires VARCHAR2(255), approved NUMBER,  creditcard_number VARCHAR2(255), balance DOUBLE, FK3_FOR_CUSTOMEREJB_TABLE VARCHAR2(255),         FK_FOR_ORDEREJB_TABLE VARCHAR2(255));

DROP table ORDEREJB_TABLE cascade;
CREATE TABLE  ORDEREJB_TABLE       (id VARCHAR2(255) PRIMARY KEY,        totalPrice DOUBLE ,        FK4_FOR_CUSTOMEREJB_TABLE VARCHAR2(255),        FK0_FOR_LINEITEMEJB_TABLE VARCHAR2(255));

DROP table PRODUCTEJB_TABLE cascade;
CREATE TABLE PRODUCTEJB_TABLE(id VARCHAR2(255) PRIMARY KEY,name VARCHAR2(255),price DOUBLE,quantity integer, pnum BIGINT);

DROP table SPOUSEEJB_TABLE cascade;
CREATE TABLE SPOUSEEJB_TABLE (ID VARCHAR2(255) PRIMARY KEY,  FIRSTNAME VARCHAR2(255), MAIDENNAME VARCHAR2(255), LASTNAME VARCHAR2(255), SOCSECNUM VARCHAR2(255), FK7_FOR_CUSTOMEREJB_TABLE VARCHAR2(255), FK_FOR_INFOEJB_TABLE VARCHAR2(255) );

DROP table INFOEJB_TABLE cascade;
CREATE TABLE INFOEJB_TABLE (ID VARCHAR2(255) PRIMARY KEY, INFOSTREET VARCHAR2(255), INFOCITY VARCHAR2(255), INFOSTATE VARCHAR2(255), INFOZIP VARCHAR2(255), FK_FOR_SPOUSEEJB_TABLE VARCHAR2(255) );

DROP table LINEITEMEJB_TABLE cascade;
CREATE TABLE LINEITEMEJB_TABLE(id VARCHAR2(255) PRIMARY KEY,quantity integer,FK_FOR_PRODUCTEJB_TABLE VARCHAR2(255),FK1_FOR_ORDEREJB_TABLE VARCHAR2(255));

ALTER TABLE PHONEEJB_TABLE add constraint FK_FOR_ADDRESSEJB_TABLE Foreign Key (FK_FOR_ADDRESSEJB_TABLE) references ADDRESSEJB_TABLE(id)ON DELETE CASCADE;

ALTER TABLE ALIASEJB_TABLE add constraint FK1_FOR_CUSTOMEREJB_TABLE Foreign Key (FK1_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE ALIASEJB_TABLE add constraint FK2_FOR_CUSTOMEREJB_TABLE Foreign Key (FK2_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE FKS_FOR_ALIAS_CUSTOMER  add constraint FK_FOR_CUSTOMEREJB_TABLE Foreign Key (FK_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE FKS_FOR_ALIAS_CUSTOMER  add constraint FK_FOR_ALIASEJB_TABLE Foreign Key (FK_FOR_ALIASEJB_TABLE) references ALIASEJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE FKS_ALIASNOOP_CUSTNOOP  add constraint FK2_FOR_ALIASEJB_TABLE Foreign Key (FK2_FOR_ALIASEJB_TABLE) references ALIASEJB_TABLE(id) ON DELETE CASCADE;
ALTER TABLE FKS_ALIASNOOP_CUSTNOOP  add constraint FK8_FOR_CUSTOMEREJB_TABLE Foreign Key (FK8_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE CREDITCARDEJB_TABLE add constraint FK3_FOR_CUSTOMEREJB_TABLE Foreign Key (FK3_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE CREDITCARDEJB_TABLE add constraint FK_FOR_ORDEREJB_TABLE Foreign Key (FK_FOR_ORDEREJB_TABLE) references ORDEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE ORDEREJB_TABLE add constraint FK4_FOR_CUSTOMEREJB_TABLE Foreign Key (FK4_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE ORDEREJB_TABLE add constraint FK0_FOR_LINEITEMEJB_TABLE Foreign Key (FK0_FOR_LINEITEMEJB_TABLE) references LINEITEMEJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE LINEITEMEJB_TABLE add constraint FK_FOR_PRODUCTEJB_TABLE Foreign Key (FK_FOR_PRODUCTEJB_TABLE) references PRODUCTEJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE LINEITEMEJB_TABLE add constraint FK1_FOR_ORDEREJB_TABLE Foreign Key (FK1_FOR_ORDEREJB_TABLE) references ORDEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE ADDRESSEJB_TABLE add constraint FK5_FOR_CUSTOMEREJB_TABLE Foreign Key (FK5_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE ADDRESSEJB_TABLE add constraint FK6_FOR_CUSTOMEREJB_TABLE Foreign Key (FK6_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE SPOUSEEJB_TABLE add constraint FK7_FOR_CUSTOMEREJB_TABLE Foreign Key (FK7_FOR_CUSTOMEREJB_TABLE) references CUSTOMEREJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE SPOUSEEJB_TABLE add constraint FK_FOR_INFOEJB_TABLE Foreign Key (FK_FOR_INFOEJB_TABLE) references INFOEJB_TABLE(id) ON DELETE CASCADE;

ALTER TABLE INFOEJB_TABLE add constraint FK_FOR_SPOUSEEJB_TABLE Foreign Key (FK_FOR_SPOUSEEJB_TABLE) references SPOUSEEJB_TABLE(id) ON DELETE CASCADE;

DROP  table COMPLEXPK_TABLE1 CASCADE;
CREATE table COMPLEXPK_TABLE1(ID INTEGER,BRANDNAME varchar(25),PRICE REAL,PRODUCT BLOB(9072), FK_FOR_LINEITEMS varchar(25), constraint  COMPLEXPK_TABLE1 primary key(ID,BRANDNAME));

DROP  table COMPLEXPK_LINEITEM_TABLE1 CASCADE;
CREATE table COMPLEXPK_LINEITEM_TABLE1(ID varchar(25), QUANTITY INTEGER, FK_FOR_ID INTEGER, FK_FOR_BRANDNAME varchar(25), constraint COMPLEXPK_LINEITEM_TABLE1 primary key(ID));

ALTER TABLE COMPLEXPK_TABLE1 add constraint FK_FOR_LINEITEMS  Foreign Key (FK_FOR_LINEITEMS) references COMPLEXPK_LINEITEM_TABLE1(ID)ON DELETE CASCADE;

ALTER TABLE COMPLEXPK_LINEITEM_TABLE1 add constraint FK_FOR_ID Foreign Key (FK_FOR_ID, FK_FOR_BRANDNAME) references COMPLEXPK_TABLE1(ID,BRANDNAME) ON DELETE CASCADE;

DROP  table CMP20_HOMEM_TABLE1 CASCADE;
CREATE table CMP20_HOMEM_TABLE1 (NAME VARCHAR2(255),STATE VARCHAR2(255),CODE VARCHAR2(255),ZIP INTEGER,ID INTEGER,EXPIRES VARCHAR2(255),STREET VARCHAR2(255),CARDBALANCE double precision,CITY VARCHAR2(255), HOMEPHONE VARCHAR2(255),WORKPHONE  VARCHAR2(255),LASTNAME VARCHAR2(255),PAYMENTTYPE VARCHAR2(255),CREDITCARDNUMBER VARCHAR2(255),ACCOUNTNUMBER VARCHAR2(255),MIDDLENAME VARCHAR2(255),FIRSTNAME VARCHAR2(255),CONSTRAINT CMP20_HOMEM_TABLE1 PRIMARY KEY(ID));

DROP  table CMP20_ENTITY_CTX_TABLE1 CASCADE;
CREATE TABLE CMP20_ENTITY_CTX_TABLE1 (KEY_ID VARCHAR(255), BRAND_NAME VARCHAR(255) , CONSTRAINT CMP20_ENTITY_CTX_TABLE1 primary key (KEY_ID) ); 

DROP  table UNKNOWNPK_TABLE1 cascade;
CREATE table UNKNOWNPK_TABLE1(MIDDLENAME VARCHAR2(255),LASTNAME VARCHAR2(255),FIRSTNAME VARCHAR2(255),STREET VARCHAR(255),CITY VARCHAR(255),STATE VARCHAR2(255),ZIP VARCHAR(255),ACCOUNTNUMBER VARCHAR(255),ABC NUMBER(29) NOT NULL,constraint UNKNOWNPK_TABLE1 primary key(ABC));

DROP  table CMP_COMPLEXPK_TABLE1 CASCADE;
CREATE table CMP_COMPLEXPK_TABLE1 (ID  INTEGER,BRAND_NAME  varchar(25),PRICE REAL,PRODUCT  blob(9092),constraint CMP_COMPLEXPK_TABLE1 primary key(ID,BRAND_NAME));

DROP  table LRAPITEST_TABLE1 CASCADE;
CREATE table LRAPITEST_TABLE1 (id  INTEGER,name  varchar(25),value  REAL, constraint LRAPITEST_TABLE1  primary key(id));

DROP  table LRAPITEST_TABLE2 CASCADE;
CREATE table LRAPITEST_TABLE2 (id  INTEGER,name  varchar(25),value  REAL, constraint LRAPITEST_TABLE2  primary key(id));

DROP  table LRAPITEST_TABLE3 CASCADE;
CREATE table LRAPITEST_TABLE3 (id  INTEGER,name  varchar(25),value  REAL, constraint LRAPITEST_TABLE3  primary key(id));

DROP  table COMPAT_ENTITYCMP_TABLE1 CASCADE;
CREATE table COMPAT_ENTITYCMP_TABLE1 (KEY_ID  INTEGER,BRAND_NAME  varchar(25),PRICE  REAL, constraint COMPAT_ENTITYCMP_TABLE1 primary key(KEY_ID));

DROP  table CMP20_ENTITYCMP_TABLE1 CASCADE;
CREATE table CMP20_ENTITYCMP_TABLE1 (KEY_ID  INTEGER,BRAND_NAME  varchar(25),PRICE  REAL, B BLOB(9072), constraint CMP20_ENTITYCMP_TABLE1 primary key(KEY_ID));

DROP table TX_NOT_TABLE3 CASCADE;
CREATE table TX_NOT_TABLE3 (KEY_ID  INTEGER,BRAND_NAME  varchar(25),PRICE  REAL, constraint TX_NOT_TABLE3 primary key(KEY_ID));

DROP table TX_NOT_TABLE4 CASCADE;
CREATE table TX_NOT_TABLE4 (KEY_ID  INTEGER,BRAND_NAME  varchar(25),PRICE  REAL, constraint TX_NOT_TABLE4 primary key(KEY_ID));

DROP  table CMP11PK_TABLE1 CASCADE;
CREATE table CMP11PK_TABLE1 (cmpID varchar2(25),cmpBrandName  varchar(25),  cmpPrice FLOAT(63),   constraint CMP11PK_TABLE1 primary key(cmpID));

DROP table CMP11PK_TABLE2 CASCADE;
CREATE table CMP11PK_TABLE2 (cmpID INTEGER,cmpBrandName  varchar(25),  cmpPrice FLOAT(63),   constraint CMP11PK_TABLE2 primary key(cmpID));

DROP table CMP11PK_TABLE3 CASCADE;
CREATE table CMP11PK_TABLE3 (cmpID FLOAT(63),cmpBrandName  varchar(25),  cmpPrice FLOAT(63),   constraint CMP11PK_TABLE3 primary key(cmpID));

DROP table CMP11PK_TABLE4 CASCADE;
CREATE table CMP11PK_TABLE4( pmIDInteger INTEGER, pmIDString  varchar(25), pmIDFloat  FLOAT(63), CMPBRANDNAME VARCHAR2(25), CMPPRICE FLOAT(63),constraint CMP11PK_TABLE4  primary key(pmIDInteger,pmIDString,pmIDFloat));

DROP  table CMP20_DEP_PKEY_TABLE1 CASCADE;
CREATE table CMP20_DEP_PKEY_TABLE1(KEY_ID  number,BRAND_NAME  varchar(25),PRICE  float, constraint CMP20_DEP_PKEY_TABLE1 primary key(KEY_ID));

DROP table CMP20_DEP_PKEY_TABLE2 CASCADE;
CREATE table CMP20_DEP_PKEY_TABLE2(KEY_ID  FLOAT,BRAND_NAME  varchar(25),PRICE  float, constraint CMP20_DEP_PKEY_TABLE2 primary key(KEY_ID));

DROP table CMP20_DEP_PKEY_TABLE3 CASCADE;
CREATE table CMP20_DEP_PKEY_TABLE3(KEY_ID varchar(50),BRAND_NAME  varchar(25),PRICE  float, constraint CMP20_DEP_PKEY_TABLE3 primary key(KEY_ID));

DROP table CMP20_DEP_PKEY_TABLE4 CASCADE;
CREATE table CMP20_DEP_PKEY_TABLE4( pmIDInteger INTEGER, pmIDString  varchar(25), pmIDFloat  FLOAT(63), BRANDNAME VARCHAR2(25), PRICE FLOAT(63), constraint CMP20_DEP_PKEY_TABLE4 primary key(pmIDInteger,pmIDString,pmIDFloat));

DROP  table CMP20_LSECP_TABLE1 CASCADE;
CREATE table CMP20_LSECP_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_LSECP_TABLE1 primary key(ID));

DROP table CMP20_LSECP_TABLE2 CASCADE;
CREATE table CMP20_LSECP_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_LSECP_TABLE2 primary key(ID));

DROP table CMP20_LSECP_TABLE3 CASCADE;
CREATE table CMP20_LSECP_TABLE3  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_LSECP_TABLE3 primary key(ID));

DROP  table CMP20_LSECR_TABLE1 CASCADE;
CREATE table CMP20_LSECR_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_LSECR_TABLE1 primary key(ID));

DROP table CMP20_LSECR_TABLE2 CASCADE;
CREATE table CMP20_LSECR_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_LSECR_TABLE2 primary key(ID));

DROP table CMP20_LSECR_TABLE3 CASCADE;
CREATE table CMP20_LSECR_TABLE3  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_LSECR_TABLE3 primary key(ID));

DROP  table CMP20_SEC_TABLE1 CASCADE;
CREATE table CMP20_SEC_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SEC_TABLE1 primary key(ID));

DROP table CMP20_SEC_TABLE2 CASCADE;
CREATE table CMP20_SEC_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SEC_TABLE2 primary key(ID));

DROP  table CMP20_SECP_TABLE1 CASCADE;
CREATE table CMP20_SECP_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SECP_TABLE1 primary key(ID));

DROP table CMP20_SECP_TABLE2 CASCADE;
CREATE table CMP20_SECP_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SECP_TABLE2 primary key(ID));

DROP table CMP20_SECP_TABLE3 CASCADE;
CREATE table CMP20_SECP_TABLE3  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SECP_TABLE3 primary key(ID));

DROP  table CMP_SEC_TABLE1 CASCADE;
CREATE table CMP_SEC_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SEC_TABLE1 primary key(ID));

DROP table CMP_SEC_TABLE2 CASCADE;
CREATE table CMP_SEC_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SEC_TABLE2 primary key(ID));

DROP  table CMP_SECP_TABLE1 CASCADE;
CREATE table CMP_SECP_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SECP_TABLE1 primary key(ID));

DROP table CMP_SECP_TABLE2 CASCADE;
CREATE table CMP_SECP_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SECP_TABLE2 primary key(ID));

DROP table CMP_SECP_TABLE3 CASCADE;
CREATE table CMP_SECP_TABLE3  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SECP_TABLE3 primary key(ID));

DROP  table CMP20_SECRASP_TABLE1 CASCADE;
CREATE table CMP20_SECRASP_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SECRASP_TABLE1 primary key(ID));

DROP table CMP20_SECRASP_TABLE2 CASCADE;
CREATE table CMP20_SECRASP_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SECRASP_TABLE2 primary key(ID));

DROP table CMP20_SECRASP_TABLE3 CASCADE;
CREATE table CMP20_SECRASP_TABLE3  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP20_SECRASP_TABLE3 primary key(ID));

DROP  table CMP_SECRASP_TABLE1 CASCADE;
CREATE table CMP_SECRASP_TABLE1 (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SECRASP_TABLE1 primary key(ID));

DROP table CMP_SECRASP_TABLE2 CASCADE;
CREATE table CMP_SECRASP_TABLE2  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SECRASP_TABLE2 primary key(ID));

DROP table CMP_SECRASP_TABLE3 CASCADE;
CREATE table CMP_SECRASP_TABLE3  (ID INTEGER,Brand_Name  varchar(25),  Price FLOAT(63),   constraint CMP_SECRASP_TABLE3 primary key(ID));

DROP  table TABLER5_DELETE cascade;
CREATE TABLE TABLER5_DELETE(id VARCHAR(255)  PRIMARY KEY,name VARCHAR(255) ,value NUMBER NOT NULL,     FK1_FOR_TABLER6_DELETE_ID VARCHAR(255),     FK2_FOR_TABLER6_DELETE_ID VARCHAR(255),     FK1_FOR_TABLER7_DELETE_ID VARCHAR(255),     FK2_FOR_TABLER7_DELETE_ID VARCHAR(255));

DROP table TABLER6_DELETE cascade;
CREATE TABLE  TABLER6_DELETE( id VARCHAR(255)  PRIMARY KEY, name VARCHAR(255) ,     value NUMBER NOT NULL);

DROP table TABLER7_DELETE cascade;
CREATE TABLE TABLER7_DELETE ( id  VARCHAR(255) PRIMARY KEY,     name VARCHAR(255),     value NUMBER NOT NULL,     FK_FOR_TABLER6_DELETE_ID VARCHAR(255));
ALTER TABLE TABLER5_DELETE add constraint FK1_FOR_TABLER6_DELETE_ID Foreign Key (FK1_FOR_TABLER6_DELETE_ID) references TABLER6_DELETE(id);

ALTER TABLE TABLER5_DELETE add constraint FK2_FOR_TABLER6_DELETE_ID Foreign Key (FK2_FOR_TABLER6_DELETE_ID) references TABLER6_DELETE(id);

ALTER TABLE TABLER5_DELETE add constraint FK1_FOR_TABLER7_DELETE_ID Foreign Key (FK1_FOR_TABLER7_DELETE_ID) references TABLER7_DELETE(id);

ALTER TABLE TABLER7_DELETE add constraint FK_FOR_TABLER6_DELETE_ID Foreign Key (FK_FOR_TABLER6_DELETE_ID) references TABLER6_DELETE(id);

ALTER TABLE TABLER5_DELETE add constraint FK2_FOR_TABLER7_DELETE_ID Foreign Key (FK2_FOR_TABLER7_DELETE_ID) references TABLER7_DELETE(id);

DROP table TIMER_TABLE1 CASCADE;
CREATE TABLE TIMER_TABLE1 (KEY_ID INTEGER NOT NULL,  BRAND_NAME VARCHAR(255), PRICE REAL NOT NULL, CONSTRAINT TIMER_TABLE1 PRIMARY KEY (KEY_ID) );

DROP table TIMER_FLAGSTORE CASCADE;
CREATE TABLE TIMER_FLAGSTORE (KEY_ID INTEGER NOT NULL, BRAND_NAME VARCHAR(255) , PRICE REAL NOT NULL , REQUIRESNEWACCESSED BOOLEAN NOT NULL, REQUIREDACCESSED BOOLEAN NOT NULL , CONSTRAINT TIMER_FLAGSTORE PRIMARY KEY (KEY_ID) );

DROP table CMP20_JACC_TABLE1 CASCADE;
CREATE TABLE CMP20_JACC_TABLE1 (ARG1 VARCHAR(255) NOT NULL, ARG2 INTEGER NOT NULL,  ARG3 BIGINT NOT NULL, CONSTRAINT CMP20_JACC_TABLE1 PRIMARY KEY (ARG1,ARG2,ARG3) );


DROP TABLE EMPLOYEEEJB CASCADE;
CREATE TABLE EMPLOYEEEJB (HIREDATE TIMESTAMP NULL, ID INTEGER NOT NULL, FIRSTNAME VARCHAR(256) NULL, SALARY REAL NOT NULL, LASTNAME VARCHAR(256) NULL, EMPLOYEEEJB_ID INTEGER NULL, DEPARTMENTEJB_ID INTEGER NULL, CONSTRAINT PK_EMPLOYEEEJB PRIMARY KEY (ID));

DROP TABLE DEPARTMENTEJB CASCADE;
CREATE TABLE DEPARTMENTEJB (NAME VARCHAR(256) NULL, ID INTEGER NOT NULL, CONSTRAINT PK_DEPARTMENTEJB PRIMARY KEY (ID));

ALTER TABLE EMPLOYEEEJB ADD CONSTRAINT FK_MANAGER FOREIGN KEY (EMPLOYEEEJB_ID) REFERENCES EMPLOYEEEJB (ID);
ALTER TABLE EMPLOYEEEJB ADD CONSTRAINT FK_DEPARTMENT FOREIGN KEY (DEPARTMENTEJB_ID) REFERENCES DEPARTMENTEJB (ID);

