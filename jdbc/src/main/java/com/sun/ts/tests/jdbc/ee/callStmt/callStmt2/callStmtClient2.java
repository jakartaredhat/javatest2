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
 * @(#)callStmtClient2.java	1.16 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.callStmt.callStmt2;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import javax.sql.DataSource;

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
// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;
import com.sun.ts.tests.jdbc.ee.common.DataSourceConnection;
import com.sun.ts.tests.jdbc.ee.common.DriverManagerConnection;
import com.sun.ts.tests.jdbc.ee.common.JDBCTestMsg;
import com.sun.ts.tests.jdbc.ee.common.rsSchema;

/**
 * The callStmtClient2 class tests methods of DatabaseMetaData interface using
 * Sun's J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class callStmtClient2 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "callStmtClient2_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(callStmtClient2.class);
		archive.addAsWebInfResource(callStmtClient2.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.callStmt.callStmt2";

	private static final Logger logger = (Logger) System.getLogger(callStmtClient2.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements
	private transient Connection conn = null;

	private DataSource ds1 = null;

	private rsSchema rsSch = null;

	private JDBCTestMsg msg = null;

	private String drManager = null;

	private Properties sqlp = null;

	private CallableStatement cstmt = null;

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
	 * @testName: testGetTime01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1263;
	 * JDBC:JAVADOC:1264; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTime(int
	 * parameterIndex) method to retrieve a Time value from Time_Tab. Extract the
	 * the same Time value from the tssql.stmt file.Compare this value with the
	 * value returned by the getTime(int parameterIndex).Both the values should be
	 * equal.
	 */
	@Test
	public void testGetTime01() throws Exception {
		Time oRetVal = null;
		Time maxTimeVal = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Time_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIME);
			cstmt.registerOutParameter(2, java.sql.Types.TIME);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getTime method");
			oRetVal = cstmt.getTime(1);
			String sRetStr = rsSch.extractVal("Time_Tab", 1, sqlp, conn);
			msg.setMsg("extracted Time value from Time_Tab");
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));

			maxTimeVal = Time.valueOf(sRetStr);

			msg.addOutputMsg("" + maxTimeVal, "" + oRetVal);
			if (oRetVal.equals(maxTimeVal))
				msg.setMsg("getTime returns the Break Time");
			else {
				msg.printTestError("getTime() did not return the proper Break Time", "Call to getTime Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime Failed!");

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
	 * @testName: testGetTime02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1263;
	 * JDBC:JAVADOC:1264; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTime(int
	 * parameterIndex) method to retrieve the null value from Time_Tab.Check if it
	 * returns null.
	 */
	@Test
	public void testGetTime02() throws Exception {
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Time_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIME);
			cstmt.registerOutParameter(2, java.sql.Types.TIME);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getTime method");
			Time oRetVal = cstmt.getTime(2);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getTime returns the null value");
			else {
				msg.printTestError("getTime() did not return the null value", "Call to getTime Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime Failed!");

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
	 * @testName: testGetTime03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1283;
	 * JDBC:JAVADOC:1284; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTime(int
	 * parameterIndex,Calander cal) method to retrieve a Time value from Time_tab.
	 * Extract the same Time value from the tssql.stmt file.Compare this value with
	 * the value returned by the getTime(int parameterIndex,Calander cal). Both the
	 * values should be equal.
	 */
	@Test
	public void testGetTime03() throws Exception {
		Time oRetVal = null;
		Time maxTimeVal = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Time_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIME);
			cstmt.registerOutParameter(2, java.sql.Types.TIME);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			Calendar oCalDefault = Calendar.getInstance();

			msg.setMsg("invoke getTime method");
			oRetVal = cstmt.getTime(1, oCalDefault);
			String sRetStr = rsSch.extractVal("Time_Tab", 1, sqlp, conn);
			msg.setMsg("extracted Time value from Time_Tab");
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			maxTimeVal = Time.valueOf(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(maxTimeVal))
				msg.setMsg("getTime returns the Break Time" + oRetVal.toString());
			else {
				msg.printTestError("getTime() did not return the proper Break Time", "Call to getTime Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime Failed!");

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
	 * @testName: testGetTime04
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1283;
	 * JDBC:JAVADOC:1284; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTime(int
	 * parameterIndex,Calender cal) method to retrieve the null value from Time_Tab.
	 * Check if it returns null.
	 */
	@Test
	public void testGetTime04() throws Exception {
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Time_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIME);
			cstmt.registerOutParameter(2, java.sql.Types.TIME);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			Calendar oCalDefault = Calendar.getInstance();

			msg.setMsg("invoke getTime method");
			Time oRetVal = cstmt.getTime(2, oCalDefault);

			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getTime returns the null value");
			else {
				msg.printTestError("getTime() did not return the null value", "Call to getTime Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime Failed!");

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
	 * @testName: testGetTimestamp01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1265;
	 * JDBC:JAVADOC:1266; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTimestamp(int
	 * parameterIndex) method to retrieve a Timestamp value from Timestamp_Tab.
	 * Extract the the same Timestamp value from the tssql.stmt file.Compare this
	 * value with the value returned by the getTimestamp(int parameterIndex) Both
	 * the values should be equal.
	 */
	@Test
	public void testGetTimestamp01() throws Exception {
		Timestamp oRetVal = null;
		Timestamp maxTimestampVal = null;
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Timestamp_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIMESTAMP);
			cstmt.registerOutParameter(2, java.sql.Types.TIMESTAMP);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getTimestamp method");
			oRetVal = cstmt.getTimestamp(1);
			String sRetStr = rsSch.extractVal("Timestamp_Tab", 1, sqlp, conn);
			msg.setMsg("extracted Timestamp value from Timestamp_Tab");
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));

			maxTimestampVal = Timestamp.valueOf(sRetStr);
			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(maxTimestampVal))
				msg.setMsg("getTimestamp returns the In Time");
			else {
				msg.printTestError("getTimestamp() did not return the proper In Time", "Call to getTimestamp Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp Failed!");

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
	 * @testName: testGetTimestamp02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1265;
	 * JDBC:JAVADOC:1266; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTimestamp(int
	 * parameterIndex) method to retrieve the null value from Timestamp_Tab. Check
	 * if it returns null.
	 */
	@Test
	public void testGetTimestamp02() throws Exception {
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Timestamp_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIMESTAMP);
			cstmt.registerOutParameter(2, java.sql.Types.TIMESTAMP);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getTimestamp method");
			Timestamp oRetVal = cstmt.getTimestamp(2);
			msg.addOutputMsg("null", "" + oRetVal);
			if (oRetVal == null)
				msg.setMsg("getTimestamp returns the null value");
			else {
				msg.printTestError("getTimestamp() did not return the null value", "Call to getTimestamp Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp Failed!");

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
	 * @testName: testGetTimestamp03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1285;
	 * JDBC:JAVADOC:1286; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTimestamp(int
	 * parameterIndex,Calender cal) method to retrieve a Timestamp value from
	 * Timestamp_Tab. Extract the the same Timestamp value from the tssql.stmt
	 * file.Compare this value with the value returned by the getTimestamp(int
	 * parameterIndex, Calender cal) Both the values should be equal.
	 */
	@Test
	public void testGetTimestamp03() throws Exception {
		Timestamp oRetVal = null;
		Timestamp maxTimestampVal = null;
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Timestamp_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIMESTAMP);
			cstmt.registerOutParameter(2, java.sql.Types.TIMESTAMP);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("Calling CallableStatement.getTimestamp(IN_TIME,Calendar)");
			Calendar oCalDefault = Calendar.getInstance();

			msg.setMsg("invoke getTimestamp method");
			oRetVal = cstmt.getTimestamp(1, oCalDefault);
			String sRetStr = rsSch.extractVal("Timestamp_Tab", 1, sqlp, conn);
			msg.setMsg("extracted Timestamp value from Timestamp_Tab");
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			maxTimestampVal = Timestamp.valueOf(sRetStr);
			msg.addOutputMsg("" + maxTimestampVal, oRetVal.toString());

			if (oRetVal.equals(maxTimestampVal))
				msg.setMsg("getTimestamp returns the In Time");
			else {
				msg.printTestError("getTimestamp() did not return the proper In Time", "Call to getTimestamp Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp Failed!");

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
	 * @testName: testGetTimestamp04
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1285;
	 * JDBC:JAVADOC:1286; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getTimestamp(int
	 * parameterIndex,Calender cal) method to retrieve the null value from
	 * Timestamp_Tab. Check if it returns null.
	 */
	@Test
	public void testGetTimestamp04() throws Exception {
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Timestamp_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.TIMESTAMP);
			cstmt.registerOutParameter(2, java.sql.Types.TIMESTAMP);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("get the calendar instance");
			Calendar oCalDefault = Calendar.getInstance();

			msg.setMsg("invoke getTimestamp method");
			Timestamp oRetVal = cstmt.getTimestamp(2, oCalDefault);
			msg.addOutputMsg("null", "" + oRetVal);
			if (oRetVal == null)
				msg.setMsg("getTimestamp returns the null value");
			else {
				msg.printTestError("getTimestamp() did not return the null value", "Call to getTimestamp Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp Failed!");

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
	 * @testName: testGetDate01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1261;
	 * JDBC:JAVADOC:1262; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDate(int
	 * parameterIndex) method to retrieve a Date value from Date_Tab. Extract the
	 * the same Date value from the tssql.stmt file.Compare this value with the
	 * value returned by the getDate(int parameterIndex).Both the values should be
	 * equal.
	 */
	@Test
	public void testGetDate01() throws Exception {
		try {
			rsSch.createTab("Date_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Date_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DATE);
			cstmt.registerOutParameter(2, java.sql.Types.DATE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getDate method");
			java.sql.Date oRetVal = cstmt.getDate(1);
			String sRetStr = rsSch.extractVal("Date_Tab", 1, sqlp, conn);
			msg.setMsg("extracted the Date value from Date_Tab");

			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			sRetStr = sRetStr.trim();
			java.sql.Date oExtVal = java.sql.Date.valueOf(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.toString().equals(oExtVal.toString()))
				msg.setMsg("getDate returns the Date");
			else {
				msg.printTestError("getDate() did not return the proper Manufacturing Date", "Call to getDate Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDate is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDate Failed!");

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
	 * @testName: testGetDate02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1261;
	 * JDBC:JAVADOC:1262; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDate(int
	 * parameterIndex) method to retrieve the null value from Date_Tab. Check if it
	 * returns null.
	 */
	@Test
	public void testGetDate02() throws Exception {
		try {
			rsSch.createTab("Date_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Date_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DATE);
			cstmt.registerOutParameter(2, java.sql.Types.DATE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getDate method");
			java.sql.Date oRetVal = cstmt.getDate(2);
			msg.addOutputMsg("null", "" + oRetVal);
			if (oRetVal == null)
				msg.setMsg("getDate returns the null value");
			else {
				msg.printTestError("getDate() did not return the null value", "Call to getDate Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDate is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDate Failed!");

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
	 * @testName: testGetDate03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1281;
	 * JDBC:JAVADOC:1282; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDate(int
	 * parameterIndex,Calender cal) method to retrieve a Date value from
	 * Date_Tab.Extract the the same Date value from the tssql.stmt file.Compare
	 * this value with the value returned by the getDate(int parameterIndex,
	 * Calender cal) Both the values should be equal.
	 */
	@Test
	public void testGetDate03() throws Exception {
		try {
			rsSch.createTab("Date_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Date_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DATE);
			cstmt.registerOutParameter(2, java.sql.Types.DATE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("get the calendar instance");
			Calendar oCalDefault = Calendar.getInstance();

			msg.setMsg("invoke getTimestamp method");
			java.sql.Date oRetVal = cstmt.getDate(1, oCalDefault);
			String sRetStr = rsSch.extractVal("Date_Tab", 1, sqlp, conn);
			sRetStr = sRetStr.substring(sRetStr.indexOf('\'') + 1, sRetStr.lastIndexOf('\''));
			sRetStr = sRetStr.trim();
			java.sql.Date oExtVal = java.sql.Date.valueOf(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.toString().equals(oExtVal.toString()))
				msg.setMsg("getDate returns the Manufacturing Date");
			else {
				msg.printTestError("getDate() did not return the proper In Time", "Call to getDate Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDate is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDate Failed!");

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
	 * @testName: testGetDate04
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1281;
	 * JDBC:JAVADOC:1282; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDate(int
	 * parameterIndex, Calender cal) method to retrieve the null value from
	 * Date_Tab. Check if it returns null.
	 */
	@Test
	public void testGetDate04() throws Exception {
		try {
			rsSch.createTab("Date_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Date_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DATE);
			cstmt.registerOutParameter(2, java.sql.Types.DATE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("get the calendar instance");
			Calendar oCalDefault = Calendar.getInstance();

			msg.setMsg("invoke getDate method");
			java.sql.Date oRetVal = cstmt.getDate(2, oCalDefault);
			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)

				msg.setMsg("getDate returns the null value");
			else {
				msg.printTestError("getDate() did not return the null value", "Call to getDate Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDate is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDate Failed!");

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
	 * @testName: testGetByte01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1245;
	 * JDBC:JAVADOC:1246; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getByte(int
	 * parameterIndex) method to retrieve the maximum value from Tinyint_Tab.
	 * Extract the maximum value from the tssql.stmt file.Compare this value with
	 * the value returned by the getByte(int parameterIndex).Both the values should
	 * be equal.
	 */
	@Test
	public void testGetByte01() throws Exception {
		Byte oRetVal = null;
		Byte maxByteVal = null;
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
			msg.setMsg("invoke getByte method");
			byte yRetVal = cstmt.getByte(1);
			oRetVal = new Byte(yRetVal);
			String sRetStr = rsSch.extractVal("Tinyint_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Tinyint_Tab");
			maxByteVal = new Byte(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(maxByteVal))
				msg.setMsg("getByte returns the Maximum Value" + oRetVal.toString());
			else {
				msg.printTestError("getByte() did not return the Maximum Value", "Call to getByte Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getByte is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getByte Failed!");

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
	 * @testName: testGetByte02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1245;
	 * JDBC:JAVADOC:1246; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getByte(int
	 * parameterIndex) method to retrieve the minimum value from Tinyint_Tab.
	 * Extract the minimum value from the tssql.stmt file.Compare this value with
	 * the value returned by the getByte(int parameterIndex).Both the values should
	 * be equal.
	 */
	@Test
	public void testGetByte02() throws Exception {
		Byte oRetVal = null;
		Byte minByteVal = null;
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
			msg.setMsg("invoke getByte method");
			byte yRetVal = cstmt.getByte(2);
			oRetVal = new Byte(yRetVal);
			String sRetStr = rsSch.extractVal("Tinyint_Tab", 2, sqlp, conn);
			msg.setMsg("extracted MIN_VAL from Tinyint_Tab");
			minByteVal = new Byte(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(minByteVal))
				msg.setMsg("getByte returns the Minimum Value" + oRetVal.toString());
			else {
				msg.printTestError("getByte() did not return the Minimum Value", "Call to getByte Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getByte is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getByte Failed!");

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
	 * @testName: testGetByte03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1245;
	 * JDBC:JAVADOC:1246; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getByte(int
	 * parameterIndex) method to retrieve the null value from Tinyint_Tab.Check if
	 * it returns null
	 */
	@Test
	public void testGetByte03() throws Exception {
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
			msg.setMsg("invoke getByte method");
			byte yRetVal = cstmt.getByte(3);
			msg.addOutputMsg("0", Byte.toString(yRetVal));

			if (yRetVal == 0)
				msg.setMsg("getByte returns the Null Value");
			else {
				msg.printTestError("getByte() did not return the Null Value", "Call to getByte Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getByte is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getByte Failed!");

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
	 * @testName: testGetDouble01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1255;
	 * JDBC:JAVADOC:1256; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDouble(int
	 * parameterIndex) method to retrieve the maximum value of Double_Tab. Extract
	 * the maximum value from the tssql.stmt file.Compare this value with the value
	 * returned by the getDouble(int parameterIndex). Both the values should be
	 * equal.
	 */
	@Test
	public void testGetDouble01() throws Exception {
		Double oRetVal = null;
		Double maxDoubleVal = null;
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

			msg.setMsg("invoke getDouble method");
			double dRetVal = cstmt.getDouble(1);
			oRetVal = new Double(dRetVal);
			String sRetStr = rsSch.extractVal("Double_Tab", 1, sqlp, conn);
			msg.setMsg("extracted maximum value from Double_Tab");
			maxDoubleVal = new Double(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(maxDoubleVal))
				msg.setMsg("getDouble returns the Maximum Value");
			else {
				msg.printTestError("getDouble() did not return the Maximum Value", "Call to getDouble Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDouble is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDouble Failed!");

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
	 * @testName: testGetDouble02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1255;
	 * JDBC:JAVADOC:1256; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDouble(int
	 * parameterIndex) method to retrieve the minimum value from Double_Tab. Extract
	 * the minimum value from the tssql.stmt file.Compare this value with the value
	 * returned by the getDouble(int parameterIndex). Both the values should be
	 * equal.
	 */
	@Test
	public void testGetDouble02() throws Exception {
		Double oRetVal = null;
		Double minDoubleVal = null;
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

			msg.setMsg("invoke getDouble method");
			double dRetVal = cstmt.getDouble(2);
			oRetVal = new Double(dRetVal);
			String sRetStr = rsSch.extractVal("Double_Tab", 2, sqlp, conn);
			msg.setMsg("extracted minimum value from Double_Tab");
			minDoubleVal = new Double(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(minDoubleVal))
				msg.setMsg("getDouble returns the Minimum Value" + oRetVal.toString());
			else {
				msg.printTestError("getDouble() did not return the Minimum Value", "Call to getDouble Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDouble is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDouble Failed!");

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
	 * @testName: testGetDouble03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1255;
	 * JDBC:JAVADOC:1256; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDouble(int
	 * parameterIndex) method to retrieve the null value from Double_Tab.Check if it
	 * returns null
	 */
	@Test
	public void testGetDouble03() throws Exception {
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

			msg.setMsg("invoke getDouble method");
			double dRetVal = cstmt.getDouble(3);
			msg.addOutputMsg("0.0", new Double(dRetVal).toString());
			;

			if (dRetVal == 0)
				msg.setMsg("getDouble returns the Null Value");
			else {
				msg.printTestError("getDouble() did not return the Null Value", "Call to getDouble Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDouble is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getDouble Failed!");

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
	 * @testName: testWasNull
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1239;
	 * JDBC:JAVADOC:1240; JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getInt(int parameterIndex)
	 * method to retrieve the null value from Integer_Tab. Check if it returns null
	 * using the method wasNull().
	 */
	@Test
	public void testWasNull() throws Exception {
		try {
			rsSch.createTab("Integer_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Integer_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getInteger method");
			int nRetVal = cstmt.getInt(3);
			msg.setMsg("execute the method cstmt.wasNull");

			if (cstmt.wasNull())
				msg.setMsg("The last OUT parameter read had the value of SQL NULL");
			else
				msg.printTestError("The last OUT parameter read did not have the value of SQL NULL", " ");

			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to wasNull is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to wasNull Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Integer_Tab", conn);
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
