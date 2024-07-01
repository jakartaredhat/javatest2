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
 * @(#)resultSetClient47.java	1.24 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.resultSet.resultSet47;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.Connection;
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
import com.sun.ts.tests.jdbc.ee.common.rsSchema;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The resultSetClient47 class tests methods of resultSet interface using Sun's
 * J2EE Reference Implementation.
 * 
 * @author
 * @version 1.7, 99/10/12
 */
@ExtendWith(ArquillianExtension.class)
public class resultSetClient47 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "resultSetClient47_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(resultSetClient47.class);
		archive.addAsWebInfResource(resultSetClient47.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.resultSet.resultSet47";

	private static final Logger logger = (Logger) System.getLogger(resultSetClient47.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements
	private transient Connection conn = null;

	private Statement stmt = null;

	private PreparedStatement pstmt = null;

	private DataSource ds1 = null;

	private String drManager = null;

	private rsSchema rsSch = null;

	private Properties props = null;

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
				if (drManager.length() == 0) {
					throw new Exception("Invalid DriverManager Name");
				}
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
				stmt = conn.createStatement();
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
	 * @testName: testGetString84
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:404;
	 * JDBC:JAVADOC:405; JDBC:JAVADOC:368; JDBC:JAVADOC:369; JavaEE:SPEC:191;
	 * 
	 * @test_Strategy: Get a ResultSet object from the Connection to the database.
	 * Call the getString(String columnName) method with the SQL null column of JDBC
	 * datatype TIMESTAMP.It should return null String object.
	 */
	@Test
	public void testGetString84() throws Exception {
		ResultSet oRes = null;
		try {
			// create the table
			rsSch.createTab("Timestamp_Tab", props, conn);
			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = props.getProperty("Timestamp_Query_Null", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.getString(NullValue)");
			msg.setMsg("get the Null value from the table using getString Method");
			ResultSetMetaData rsMetaData = oRes.getMetaData();
			String sColName = rsMetaData.getColumnName(1);
			String oRetVal = oRes.getString(sColName);

			if (oRetVal == null)
				msg.setMsg("getString returns the Null Value " + oRetVal);
			else {
				msg.printTestError("getString did not return the Null Value", "Call to getString is Failed!");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getString is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getString is Failed!");

		} finally {
			try {
				oRes.close();
				stmt.close();
				// drop the table
				rsSch.dropTab("Timestamp_Tab", conn);
			} catch (Exception eclean) {
				msg.printError(eclean, "Call to getString is Failed!");

			}
		}
	}

	/*
	 * @testName: testGetBytes01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:390;
	 * JDBC:JAVADOC:391; JDBC:JAVADOC:368; JDBC:JAVADOC:369; JavaEE:SPEC:191;
	 * 
	 * @test_Strategy: Get a ResultSet object from the Connection to the
	 * database.Update the column value of Binary_Tab table with a byte array using
	 * the PreparedStatement.setBytes(int columnIndex) method.Call the getBytes(int
	 * columnIndex) method with the SQL column of JDBC datatype BINARY.It should
	 * return the byte array object that has been set.
	 */
	@Test
	public void testGetBytes01() throws Exception {
		String binarySize = null;
		String createString = null, createString1 = null, createString2 = null;
		String executeString = null;
		ResultSet oRes = null;
		try {
			rsSch.createTab("Binary_Tab", props, conn);
			msg.setMsg("extract the Binary Table size from property file");
			binarySize = props.getProperty("binarySize");
			msg.setMsg("Binary Table Size : " + binarySize);

			String sPrepStmt = props.getProperty("Binary_Tab_Val_Update", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("From File" + props.getProperty("bytesize"));

			int bytearrsize = Integer.parseInt(binarySize);

			msg.setMsg("ByteArraySize is :" + bytearrsize);

			byte[] bytearr = new byte[bytearrsize];
			String sbyteval = null;
			// to get the bytearray value
			for (int count = 0; count < bytearrsize; count++) {
				sbyteval = Integer.toString(count % 255);
				bytearr[count] = Byte.parseByte(sbyteval);
			}

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setBytes(1, bytearr);
			pstmt.executeUpdate();

			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = props.getProperty("Binary_Query_Val", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.GetBytes(Value)");
			msg.setMsg("get the value from the table using GetBytes Method");
			byte[] oRetVal = oRes.getBytes(1);
			for (int i = 0; i < bytearrsize; i++) {

				msg.addOutputMsg(Byte.toString(bytearr[i]), Byte.toString(oRetVal[i]));
				if (oRetVal[i] != bytearr[i]) {
					msg.printTestError("GetBytes did not return the proper byte array values", "test getBytes Failed");

				}
			}
			msg.setMsg("GetBytes returns the proper byte array values");
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBytes is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBytes is Failed!");

		} finally {
			try {
				pstmt.close();
				oRes.close();
				stmt.close();
				// drop the table
				rsSch.dropTab("Binary_Tab", conn);
			} catch (Exception eclean) {
				msg.printError(eclean, "Call to getBytes is Failed!");

			}
		}
	}

	/*
	 * @testName: testGetBytes02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:390;
	 * JDBC:JAVADOC:391; JDBC:JAVADOC:370; JDBC:JAVADOC:371; JavaEE:SPEC:191;
	 * 
	 * @test_Strategy: Get a ResultSet object from the Connection to the database.
	 * Call the getBytes(int columnIndex) method with the SQL null column of JDBC
	 * datatype BINARY.It should return null byte array object.
	 */
	@Test
	public void testGetBytes02() throws Exception {
		String binarySize = null;
		String createString = null, createString1 = null, createString2 = null;
		String executeString = null;
		ResultSet oRes = null;
		try {
			rsSch.createTab("Binary_Tab", props, conn);
			msg.setMsg("extract the Binary Table size from property file");
			binarySize = props.getProperty("binarySize");
			msg.setMsg("Binary Table Size : " + binarySize);

			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = props.getProperty("Binary_Query_Val", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.GetBytes(NullValue)");
			msg.setMsg("get the Null value from the table using GetBytes Method");
			byte[] oRetVal = oRes.getBytes(1);
			// check whether the value is null or not
			if (oRes.wasNull())
				msg.setMsg("GetBytes returns the Null Value " + oRetVal);
			else {
				msg.printTestError("GetBytes did not return the Null Value", "test getBytes Failed");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBytes is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBytes is Failed!");

		} finally {
			try {
				oRes.close();
				stmt.close();
				// drop the table
				rsSch.dropTab("Binary_Tab", conn);
			} catch (Exception eclean) {
				msg.printError(eclean, "Call to getBytes is Failed!");

			}
		}
	}

	/*
	 * @testName: testGetBytes03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:390;
	 * JDBC:JAVADOC:391; JavaEE:SPEC:191;
	 * 
	 * @test_Strategy: Get a ResultSet object from the Connection to the
	 * database.Update the column value of Varbinary_Tab table with a byte array
	 * using the PreparedStatement.setBytes(int columnIndex).Call the getBytes(int
	 * columnIndex) method with the SQL column of JDBC datatype VARBINARY.It should
	 * return the byte array object that has been set.
	 */
	@Test
	public void testGetBytes03() throws Exception {
		String varbinarySize = null;
		String createString = null, createString1 = null, createString2 = null;
		String executeString = null;
		ResultSet oRes = null;
		try {
			rsSch.createTab("Varbinary_Tab", props, conn);
			msg.setMsg("extract the Binary Table size from property file");
			varbinarySize = props.getProperty("varbinarySize");
			msg.setMsg("Varbinary Table Size : " + varbinarySize);

			String sPrepStmt = props.getProperty("Varbinary_Tab_Val_Update", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			int bytearrsize = Integer.parseInt(varbinarySize);

			msg.setMsg("ByteArraySize is :" + bytearrsize);

			byte[] bytearr = new byte[bytearrsize];
			String sbyteval = null;
			// to get the bytearray value
			for (int count = 0; count < bytearrsize; count++) {
				sbyteval = Integer.toString(count % 255);
				bytearr[count] = Byte.parseByte(sbyteval);
			}

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setBytes(1, bytearr);
			pstmt.executeUpdate();

			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = props.getProperty("Varbinary_Query_Val", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.GetBytes(Value)");
			msg.setMsg("get the value from the table using GetBytes Method");
			byte[] oRetVal = oRes.getBytes(1);
			for (int i = 0; i < bytearrsize; i++) {

				msg.addOutputMsg(Byte.toString(bytearr[i]), Byte.toString(oRetVal[i]));
				if (oRetVal[i] != bytearr[i]) {
					msg.printTestError("GetBytes did not return the proper byte array values", "test getBytes Failed");

				}
			}
			msg.setMsg("GetBytes returns the proper byte array values");
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBytes is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBytes is Failed!");

		} finally {
			try {
				pstmt.close();
				oRes.close();
				stmt.close();
				// drop the table
				rsSch.dropTab("Varbinary_Tab", conn);
			} catch (Exception eclean) {
				msg.printError(eclean, "Call to getBytes is Failed!");

			}
		}
	}

	/*
	 * @testName: testGetBytes04
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:390;
	 * JDBC:JAVADOC:391; JDBC:JAVADOC:370; JDBC:JAVADOC:371; JavaEE:SPEC:191;
	 * 
	 * @test_Strategy: Get a ResultSet object from the Connection to the database.
	 * Call the getBytes(int columnIndex) method with the SQL null column of JDBC
	 * datatype VARBINARY.It should return null byte array object.
	 */
	@Test
	public void testGetBytes04() throws Exception {
		String varbinarySize = null;
		String createString = null, createString1 = null, createString2 = null;
		String executeString = null;
		ResultSet oRes = null;
		try {
			rsSch.createTab("Varbinary_Tab", props, conn);
			msg.setMsg("extract the Varbinary Table size from property file");
			varbinarySize = props.getProperty("varbinarySize");
			msg.setMsg("Varbinary Table Size : " + varbinarySize);

			msg.setMsg("Execute the query and get the resultSet Object");
			String sQuery = props.getProperty("Varbinary_Query_Val", "");
			oRes = stmt.executeQuery(sQuery);
			oRes.next();
			msg.setMsg("Calling ResultSet.GetBytes(NullValue)");

			msg.setMsg("get the Null value from the table using GetBytes Method");
			byte[] oRetVal = oRes.getBytes(1);
			// check whether the value is null or not
			if (oRes.wasNull())
				msg.setMsg("GetBytes returns the Null Value " + oRetVal);
			else {
				msg.printTestError("GetBytes did not return the Null Value", "test getBytes Failed");

			}
			msg.printTestMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBytes is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBytes is Failed!");

		} finally {
			try {
				oRes.close();
				stmt.close();
				// drop the table
				rsSch.dropTab("Varbinary_Tab", conn);
			} catch (Exception eclean) {
				msg.printError(eclean, "Call to getString is Failed!");

			}
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
