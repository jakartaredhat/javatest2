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
 * %W% %E%
 */

package com.sun.ts.tests.jdbc.ee.resultSet.resultSet41;

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
import java.util.Properties;

import javax.sql.DataSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sun.ts.lib.util.TSNamingContextInterface;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jdbc.ee.common.DataSourceConnection;
import com.sun.ts.tests.jdbc.ee.common.DriverManagerConnection;
import com.sun.ts.tests.jdbc.ee.common.JDBCTestMsg;
import com.sun.ts.tests.jdbc.ee.common.csSchema;
import com.sun.ts.tests.jdbc.ee.common.dbSchema;
import com.sun.ts.tests.jdbc.ee.common.rsSchema;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The resultSetClient41 class tests methods of resultSet interface using Sun's
 * J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class resultSetClient41 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "resultSetClient41_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(resultSetClient41.class);
		archive.addAsWebInfResource(resultSetClient41.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.resultSet.resultSet41";

	private static final Logger logger = (Logger) System.getLogger(resultSetClient41.class.getName());

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

	private csSchema csSch = null;

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
				csSch = new csSchema();
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
	 * @testName: testGetBoolean67
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:406;
	 * JDBC:JAVADOC:407; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that gets the
	 * maximum value of table Bit_Tab.Call the getBoolean(String columnName) method.
	 * Compare the returned result with the value extracted from tssql.stmt
	 * file.Both of them should be equal and the returned result must be equal to
	 * the Maximum Value of JDBC Bit datatype.
	 */

	public void testGetBoolean67() throws Exception {
		try {
			// create the table
			rsSch.createTab("Bit_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Bit_Query_Max", "");
			ResultSet oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getBoolean(MaximumValue)");
			msg.setMsg("get the Maximum value from the table using getBoolean Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			boolean oRetVal = oRes.getBoolean(sColName);

			msg.setMsg("get the Maximum value from the Insert String ");
			boolean oExtVal = rsSch.extractValAsBoolVal("Bit_Tab", 1, sqlp, conn);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getBoolean returns the Maximum Value " + oRetVal);
			else {
				msg.printTestError("getBoolean did not return the Maximum Value", "test getBoolean Failed");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBoolean is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBoolean is Failed!");

		} finally {
			try {
				stmt.close();
				// drop the table
				rsSch.dropTab("Bit_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetBoolean68
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:406;
	 * JDBC:JAVADOC:407; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that gets the
	 * minimum value of table Bit_Tab.Call the getBoolean(String columnName) method.
	 * Compare the returned result with the value extracted from tssql.stmt
	 * file.Both of them should be equal and the returned result must be equal to
	 * the Minimum Value of JDBC Bit datatype.
	 */
	public void testGetBoolean68() throws Exception {
		try {
			// create the table
			rsSch.createTab("Bit_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Bit_Query_Min", "");
			ResultSet oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getBoolean(MinimumValue)");
			msg.setMsg("get the Minimum value from the table using getBoolean Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			boolean oRetVal = oRes.getBoolean(sColName);

			msg.setMsg("get the Minimum value from the Insert String");
			boolean oExtVal = rsSch.extractValAsBoolVal("Bit_Tab", 2, sqlp, conn);

			msg.addOutputMsg("" + oExtVal, "" + oRetVal);
			if (oRetVal == oExtVal)
				msg.setMsg("getBoolean returns the Minimum Value " + oRetVal);
			else {
				msg.printTestError("getBoolean did not return the Minimum Value", "test getBoolean Failed");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBoolean is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBoolean is Failed!");

		} finally {
			try {
				stmt.close();
				// drop the table
				rsSch.dropTab("Bit_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/*
	 * @testName: testGetBoolean69
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:406;
	 * JDBC:JAVADOC:407; JavaEE:SPEC:191;
	 *
	 * @test_Strategy: Get a ResultSet object by executing the query that returns
	 * null value from table Bit_Tab.Call the getBoolean(String columnName)
	 * method.Check if the value returned is boolean value false.
	 */
	public void testGetBoolean69() throws Exception {
		try {
			// create the table
			rsSch.createTab("Bit_Tab", sqlp, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = System.getProperty("Bit_Query_Null", "");
			ResultSet oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getBoolean(NullValue)");
			msg.setMsg("get the Null value from the table using getBoolean Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			boolean oRetVal = oRes.getBoolean(sColName);

			// check whether the value is boolean false
			if (oRetVal == false) {
				msg.setMsg("Calling getBoolean method on a SQL Null column returns boolean " + oRetVal);
			} else {
				msg.printTestError("getBoolean did not return the boolean value false", "test getBoolean Failed");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBoolean is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBoolean is Failed!");

		} finally {
			try {
				stmt.close();
				// drop the table
				rsSch.dropTab("Bit_Tab", conn);
			} catch (Exception eclean) {
			}
		}
	}

	/* cleanup */
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
