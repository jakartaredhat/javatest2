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
 * @(#)resultSetClient17.java	1.22 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.resultSet.resultSet17;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
 * The resultSetClient17 class tests methods of resultSet interface using Sun's
 * J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class resultSetClient17 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "resultSetClient17_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(resultSetClient17.class);
		archive.addAsWebInfResource(resultSetClient17.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.resultSet.resultSet17";

	private static final Logger logger = (Logger) System.getLogger(resultSetClient17.class.getName());

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

	private ResultSet oRes = null;

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
	 * @testName: testGetShort61
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the maximum value of table Decimal_Tab with the
	 * maximum value of table Smallint_Tab.Now execute a query to get the maximum
	 * value of Decimal_Tab table and retrieve the result of the query using the
	 * getShort(String columnName) method.Compare the returned value, with the
	 * maximum value of table Smallint_Tab extracted from the tssql.stmt file. Both
	 * of them should be equal.
	 */
	@Test
	public void testGetShort61() throws Exception {
		try {
			// create the table
			rsSch.createTab("Decimal_Tab", sqlp, conn);
			// update the Max value of Decimal table with the Max value of Smallint
			String sUpdStmt = System.getProperty("Decimal_Tab_Max_Update", "");
			pstmt = conn.prepareStatement(sUpdStmt);
			String sSuppVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			BigDecimal oSuppVal = new BigDecimal(sSuppVal);
			pstmt.setBigDecimal(1, oSuppVal);
			pstmt.executeUpdate();
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Decimal_Query_Max", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(MaximumValue)");

			msg.setMsg("get the Maximum value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);
			msg.setMsg("get the Maximum value from the Insert String");
			String sExtVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			short oExtVal = Short.parseShort(sExtVal);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getShort returns the Maximum Value " + oRetVal);
			else {
				msg.printTestError("getShort did not return the Maximum Value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				// close the prepared statement
				pstmt.close();
				// drop the table
				oRes.close();
				rsSch.dropTab("Decimal_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort63
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that returns
	 * null value from table Decimal_Tab.Call the getShort(String columnName)
	 * method.Check if the value returned is zero.
	 */
	@Test
	public void testGetShort63() throws Exception {
		try {
			// create the table
			rsSch.createTab("Decimal_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Decimal_Query_Null", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(NullValue)");
			msg.setMsg("get the Null value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);

			if (oRetVal == 0) {
				msg.setMsg("Calling getShort method on a SQL Null column returns" + oRetVal);
			} else {
				msg.printTestError("getShort did not return the value zero", "test getShort Failed!");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				oRes.close();
				rsSch.dropTab("Decimal_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort64
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the maximum value of table Numeric_Tab with the
	 * maximum value of table Smallint_Tab.Now execute a query to get the maximum
	 * value of Numeric_Tab table and retrieve the result of the query using the
	 * getShort(String columnName) method.Compare the returned value, with the
	 * maximum value of table Smallint_Tab extracted from the tssql.stmt file. Both
	 * of them should be equal.
	 */
	@Test
	public void testGetShort64() throws Exception {
		try {
			// create the table
			rsSch.createTab("Numeric_Tab", sqlp, conn);
			// update the Max value of Numeric table with the Max value of Smallint
			String sUpdStmt = System.getProperty("Numeric_Tab_Max_Update", "");
			pstmt = conn.prepareStatement(sUpdStmt);
			String sSuppVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			BigDecimal oSuppVal = new BigDecimal(sSuppVal);
			pstmt.setBigDecimal(1, oSuppVal);
			pstmt.executeUpdate();
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Numeric_Query_Max", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(MaximumValue)");
			msg.setMsg("get the Maximum value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);
			msg.setMsg("get the Maximum value from the Insert String ");
			String sExtVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			short oExtVal = Short.parseShort(sExtVal);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getShort returns the Maximum Value " + oRetVal);
			else {
				msg.printTestError("getShort did not return the Maximum Value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				// close the prepared statement
				pstmt.close();
				// drop the table
				oRes.close();
				rsSch.dropTab("Numeric_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort66
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that returns
	 * null value from table Numeric_Tab.Call the getShort(String columnName)
	 * method.Check if the value returned is zero.
	 */
	@Test
	public void testGetShort66() throws Exception {
		try {
			// create the table
			rsSch.createTab("Numeric_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Numeric_Query_Null", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(NullValue)");
			msg.setMsg("get the Null value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);

			if (oRetVal == 0) {
				msg.setMsg("Calling getShort method on a SQL Null column returns" + oRetVal);
			} else {
				msg.printTestError("getShort did not return the value zero", "test getShort Failed!");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				oRes.close();
				rsSch.dropTab("Numeric_Tab", conn);
			} catch (Exception e) {
			}
			;
		}

	}

	/*
	 * @testName: testGetShort70
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the table Char_Tab with the maximum value of
	 * table Smallint_Tab.Now execute a query to get the maximum value of Char_Tab
	 * table and retrieve the result of the query using the getShort(String
	 * columnName) method.Compare the returned value, with the maximum value of
	 * table Smallint_Tab extracted from the tssql.stmt file. Both of them should be
	 * equal.
	 */
	@Test
	public void testGetShort70() throws Exception {
		try {
			// create the table
			rsSch.createTab("Char_Tab", sqlp, conn);
			// update the value of Char table with the Max value of Smallint
			String sUpdStmt = System.getProperty("Char_Tab_Name_Update", "");
			pstmt = conn.prepareStatement(sUpdStmt);
			String sSuppVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			pstmt.setString(1, sSuppVal);
			pstmt.executeUpdate();
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Char_Query_Name", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(MaximumValue)");
			msg.setMsg("get the Maximum value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);
			msg.setMsg("get the Maximum value from the Insert String ");
			String sExtVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			short oExtVal = Short.parseShort(sExtVal);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getShort returns the Maximum Value " + oRetVal);
			else {
				msg.printTestError("getShort did not return the Maximum Value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				// close the prepared statement
				pstmt.close();
				// drop the table
				oRes.close();
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception e) {
			}
			;
		}

	}

	/*
	 * @testName: testGetShort71
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the Char_Tab with the minimum value of table
	 * Smallint_Tab.Now execute a query to get the minimum value of Char_Tab table
	 * and retrieve the result of the query using the getShort(String columnName)
	 * method.Compare the returned value, with the minimum value of table
	 * Smallint_Tab extracted from the tssql.stmt file. Both of them should be
	 * equal.
	 */
	@Test
	public void testGetShort71() throws Exception {
		try {
			// create the table
			rsSch.createTab("Char_Tab", sqlp, conn);
			// update the value of Char table with the Min value of Smallint
			String sUpdStmt = System.getProperty("Char_Tab_Name_Update", "");
			pstmt = conn.prepareStatement(sUpdStmt);
			String sSuppVal = rsSch.extractVal("Smallint_Tab", 2, sqlp, conn);
			pstmt.setString(1, sSuppVal);
			pstmt.executeUpdate();
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Char_Query_Name", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(MinimumValue)");
			msg.setMsg("get the Minimum value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);
			msg.setMsg("get the Minimum value from the Insert String ");
			String sExtVal = rsSch.extractVal("Smallint_Tab", 2, sqlp, conn);
			short oExtVal = Short.parseShort(sExtVal);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getShort returns the Minimum Value " + oRetVal);
			else {
				msg.printTestError("getShort did not return the Minimum Value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				// close the prepared statement
				pstmt.close();
				// drop the table
				oRes.close();
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception e) {
			}
			;
		}

	}

	/*
	 * @testName: testGetShort72
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that returns
	 * null value from table Char_Tab.Call the getShort(String columnName) method.
	 * Check if the value returned is zero.
	 */
	@Test
	public void testGetShort72() throws Exception {
		try {
			// create the table
			rsSch.createTab("Char_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Char_Query_Null", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(NullValue)");
			msg.setMsg("get the Null value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);

			if (oRetVal == 0) {
				msg.setMsg("Calling getShort method on a SQL Null column returns" + oRetVal);
			} else {
				msg.printTestError("getShort did not return the value zero", "test getShort Failed!");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				oRes.close();
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort73
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the Varchar_Tab with the maximum value of table
	 * Smallint_Tab.Now execute a query to value from Varchar_Tab table and retrieve
	 * the result of the query using the getShort(String columnName) method.Compare
	 * the returned value, with the maximum value of table Smallint_Tab extracted
	 * from the tssql.stmt file. Both of them should be equal.
	 */
	@Test
	public void testGetShort73() throws Exception {
		try {
			// create the table
			rsSch.createTab("Varchar_Tab", sqlp, conn);
			// update the value of Varchar table with the Max value of Smallint
			String sUpdStmt = System.getProperty("Varchar_Tab_Name_Update", "");
			pstmt = conn.prepareStatement(sUpdStmt);
			String sSuppVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			pstmt.setString(1, sSuppVal);
			pstmt.executeUpdate();
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Varchar_Query_Name", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(MaximumValue)");
			msg.setMsg("get the Maximum value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);
			msg.setMsg("get the Maximum value from the Insert String ");
			String sExtVal = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			short oExtVal = Short.parseShort(sExtVal);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getShort returns the Maximum Value " + oRetVal);
			else {
				msg.printTestError("getShort did not return the Maximum Value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				// close the prepared statement
				pstmt.close();
				// drop the table
				oRes.close();
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception e) {
			}
			;
		}

	}

	/*
	 * @testName: testGetShort74
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a PreparedStatement object from the connection to the
	 * database. Using this,update the table Varchar_Tab with the minimum value of
	 * table Smallint_Tab.Now execute a query to value from Varchar_Tab table and
	 * retrieve the result of the query using the getShort(String columnName)
	 * method.Compare the returned value, with the minimum value of table
	 * Smallint_Tab extracted from the tssql.stmt file. Both of them should be
	 * equal.
	 */
	@Test
	public void testGetShort74() throws Exception {
		try {
			// create the table
			rsSch.createTab("Varchar_Tab", sqlp, conn);
			// update the value of Varchar table with the Min value of Smallint
			String sUpdStmt = System.getProperty("Varchar_Tab_Name_Update", "");
			pstmt = conn.prepareStatement(sUpdStmt);
			String sSuppVal = rsSch.extractVal("Smallint_Tab", 2, sqlp, conn);
			pstmt.setString(1, sSuppVal);
			pstmt.executeUpdate();
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Varchar_Query_Name", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(MinimumValue)");
			msg.setMsg("get the Minimum value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);
			msg.setMsg("get the Minimum value from the Insert String");
			String sExtVal = rsSch.extractVal("Smallint_Tab", 2, sqlp, conn);
			short oExtVal = Short.parseShort(sExtVal);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getShort returns the Minimum Value " + oRetVal);
			else {
				msg.printTestError("getShort did not return the Minimum Value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				// close the prepared statement
				pstmt.close();
				// drop the table
				oRes.close();
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort75
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:410;
	 * JDBC:JAVADOC:411; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that returns
	 * null value from table Varchar_Tab.Call the getShort(String columnName)
	 * method.Check if the value returned is zero.
	 */
	@Test
	public void testGetShort75() throws Exception {
		try {
			// create the table
			rsSch.createTab("Varchar_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Varchar_Query_Null", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getShort(NullValue)");
			msg.setMsg("get the Null value from the table using getShort Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			short oRetVal = oRes.getShort(sColName);

			if (oRetVal == 0) {
				msg.setMsg("Calling getShort method on a SQL Null column returns" + oRetVal);
			} else {
				msg.printTestError("getShort did not return the value zero", "test getShort Failed!");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort is Failed!");

		} finally {
			try {
				oRes.close();
				rsSch.dropTab("Varchar_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			// Close the Statement
			stmt.close();
			// Close the database
			rsSch.dbUnConnect(conn);
			logger.log(Logger.Level.INFO, "Cleanup ok;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "An error occurred while closing the database connection", e);
		}
	}
}
