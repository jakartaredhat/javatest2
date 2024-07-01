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
 * @(#)resultSetClient49.java	1.24 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.resultSet.resultSet49;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
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
import com.sun.ts.tests.jdbc.ee.common.DataSourceConnection;
import com.sun.ts.tests.jdbc.ee.common.DriverManagerConnection;
import com.sun.ts.tests.jdbc.ee.common.JDBCTestMsg;
import com.sun.ts.tests.jdbc.ee.common.dbSchema;
import com.sun.ts.tests.jdbc.ee.common.rsSchema;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The resultSetClient49 class tests methods of resultSet interface using Sun's
 * J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class resultSetClient49 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "resultSetClient49_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(resultSetClient49.class);
		archive.addAsWebInfResource(resultSetClient49.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.resultSet.resultSet49";

	private static final Logger logger = (Logger) System.getLogger(resultSetClient49.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements
	private transient Connection conn = null;

	private transient DatabaseMetaData dbmd = null;

	private Statement stmt = null;

	private PreparedStatement pstmt = null;

	private DataSource ds1 = null;

	private String drManager = null;

	private String sqlStmt = null;

	private dbSchema dbSch = null;

	private rsSchema rsSch = null;

	private Properties props = null;

	private Properties sqlp = null;

	private JDBCTestMsg msg = null;

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
				/*
				 * sqlp=new Properties(); sqlStmt= System.getProperty("rsQuery",""); InputStream
				 * istr= new FileInputStream(sqlStmt); sqlp.load(istr);
				 */
				if (drManager.equals("yes")) {
					logger.log(Logger.Level.TRACE, "Using DriverManager");
					DriverManagerConnection dmCon = new DriverManagerConnection();
					conn = dmCon.getConnection();
				} else {
					logger.log(Logger.Level.TRACE, "Using DataSource");
					DataSourceConnection dsCon = new DataSourceConnection();
					conn = dsCon.getConnection();
				}
				dbSch = new dbSchema();
				rsSch = new rsSchema();
				stmt = conn.createStatement(/*
											 * ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY
											 */);
				dbmd = conn.getMetaData();
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
	 * @testName: testGetTime13
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:394;
	 * JDBC:JAVADOC:395; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns the
	 * non-null column of Time_Tab as a Time object.Call the getTime(int
	 * columnIndex) method to retrieve this value.Extract the non-null value of
	 * Time_Tab from the tssql.stmt file as a String.Convert this value into a Time
	 * object.Compare this object with the object returned by the getTime(int
	 * columnIndex).Both of them should be equal.
	 */
	@Test
	public void testGetTime13() throws Exception {
		Time retVal;
		Time brkVal;
		ResultSet rs = null;
		String str = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);
			msg.setMsg("perform query to get the value of Time from Time_Tab");
			String Time_Query_Brk = System.getProperty("Time_Query_Brk", null);
			rs = stmt.executeQuery(Time_Query_Brk);
			rs.next();
			msg.setMsg("Calling getTime on Time_Tab");
			retVal = rs.getTime(1);
			msg.setMsg("extract the Value of Time from Time_Tab");
			str = rsSch.extractVal("Time_Tab", 1, sqlp, conn);
			str = getSingleQuoteContent(str);
			// Convert the string str into a Time object
			brkVal = Time.valueOf(str);

			msg.addOutputMsg("" + brkVal, "" + retVal);
			if (retVal.equals(brkVal))
				msg.setMsg("getTime method returns: " + retVal);
			else {

				msg.printTestError("getTime does not return the Time value from Time_Tab",
						"Call to getTime(int columnIndex) failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime method has failed!!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime method has failed!!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Time_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTime14
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:394;
	 * JDBC:JAVADOC:395; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns null
	 * value from Time_Tab as a Time object.Call the getTime(int columnIndex)
	 * method. Check if the value returned is null.
	 */
	@Test
	public void testGetTime14() throws Exception {
		Time retVal;
		ResultSet rs = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);
			String Time_Query_Null = System.getProperty("Time_Query_Null", null);
			rs = stmt.executeQuery(Time_Query_Null);
			rs.next();
			msg.setMsg("Calling getTime on Time_Tab");
			retVal = rs.getTime(1);
			if (retVal == null)
				msg.setMsg("getTime method returns :" + retVal);
			else {
				msg.printTestError("getTime method does not return null", "test getTime Failed");

			}
			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime method has failed!!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime method has failed!!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Time_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTime16
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:394;
	 * JDBC:JAVADOC:395; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns null
	 * value from Timestamp_Tab as a Time object.Call the getTime(int columnIndex)
	 * method. Check if the value returned is null.
	 */
	@Test
	public void testGetTime16() throws Exception {
		Time retVal;
		ResultSet rs = null;
		try {
			rsSch.createTab("Timestamp_Tab", sqlp, conn);
			String Timestamp_Query_Null = System.getProperty("Timestamp_Query_Null", null);
			rs = stmt.executeQuery(Timestamp_Query_Null);
			rs.next();
			msg.setMsg("Calling getTime on Timestamp_Tab");
			retVal = rs.getTime(1);
			if (retVal == null)
				msg.setMsg("getTime method returns :" + retVal);
			else {
				msg.printTestError("getTime method does not return null", "test getTime Failed");

			}
			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime method has failed!!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime method has failed!!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Timestamp_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTime17
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:426;
	 * JDBC:JAVADOC:427; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns non
	 * null column of Time_Tab as a Time object.Call the getTime(String columnName)
	 * to retrieve this value.Extract the non-null value ofTime_Tab from the
	 * tssql.stmt file as a String.Convert this value into a Time object.Compare
	 * this object with the object returned by the getTime(String columnName)
	 * method. Both of them should be equal.
	 */
	@Test
	public void testGetTime17() throws Exception {
		Time retVal;
		Time brkVal;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String columname = null;
		String str = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);
			msg.setMsg("perform query to get the value of Time from Time_Tab");
			String Time_Query_Brk = System.getProperty("Time_Query_Brk", null);
			rs = stmt.executeQuery(Time_Query_Brk);
			rsmd = rs.getMetaData();
			rs.next();
			msg.setMsg("Calling getTime on Time_Tab");
			columname = rsmd.getColumnName(1);
			retVal = rs.getTime(columname);
			msg.setMsg("extract the Value of Time from Time_Tab");
			str = rsSch.extractVal("Time_Tab", 1, sqlp, conn);
			str = getSingleQuoteContent(str);
			// Convert the string str into a Time object
			brkVal = Time.valueOf(str);

			msg.addOutputMsg("" + brkVal, "" + retVal);
			if (retVal.equals(brkVal))
				msg.setMsg("getTime method returns: " + retVal);
			else {

				msg.printTestError("getTime does not return the Time value from Char_Tab",
						"test getTime(int columnIndex) failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime method has failed!!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime method has failed!!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Time_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTime18
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:426;
	 * JDBC:JAVADOC:427; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns null
	 * value from Time_Tab as a Time object.Call the getTime(String columnName)
	 * method. Check if the value returned is null.
	 */
	@Test
	public void testGetTime18() throws Exception {
		Time retVal;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String columname = null;
		try {
			rsSch.createTab("Time_Tab", sqlp, conn);
			String Time_Query_Null = System.getProperty("Time_Query_Null", null);
			rs = stmt.executeQuery(Time_Query_Null);
			rsmd = rs.getMetaData();
			rs.next();
			msg.setMsg("Calling getTime on Time_Tab");
			columname = rsmd.getColumnName(1);
			retVal = rs.getTime(columname);
			if (retVal == null)
				msg.setMsg("getTime method returns :" + retVal);
			else {
				msg.printTestError("getTime method does not return null", "test getTime Failed!");

			}
			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTime method has failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTime method has failed!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Time_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTimestamp01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:396;
	 * JDBC:JAVADOC:397; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the non-null column of Char_Tab table with the
	 * non-null value of Timestamp_Tab.Execute a query that returns the non-null
	 * column of Char_Tab. table.Call the getTimeStamp(int columnIndex) to retrieve
	 * this value.Compare the value returned with the non null column value of
	 * Timestamp_Tab table. Both of them should be equal.
	 */
	@Test
	public void testGetTimestamp01() throws Exception {
		Timestamp retVal;
		Timestamp brkVal;
		ResultSet rs = null;
		String str = null;
		try {
			rsSch.createTab("Char_Tab", sqlp, conn);
			msg.setMsg("extract the Value of Timestamp from Timestamp_Tab");
			str = rsSch.extractVal("Timestamp_Tab", 1, sqlp, conn);

			logger.log(Logger.Level.INFO, "Table String is " + str);

			str = getSingleQuoteContent(str);

			logger.log(Logger.Level.INFO, "Modified String is " + str);

			String sPrepStatement = System.getProperty("Char_Tab_Name_Update", null);
			PreparedStatement pstmt = conn.prepareStatement(sPrepStatement);
			// Convert the string str into a Timestamp object
			brkVal = Timestamp.valueOf(str);
			pstmt.setString(1, str);
			pstmt.executeUpdate();
			msg.setMsg("perform query to get the value of Timestamp from Timestamp_Tab");
			String Char_Query_Name = System.getProperty("Char_Query_Name", null);
			rs = stmt.executeQuery(Char_Query_Name);
			rs.next();
			msg.setMsg("Calling getTime on Char_Tab");
			retVal = rs.getTimestamp(1);

			msg.addOutputMsg("" + brkVal, "" + retVal);
			if (retVal.equals(brkVal))
				msg.setMsg("getTimestamp method returns: " + retVal);
			else {
				msg.printTestError("getTimestamp does not return the Timestamp value from Char_Tab",
						"test getTimestamp(int columnIndex) Failed");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp method has failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp method has failed!");

		}

		finally {
			try {
				stmt.close();
				pstmt.close();
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTimestamp13
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:396;
	 * JDBC:JAVADOC:397; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns null
	 * value from from Char_Tab.Call the getTimestamp(String columnIndex)
	 * method.Check if it returns null.
	 */
	@Test
	public void testGetTimestamp13() throws Exception {
		Timestamp retVal;
		ResultSet rs = null;
		try {
			rsSch.createTab("Char_Tab", sqlp, conn);
			String Char_Query_Null = System.getProperty("Char_Query_Null", null);
			rs = stmt.executeQuery(Char_Query_Null);
			rs.next();
			msg.setMsg("Calling getTimestamp on Char_Tab");
			retVal = rs.getTimestamp(1);
			if (retVal == null)
				msg.setMsg("getTimestamp method returns :" + retVal);
			else {
				msg.printTestError("getTimestamp method does not return null", "test getTimestamp Failed!");

			}
			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp method has failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp method has failed!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTimestamp03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:396;
	 * JDBC:JAVADOC:397; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the non-null column of Varchar_Tab table with the
	 * non-null value of Timestamp_Tab.Execute a query that returns the non-null
	 * column of Varchar_Tab. table.Call the getTimeStamp(int columnIndex) to
	 * retrieve this value.Compare the value returned with the non null column value
	 * of Timestamp_Tab table. Both of them should be equal.
	 */
	@Test
	public void testGetTimestamp03() throws Exception {
		Timestamp retVal;
		Timestamp brkVal;
		ResultSet rs = null;
		String str = null;
		try {
			rsSch.createTab("Varchar_Tab", sqlp, conn);
			msg.setMsg("extract the Value of Timestamp from Timestamp_Tab");
			str = rsSch.extractVal("Timestamp_Tab", 1, sqlp, conn);

			str = getSingleQuoteContent(str);

			String sPrepStatement = System.getProperty("Varchar_Tab_Name_Update", null);
			PreparedStatement pstmt = conn.prepareStatement(sPrepStatement);
			// Convert the string str into a Timestamp object
			brkVal = Timestamp.valueOf(str);
			pstmt.setString(1, str);
			pstmt.executeUpdate();
			msg.setMsg("perform query to get the value of Timestamp from Timestamp_Tab");
			String Varchar_Query_Name = System.getProperty("Varchar_Query_Name", null);
			rs = stmt.executeQuery(Varchar_Query_Name);
			rs.next();
			msg.setMsg("Calling getTime on Varchar_Tab");
			retVal = rs.getTimestamp(1);
			// Compare the values of retVal & brkVal

			msg.addOutputMsg("" + brkVal, "" + retVal);
			if (retVal.equals(brkVal)) {
				msg.setMsg("getTimestamp method returns: " + retVal);
			} else {
				msg.printTestError("getTimestamp does not return the Timestamp value from Varchar_Tab",
						"Call to getTimestamp(int columnIndex) failed!!!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp method has failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp method has failed!");

		} finally {
			try {
				stmt.close();
				pstmt.close();
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTimestamp04
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:396;
	 * JDBC:JAVADOC:397; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing a query that returns null
	 * value from from Varchar_Tab.Call the getTimestamp(int columnIndex)
	 * method.Check if it returns null.
	 */
	@Test
	public void testGetTimestamp04() throws Exception {
		Timestamp retVal;
		ResultSet rs = null;
		try {
			rsSch.createTab("Varchar_Tab", sqlp, conn);
			String Varchar_Query_Null = System.getProperty("Varchar_Query_Null", null);
			rs = stmt.executeQuery(Varchar_Query_Null);
			rs.next();
			msg.setMsg("Calling getTime on Varchar_Tab");
			retVal = rs.getTimestamp(1);
			if (retVal == null)
				msg.setMsg("getTimestamp method returns :" + retVal);
			else {
				msg.printTestError("getTimestamp method does not return null", "call to getTimestamp failed");

			}
			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp method has failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp method has failed!");

		}

		finally {
			try {
				stmt.close();
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetTimestamp12
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:396;
	 * JDBC:JAVADOC:397; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the non-null column of Longvarchar_Tab table with
	 * the non-null value of Timestamp_Tab.Execute a query that returns the non-null
	 * column of Longvarchar_Tab. table.Call the getTimeStamp(String columnIndex) to
	 * retrieve this value.Compare the value returned with the non null column value
	 * of Timestamp_Tab table. Both of them should be equal.
	 */
	@Test
	public void testGetTimestamp12() throws Exception {
		Timestamp retVal;
		Timestamp brkVal;
		ResultSet rs = null;
		String str = null;
		try {
			rsSch.createTab("Longvarchar_Tab", sqlp, conn);
			msg.setMsg("extract the Value of Timestamp from Timestamp_Tab");
			str = rsSch.extractVal("Timestamp_Tab", 1, sqlp, conn);

			str = getSingleQuoteContent(str);

			String sPrepStatement = System.getProperty("Longvarchar_Tab_Name_Update", null);
			PreparedStatement pstmt = conn.prepareStatement(sPrepStatement);
			// Convert the string str into a Timestamp object
			brkVal = Timestamp.valueOf(str);
			pstmt.setString(1, str);
			pstmt.executeUpdate();
			msg.setMsg("perform query to get the value of Timestamp from Timestamp_Tab");
			String Longvarchar_Query_Name = System.getProperty("Longvarchar_Query_Name", null);
			rs = stmt.executeQuery(Longvarchar_Query_Name);
			rs.next();
			msg.setMsg("Calling getTimestamp on Longvarchar_Tab");
			retVal = rs.getTimestamp(1);
			// Compare the values of retVal & brkVal
			msg.addOutputMsg("" + brkVal, "" + retVal);
			if (retVal.equals(brkVal))
				msg.setMsg("getTimestamp method returns: " + retVal);
			else {
				msg.printTestError("getTimestamp does not return the Timestamp value from Longvarchar_Tab",
						"test getTimestamp Failed");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTimestamp method has failed!!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTimestamp method has failed!!");

		} finally {
			try {
				stmt.close();
				pstmt.close();
				rsSch.dropTab("Longvarchar_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * convenience method to help us facilitate the extracting of strings that are
	 * encased within single quotes ('). If the passed in string contains more than
	 * 2 single quotes, we will only extract the portion of the string that is
	 * between the first 2 single quotes we find.
	 */
	private String getSingleQuoteContent(String str) {
		int index1 = str.indexOf("'"); // get 1st quote
		String retStr = "";

		if (index1 < 0) {
			// no single quotes so just return
			logger.log(Logger.Level.ERROR, "Error - No single quotes found in :  " + str);
			return "";
		} else {
			int index2 = str.indexOf("'", index1 + 1); // get 2nd quote
			if (index2 < 0) {
				// missing a closing quote so return empty string
				logger.log(Logger.Level.ERROR, "Error - No closing quote found in : " + str);
				return "";
			}

			retStr = str.substring(index1 + 1, index2);
			logger.log(Logger.Level.TRACE, "Found the properly quoted string: \"" + retStr + "\"");
		}

		return retStr;
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
