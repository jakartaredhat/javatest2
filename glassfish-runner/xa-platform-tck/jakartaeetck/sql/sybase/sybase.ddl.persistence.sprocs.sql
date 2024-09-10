DROP PROCEDURE GetEmpOneFirstNameFromOut !
CREATE PROCEDURE GetEmpOneFirstNameFromOut (@OUT_PARAM VARCHAR(255) out) AS BEGIN SELECT @OUT_PARAM=FIRSTNAME FROM EMPLOYEE WHERE ID=1 END !

DROP PROCEDURE GetEmpFirstNameFromOut !
CREATE PROCEDURE GetEmpFirstNameFromOut (@IN_PARAM INTEGER, @OUT_PARAM VARCHAR(255) out) AS BEGIN SELECT @OUT_PARAM=FIRSTNAME  FROM EMPLOYEE WHERE ID=@IN_PARAM END !

DROP PROCEDURE GetEmpLastNameFromInOut !
CREATE PROCEDURE GetEmpLastNameFromInOut (@INOUT_PARAM VARCHAR(255) out) AS BEGIN SELECT @INOUT_PARAM=LASTNAME FROM EMPLOYEE WHERE ID=convert(int,@INOUT_PARAM) END !

DROP PROCEDURE GetEmpASCFromRS !
CREATE PROCEDURE GetEmpASCFromRS AS BEGIN SELECT ID, FIRSTNAME, LASTNAME, HIREDATE, SALARY FROM EMPLOYEE ORDER BY ID ASC END !

DROP PROCEDURE GetEmpIdFNameLNameFromRS !
CREATE PROCEDURE GetEmpIdFNameLNameFromRS(@IN_PARAM INTEGER) AS BEGIN SELECT ID, FIRSTNAME, LASTNAME FROM EMPLOYEE WHERE ID=@IN_PARAM END !

DROP PROCEDURE GetEmpIdUsingHireDateFromOut !
CREATE PROCEDURE GetEmpIdUsingHireDateFromOut (@IN_PARAM DATE, @OUT_PARAM INTEGER OUT) AS BEGIN SELECT @OUT_PARAM=ID FROM EMPLOYEE WHERE HIREDATE=@IN_PARAM END !

DROP PROCEDURE UpdateEmpSalaryColumn !
CREATE PROCEDURE UpdateEmpSalaryColumn AS BEGIN UPDATE EMPLOYEE set SALARY=0.00 END !

DROP PROCEDURE DeleteAllEmp !
CREATE PROCEDURE DeleteAllEmp AS BEGIN DELETE FROM EMPLOYEE END !

grant execute on GetEmpOneFirstNameFromOut  to public !
grant execute on GetEmpFirstNameFromOut  to public !
grant execute on GetEmpLastNameFromInOut  to public !
grant execute on GetEmpASCFromRS  to public !
grant execute on GetEmpIdFNameLNameFromRS  to public !
grant execute on GetEmpIdUsingHireDateFromOut  to public !
grant execute on UpdateEmpSalaryColumn  to public !
grant execute on DeleteAllEmp  to public !

exec sp_procxmode GetEmpOneFirstNameFromOut, anymode !
exec sp_procxmode GetEmpFirstNameFromOut, anymode !
exec sp_procxmode GetEmpLastNameFromInOut, anymode !
exec sp_procxmode GetEmpASCFromRS, anymode !
exec sp_procxmode GetEmpIdFNameLNameFromRS, anymode !
exec sp_procxmode GetEmpIdUsingHireDateFromOut, anymode !
exec sp_procxmode UpdateEmpSalaryColumn, anymode !
exec sp_procxmode DeleteAllEmp, anymode !

