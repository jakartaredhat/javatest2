/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * @(#)callStmtClient4.java	1.19 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.callStmt.callStmt4;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sun.ts.lib.util.TSNamingContextInterface;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jdbc.ee.callStmt.callStmt3.callStmtClient3;
import com.sun.ts.tests.jdbc.ee.common.DataSourceConnection;
import com.sun.ts.tests.jdbc.ee.common.DriverManagerConnection;
import com.sun.ts.tests.jdbc.ee.common.JDBCTestMsg;
import com.sun.ts.tests.jdbc.ee.common.rsSchema;
import com.sun.ts.tests.jdbc.ee.prepStmt.prepStmt9.prepStmtClient9;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;
/**
 * The callStmtClient4 class tests methods of DatabaseMetaData interface using
 * Sun's J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class callStmtClient4 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "callStmtClient4_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(callStmtClient4.class);
		archive.addAsWebInfResource(callStmtClient4.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.callStmt.callStmt4";

	private static final Logger logger = (Logger) System.getLogger(callStmtClient4.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements
	private transient Connection conn = null;

	private rsSchema rsSch = null;

	private JDBCTestMsg msg = null;

	private String drManager = null;

	private Properties sqlp = null;

	private transient DatabaseMetaData dbmd = null;

	private CallableStatement cstmt = null;

	private Properties props = null;

	/* Test setup: */
	/*
	 * @class.setup_props: Driver, the Driver name; db1, the database name with url;
	 * user1, the database user name; password1, the database password; db2, the
	 * database name with url; user2, the database user name; password2, the
	 * database password; DriverManager, flag for DriverManager; ptable, the primary
	 * table; ftable, the foreign table; cofSize, the initial size of the ptable;
	 * cofTypeSize, the initial size of the ftable; binarySize, size of binary data
	 * type; varbinarySize, size of varbinary data type; longvarbinarySize, size of
	 * longvarbinary data type;
	 * 
	 * @class.testArgs: -ap tssql.stmt
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {
			try {
				drManager = System.getProperty("DriverManager", "");
				if (drManager.length() == 0)
					throw new Exception("Invalid DriverManager Name");

				if (drManager.equals("yes")) {
					logger.log(Logger.Level.TRACE, "Using DriverManager");
					DriverManagerConnection dmCon = new DriverManagerConnection();
					conn = dmCon.getConnection();
				} else {
					logger.log(Logger.Level.TRACE, "Using DataSource");
					DataSourceConnection dsCon = new DataSourceConnection();
					conn = dsCon.getConnection();
				}
				rsSch = new rsSchema();
				msg = new JDBCTestMsg();
			} catch (SQLException ex) {
				logger.log(Logger.Level.ERROR, "SQL Exception : " + ex.getMessage(), ex);
			}
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Setup Failed!");
			TestUtil.printStackTrace(e);
		}
	}

	/*
	 * @testName: testGetObject21
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve a Time value from
	 * Time_Tab.Extract the same Time value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex).Both the
	 * values should be equal.
	 */
	public void testGetObject21() throws Exception {
		Time oRetVal = null;
		Time nonNullTimeVal = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Time_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIME);
			cstmt.registerOutParameter(2, java.sql.Types.TIME);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			oRetVal = (Time) cstmt.getObject(1);
			String sRetStr = rsSch.extractVal("Time_Tab", 1, sqlp, conn);
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			nonNullTimeVal = Time.valueOf(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(nonNullTimeVal))
				msg.setMsg("getObject returns the Break Time for type Time ");
			else {
				msg.printTestError("getObject did not return the proper Break Time for type Time",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Time_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject22
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Time_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject22() throws Exception {
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Time_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIME);
			cstmt.registerOutParameter(2, java.sql.Types.TIME);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("Calling CallableStatement.getObject(Time.NullValue)");

			msg.setMsg("invoke getObject method");
			Time oRetVal = (Time) cstmt.getObject(2);

			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the null value for type Time ");
			else {
				msg.printTestError("getObject did not return the null value for type Time", "test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Time_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject23
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve a Timestamp value from
	 * Timestamp_Tab.Extract the same Timestamp value from the tssql.stmt
	 * file.Compare this value with the value returned by the getObject(int
	 * parameterIndex).Both the values should be equal.
	 */
	@Test
	public void testGetObject23() throws Exception {
		Timestamp oRetVal = null;
		Timestamp nonNullTimestampVal = null;
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Timestamp_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIMESTAMP);
			cstmt.registerOutParameter(2, java.sql.Types.TIMESTAMP);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			oRetVal = (Timestamp) cstmt.getObject(1);
			String sRetStr = rsSch.extractVal("Timestamp_Tab", 1, sqlp, conn);
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			nonNullTimestampVal = Timestamp.valueOf(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(nonNullTimestampVal))
				msg.setMsg("getObject returns the In Time for type Timestamp " + oRetVal);
			else {
				msg.printTestError("getObject did not return the proper In Time for type Timestamp",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Timestamp_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject24
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Timestamp_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject24() throws Exception {
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Timestamp_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIMESTAMP);
			cstmt.registerOutParameter(2, java.sql.Types.TIMESTAMP);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("Calling CallableStatement.getObject(Timestamp.NullValue)");

			msg.setMsg("invoke getObject method");
			Timestamp oRetVal = (Timestamp) cstmt.getObject(2);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the null value for type Timestamp ");
			else {
				msg.printTestError("getObject did not return the null value for type Timestamp",
						"test getObject failed");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Timestamp_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject25
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve a Date value from from
	 * Date_Tab.Extract the Date char value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex).Both the
	 * values should be equal.
	 */
	@Test
	public void testGetObject25() throws Exception {
		java.sql.Date oRetVal = null;
		try {
			rsSch.createTab("Date_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Date_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DATE);
			cstmt.registerOutParameter(2, java.sql.Types.DATE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			oRetVal = (java.sql.Date) cstmt.getObject(1);
			String sRetStr = rsSch.extractVal("Date_Tab", 1, sqlp, conn);
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			sRetStr = sRetStr.trim();
			java.sql.Date oExtVal = java.sql.Date.valueOf(sRetStr);

			msg.addOutputMsg(oExtVal.toString(), oRetVal.toString());

			if (oRetVal.toString().equals(oExtVal.toString()))
				msg.setMsg("getObject returns the proper Date for type Date ");
			else {
				msg.printTestError("getObject did not return the proper Date for type Date", "test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Date_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject26
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Date_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject26() throws Exception {
		try {
			rsSch.createTab("Date_Tab", sqlp, conn);
			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Date_Proc(?,?)}");
			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DATE);
			cstmt.registerOutParameter(2, java.sql.Types.DATE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			java.sql.Date oRetVal = (java.sql.Date) cstmt.getObject(2);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the null value for type Date ");
			else {
				msg.printTestError("getObject did not return the null value for type Date", "test getObject Failed!");
			}

			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Date_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject27
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the maximum value from
	 * Tinyint_Tab. Extract the maximum value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex) Both the
	 * values should be equal.
	 */
	@Test
	public void testGetObject27() throws Exception {
		try {
			rsSch.createTab("Tinyint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Tinyint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TINYINT);
			cstmt.registerOutParameter(2, java.sql.Types.TINYINT);
			cstmt.registerOutParameter(3, java.sql.Types.TINYINT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Object obj = cstmt.getObject(1);
			Byte oRetVal = new Byte(obj.toString());

			String sRetStr = rsSch.extractVal("Tinyint_Tab", 1, sqlp, conn);
			Byte oExtVal = new Byte(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if ((oRetVal.compareTo(oExtVal)) == 0)
				msg.setMsg("getObject returns the Maximum Value for type TINYINT ");
			else {
				msg.printTestError("getObject did not return the Maximum Value for type TINYINT ",
						"test getObject failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Tinyint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject28
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the minimum value from
	 * Tinyint_Tab. Extract the minimum value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex) Both the
	 * values should be equal.
	 */
	@Test
	public void testGetObject28() throws Exception {
		try {
			rsSch.createTab("Tinyint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Tinyint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TINYINT);
			cstmt.registerOutParameter(2, java.sql.Types.TINYINT);
			cstmt.registerOutParameter(3, java.sql.Types.TINYINT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Object obj = cstmt.getObject(2);
			Byte oRetVal = new Byte(obj.toString());

			String sRetStr = rsSch.extractVal("Tinyint_Tab", 2, sqlp, conn);
			Byte oExtVal = new Byte(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if ((oRetVal.compareTo(oExtVal)) == 0)
				msg.setMsg("getObject returns the Minimum Value for type TINYINT " + oRetVal);
			else {
				msg.printTestError("getObject did not return the Minimum Value for type TINYINT ",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Tinyint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject29
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Tinyint_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject29() throws Exception {
		try {
			rsSch.createTab("Tinyint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Tinyint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TINYINT);
			cstmt.registerOutParameter(2, java.sql.Types.TINYINT);
			cstmt.registerOutParameter(3, java.sql.Types.TINYINT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Object oRetVal = cstmt.getObject(3);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the Null Value for type Byte(JDBC TINYINT) ");
			else {
				msg.printTestError("getObject did not return the Null Value for type Byte(JDBC TINYINT)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Tinyint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject30
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the maximum value from
	 * Double_Tab. Extract the maximum value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex) Both the
	 * values should be equal.
	 */
	@Test
	public void testGetObject30() throws Exception {
		try {
			rsSch.createTab("Double_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Double_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(3, java.sql.Types.DOUBLE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Double oRetVal = (Double) cstmt.getObject(1);
			String sRetStr = rsSch.extractVal("Double_Tab", 1, sqlp, conn);
			Double oExtVal = new Double(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(oExtVal))
				msg.setMsg("getObject returns the Maximum Value for type Double(JDBC DOUBLE) ");
			else {
				msg.printTestError("getObject did not return the Maximum Value for type Double(JDBC DOUBLE)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Double_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject31
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the minimum value of the
	 * parameter from Double_Tab. Extract the minimum value from the tssql.stmt
	 * file.Compare this value with the value returned by the getObject(int
	 * parameterIndex) Both the values should be equal.
	 */
	@Test
	public void testGetObject31() throws Exception {
		try {
			rsSch.createTab("Double_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Double_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(3, java.sql.Types.DOUBLE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("Calling CallableStatement.getObject(Double.MinimumValue(JDBC DOUBLE))");

			msg.setMsg("invoke getObject method");
			Double oRetVal = (Double) cstmt.getObject(2);
			String sRetStr = rsSch.extractVal("Double_Tab", 2, sqlp, conn);
			Double oExtVal = new Double(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(oExtVal))
				msg.setMsg("getObject returns the Minimum Value for type Double(JDBC DOUBLE) ");
			else {
				msg.printTestError("getObject did not return the Minimum Value for type Double(JDBC DOUBLE)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Double_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject32
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Double_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject32() throws Exception {
		try {
			rsSch.createTab("Double_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Double_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(3, java.sql.Types.DOUBLE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Double oRetVal = (Double) cstmt.getObject(3);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the Null Value for type Double(JDBC DOUBLE) ");
			else {
				msg.printTestError("getObject did not return the Null Value for type Double(JDBC DOUBLE)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Double_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject33
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the maximum value from
	 * Real_Tab. Extract the maximum value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex) Both the
	 * values should be equal.
	 */
	@Test
	public void testGetObject33() throws Exception {
		try {
			rsSch.createTab("Real_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Real_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.REAL);
			cstmt.registerOutParameter(2, java.sql.Types.REAL);
			cstmt.registerOutParameter(3, java.sql.Types.REAL);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("Calling CallableStatement.getObject(Float.MaximumValue)");

			msg.setMsg("invoke getObject method");
			Float oRetVal = (Float) cstmt.getObject(1);
			String sRetStr = rsSch.extractVal("Real_Tab", 1, sqlp, conn);
			Float oExtVal = new Float(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(oExtVal))
				msg.setMsg("getObject returns the Maximum Value for type Float ");
			else {
				msg.printTestError("getObject did not return the Maximum Value for type Float",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Real_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject34
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the minimum value from
	 * Real_Tab. Extract the minimum value from the tssql.stmt file.Compare this
	 * value with the value returned by the getObject(int parameterIndex) Both the
	 * values should be equal.
	 */
	@Test
	public void testGetObject34() throws Exception {
		try {
			rsSch.createTab("Real_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Real_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.REAL);
			cstmt.registerOutParameter(2, java.sql.Types.REAL);
			cstmt.registerOutParameter(3, java.sql.Types.REAL);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Float oRetVal = (Float) cstmt.getObject(2);
			String sRetStr = rsSch.extractVal("Real_Tab", 2, sqlp, conn);
			Float oExtVal = new Float(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(oExtVal))
				msg.setMsg("getObject returns the Minimum Value for type Float ");
			else {
				msg.printTestError("getObject did not return the Minimum Value for type Float",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Real_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject35
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Real_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject35() throws Exception {
		try {
			rsSch.createTab("Real_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Real_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.REAL);
			cstmt.registerOutParameter(2, java.sql.Types.REAL);
			cstmt.registerOutParameter(3, java.sql.Types.REAL);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			Float oRetVal = (Float) cstmt.getObject(3);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the Null Value for type Float ");
			else {
				msg.printTestError("getObject did not return the Null Value for type Float", "test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Real_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject36
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve a Varchar value from
	 * Varchar_Tab.Extract the same Varchar value from the tssql.stmt file.Compare
	 * this value with the value returned by the getObject(int parameterIndex).Both
	 * the values should be equal.
	 */
	@Test
	public void testGetObject36() throws Exception {
		try {
			rsSch.createTab("Varchar_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Varchar_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			String oRetVal = (String) cstmt.getObject(1);
			String oExtVal = rsSch.extractVal("Varchar_Tab", 1, sqlp, conn);
			oExtVal = oExtVal.trim();
			oRetVal = oRetVal.trim();

			msg.addOutputMsg(oExtVal, oRetVal);

			if (oRetVal.equals(oExtVal.substring(1, oExtVal.length() - 1)))
				msg.setMsg("getObject returns the Name for type String(JDBC VARCHAR) ");
			else {
				msg.printTestError("getObject did not return the Name for type String(JDBC VARCHAR)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject37
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Varchar_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject37() throws Exception {
		try {
			rsSch.createTab("Varchar_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Varchar_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			String oRetVal = (String) cstmt.getObject(2);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the null value for type String(JDBC VARCHAR) ");
			else {
				msg.printTestError("getObject did not return the null value for type String(JDBC VARCHAR)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject38
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve a Longvarchar value from
	 * Longvarchar_Tab.Extract the same Longvarchar value from the tssql.stmt
	 * file.Compare this value with the value returned by the getObject(int
	 * parameterIndex).Both the values should be equal.
	 */
	@Test
	public void testGetObject38() throws Exception {
		try {
			rsSch.createTab("Longvarchar_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Longvarchar_Proc(?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.LONGVARCHAR);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			String oRetVal = (String) cstmt.getObject(1);
			String oExtVal = rsSch.extractVal("Longvarchar_Tab", 1, sqlp, conn);
			oExtVal = oExtVal.trim();
			oRetVal = oRetVal.trim();
			msg.addOutputMsg(oExtVal, oRetVal);

			if (oRetVal.equals(oExtVal.substring(1, oExtVal.length() - 1)))
				msg.setMsg("getObject returns the Name for type String(JDBC LONGVARCHAR) ");
			else {
				msg.printTestError("getObject did not return the Name for type String(JDBC LONGVARCHAR)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Longvarchar_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject39
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType) method. Execute the stored procedure and call the
	 * getObject(int parameterIndex) method to retrieve the null value from
	 * Longvarchar_Tab.Check if it returns null
	 */
	@Test
	public void testGetObject39() throws Exception {
		try {
			rsSch.createTab("Longvarcharnull_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Lvarcharnull_Proc(?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.LONGVARCHAR);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("Calling CallableStatement.getObject(String.NullValue(JDBC LONGVARCHAR))");

			msg.setMsg("invoke getObject method");
			String oRetVal = (String) cstmt.getObject(1);
			msg.addOutputMsg("null", oRetVal);

			if (oRetVal == null)
				msg.setMsg("getObject returns the null value for type String(JDBC LONGVARCHAR) ");
			else {
				msg.printTestError("getObject did not return the null value for type String(JDBC LONGVARCHAR)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Longvarcharnull_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetObject40
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1267;
	 * JDBC:JAVADOC:1268; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Register the parameter using registerOutParameter(int
	 * parameterIndex,int sqlType,int scale) method. Execute the stored procedure
	 * and call the getObject(int parameterIndex) method to retrieve the maximum
	 * value from Decimal_Tab. Extract the maximum value from the tssql.stmt
	 * file.Compare this value with the value returned by the getObject(int
	 * parameterIndex) Both the values should be equal.
	 */
	@Test
	public void testGetObject40() throws Exception {
		try {
			rsSch.createTab("Decimal_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Decimal_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DECIMAL, 15);
			cstmt.registerOutParameter(2, java.sql.Types.DECIMAL, 15);
			cstmt.registerOutParameter(3, java.sql.Types.DECIMAL, 15);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getObject method");
			BigDecimal oRetVal = (BigDecimal) cstmt.getObject(1);
			String sRetStr = rsSch.extractVal("Decimal_Tab", 1, sqlp, conn);
			BigDecimal oExtVal = new BigDecimal(sRetStr);
			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if ((oRetVal.compareTo(oExtVal) == 0))
				msg.setMsg("getObject returns the Maximum value for type BigDecimal(JDBC DECIMAL) ");
			else {
				msg.printTestError("getObject did not return the Maximum value for type BigDecimal(JDBC DECIMAL)",
						"test getObject Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getObject is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getObject Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Decimal_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			// Close the database
			rsSch.dbUnConnect(conn);
			logger.log(Logger.Level.INFO, "Cleanup ok;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "An error occurred while closing the database connection", e);
		}
	}
}
