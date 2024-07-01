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
 * @(#)sqlExceptionClient.java	1.16 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.exception.sqlException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
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
 * The sqlExceptionClient class tests methods of SQLException class using Sun's
 * J2EE Reference Implementation.
 * 
 */

@ExtendWith(ArquillianExtension.class)
public class sqlExceptionClient implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "sqlExceptionClient_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(sqlExceptionClient.class);
		archive.addAsWebInfResource(sqlExceptionClient.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.exception.sqlException";

	private static final Logger logger = (Logger) System.getLogger(sqlExceptionClient.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements

	private transient Connection conn = null;

	private ResultSet rs = null;

	private Statement stmt = null;

	private DataSource ds1 = null;

	private dbSchema dbSch = null;

	private rsSchema rsSch = null;

	private String drManager = null;

	private Properties sqlp = null;

	private boolean isThrown = false;

	private String sReason = null;

	private String sSqlState = null;

	private String sVendorCode = null;

	private String sUsr, sPass, sUrl;

	private int vendorCode = 0, maxVal = 0, minVal = 0;

	private int[] updateCount = null;

	private JDBCTestMsg msg = null;

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
				sUrl = System.getProperty("db1", "");
				sUsr = System.getProperty("user1", "");
				sPass = System.getProperty("password1", "");
				/*
				 * sqlp = new Properties(); String sqlStmt= System.getProperty("rsQuery","");
				 * InputStream istr= new FileInputStream(sqlStmt); sqlp.load(istr);
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
				dbSch.createData(conn);
				// conn.setAutoCommit(false);
				msg = new JDBCTestMsg();
				stmt = conn.createStatement();
				sReason = System.getProperty("Reason_BatUpdExec");
				logger.log(Logger.Level.TRACE, "Reason : " + sReason);
				sSqlState = System.getProperty("SQLState_BatUpdExec");
				logger.log(Logger.Level.TRACE, "SQLState : " + sSqlState);

				sVendorCode = System.getProperty("VendorCode_BatUpdExec");
				logger.log(Logger.Level.TRACE, "VendorCode : " + sVendorCode);

				sVendorCode = sVendorCode.trim();
				vendorCode = Integer.valueOf(sVendorCode).intValue();

			} catch (SQLException ex) {
				logger.log(Logger.Level.ERROR, "SQL Exception : " + ex.getMessage());
				throw new Exception("Set Up Failed", ex);
			}
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Setup Failed!");
			TestUtil.printStackTrace(e);
			throw new Exception("Setup Failed");
		}
	}

	/*
	 * @testName: testSQLException01
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:68;
	 * 
	 * @test_Strategy: This method constructs a SQLException Object with no
	 * arguments and for that object the reason,SQLState and ErrorCode are checked
	 * for default values.
	 *
	 */
	@Test
	public void testSQLException01() throws Exception {
		try {
			isThrown = false;
			throw new SQLException();
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			if ((b.getMessage() != null) || (b.getSQLState() != null) || (b.getErrorCode() != 0)) {
				msg.printTestError("SQLException() Constructor Fails", "Call to SQLException() Failed!");

			} else {
				msg.setMsg("SQLException() Constructor is implemented");
			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "Call to SQLException() Failed!");

		}
		if (!isThrown) {
			msg.printTestError("SQLException() not thrown", "Call to SQLException() Failed!");

		}
	}

	/*
	 * @testName: testSQLException02
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:67;
	 * 
	 * @test_Strategy: This method constructs a SQLException Object with one
	 * argument and for that object the SQLState, ErrorCode are checked for default
	 * values.The reason is checked for whatever is been assigned while creating the
	 * new instance.
	 */
	@Test
	public void testSQLException02() throws Exception {
		try {
			isThrown = false;
			throw new SQLException(sReason);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			if ((b.getSQLState() != null) || (b.getErrorCode() != 0)) {
				msg.printTestError("SQLException(String) Constructor Fails",
						"Call to SQLException(String) Constructor Fails");

			} else {
				if ((!sReason.equals(b.getMessage()))) {
					msg.printTestError("SQLException(String) Constructor Fails",
							"Call to SQLException(String) Constructor Fails");

				} else {
					msg.setMsg("SQLException Constructor implemented");
				}
			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "Call to SQLException(String) Constructor Fails");

		}

		if (!isThrown) {
			msg.printTestError("SQLException(String) Constructor Fails",
					"Call to SQLException(String) Constructor Fails");

		}

	}

	/*
	 * @testName: testSQLException03
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:66;
	 * 
	 * @test_Strategy: This method constructs a SQLException Object with two
	 * arguments and for that object ErrorCode is checked for default values.The
	 * reason and SQLState are checked for whatever is been assigned while creating
	 * the new instance.
	 */
	@Test
	public void testSQLException03() throws Exception {
		try {
			isThrown = false;
			throw new SQLException(sReason, sSqlState);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			if ((b.getErrorCode() != 0)) {
				msg.printTestError("SQLException(String,String) Constructor Fails",
						"Call to SQLException(String,String) Constructor Fails");

			} else {
				if ((!sSqlState.equals(b.getSQLState())) || (!sReason.equals(b.getMessage()))) {
					msg.printTestError("SQLException(String,String) Constructor Fails",
							"Call to SQLException(String,String) Constructor Fails");

				} else {
					msg.setMsg("Call to SQLException Passes");
				}
			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "Call to SQLException(String,String) Constructor Fails");

		}

		if (!isThrown) {
			msg.printTestError("SQLException(String,String) not thrown",
					"Call to SQLException(String,String) Constructor Fails");

		}
	}

	/*
	 * @testName: testSQLException04
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:65;
	 * 
	 * @test_Strategy: This method constructs a SQLException Object with three
	 * arguments .The reason,SQLState and Errorcode is checked for whatever is been
	 * assigned while creating the new instance.
	 */
	@Test
	public void testSQLException04() throws Exception {
		try {
			isThrown = false;
			throw new SQLException(sReason, sSqlState, vendorCode);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			if ((!sReason.equals(b.getMessage())) || (!sSqlState.equals(b.getSQLState()))
					|| (!(vendorCode == b.getErrorCode()))) {
				msg.printTestError("SQLException(String,String,int) Constructor Fails",
						"Call to SQLException(String,String,int) Constructor Fails");

			} else {
				msg.setMsg("SQLException(String,String,int) Constructor is implemented");
			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "Call to SQLException(String,String,int) Constructor is Failed!");

		}

		if (!isThrown) {
			msg.printTestError("SQLException(String,String,int) Constructor not thrown",
					"Call to SQLException(String,String,int) Constructor is Failed!");

		}
	}

	/*
	 * @testName: testGetErrorCode
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:70;
	 * 
	 * @testStartegy: The SQLException object is generated by executing an
	 * incomplete SQL Statement and the getErrorCode() method of that object is
	 * checked whether it returns an integer.
	 */
	@Test
	public void testGetErrorCode() throws Exception {
		try {
			String sErrorQuery = System.getProperty("Error_Query");
			stmt.executeQuery(sErrorQuery);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			maxVal = Integer.parseInt((rsSch.extractVal("Integer_Tab", 1, sqlp, conn)).trim());
			minVal = Integer.parseInt((rsSch.extractVal("Integer_Tab", 2, sqlp, conn)).trim());
			if ((b.getErrorCode() <= maxVal) && (b.getErrorCode() >= minVal)) {
				msg.setMsg("getErrorCode() method returns integer value");
			} else {
				msg.printTestError("getErrorCode() Method does not returns integer value",
						"Call to getErrorCode() method fails");

			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "call to getErrorCode() method fails");

		}
		if (!isThrown) {
			msg.printTestError("getErrorCode() Method does not returns integer value",
					"Call to getErrorCode() method fails");

		}
	}

	/*
	 * @testName: testGetSQLState
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:69;
	 * 
	 * @testStartegy: The SQLException object is generated by executing an
	 * incomplete SQL Statement and the getSQLState() method of that object is
	 * checked whether it is an instance of java.lang.String.
	 */
	@Test
	public void testGetSQLState() throws Exception {
		try {
			String sErrorQuery = System.getProperty("Error_Query");
			stmt.executeQuery(sErrorQuery);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			if ((b.getSQLState() instanceof java.lang.String)) {
				msg.setMsg("getSQLState() method returns String value");
			} else {
				msg.printTestError("getSQLState() method does not returns String Value",
						"call to getSQLState() method fails");

			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "call to getSQLState() method fails");

		}

		if (!isThrown) {
			msg.printTestError("SQLException not thrown", "call to getSQLState() method fails");

		}
	}

	/*
	 * @testName: testGetNextException
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:71;
	 * 
	 * @test_Strategy: SQLException object is generated by executing an incomplete
	 * SQL Statement and using setNextException method a SQLException object is
	 * chained. This is checked using the getNextException method which should
	 * return a instanceof SQLException object.
	 */
	@Test
	public void testGetNextException() throws Exception {
		try {
			String sErrorQuery = System.getProperty("Error_Query");
			stmt.executeQuery(sErrorQuery);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			b.setNextException(new SQLException(sReason, sSqlState, vendorCode));
			if ((b.getNextException() instanceof java.sql.SQLException)) {
				msg.setMsg("getNextException() method returns SQLException object");
				msg.setMsg("String is " + b.getMessage());
			} else {
				msg.printTestError("getNextException() mMethod doesnot returns SQLException object",
						"call to getNextException() method fails");

			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "call to getNextException() method fails");

		}

		if (!isThrown) {
			msg.printTestError("SQLException not thrown", "call to getNextException() method fails");

		}
	}

	/*
	 * @testName: testSetNextException
	 * 
	 * @assertion_ids: JDBC:SPEC:6; JDBC:JAVADOC:72;
	 * 
	 * @test_Strategy: SQLException object is obtained by executing a incomplete
	 * SQLStatement and setNextException() method on the object will set a chain of
	 * SQLException on that object which can be checked by using getNextException()
	 * method.
	 *
	 */
	@Test
	public void testSetNextException() throws Exception {
		try {
			String sErrorQuery = System.getProperty("Error_Query");
			stmt.executeQuery(sErrorQuery);
		} catch (SQLException b) {
			TestUtil.printStackTrace(b);

			isThrown = true;
			b.setNextException(new SQLException(sReason, sSqlState, vendorCode));
			if ((b.getNextException() instanceof java.sql.SQLException)) {
				msg.setMsg("setNextException() method sets SQLException object");
				msg.setMsg("String is " + b.getMessage());
			} else {
				msg.printTestError("setNextException() Method doesnot sets SQLException object",
						"call to setNextException() method fails");

			}
			msg.printTestMsg();
		} catch (Exception ex) {
			msg.printError(ex, "call to setNextException() method fails");

		}
		if (!isThrown) {
			msg.printTestError("SQLException not thrown", "call to setNextException() method fails");

		}
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			// conn.setAutoCommit(true);
			stmt.close();
			dbSch.destroyData(conn);
			// Close the database
			dbSch.dbUnConnect(conn);
			logger.log(Logger.Level.INFO, "Cleanup ok;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "An error occurred while closing the database connection", e);
		}
	}
}
