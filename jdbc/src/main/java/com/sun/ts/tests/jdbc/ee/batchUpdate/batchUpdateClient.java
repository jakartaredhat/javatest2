/*
 * Copyright (c) 2007, 2024 Oracle and/or its affiliates. All rights reserved.
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
 * @(#)batchUpdateClient.java	1.34 03/05/16
 */
package com.sun.ts.tests.jdbc.ee.batchUpdate;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import com.sun.ts.tests.jdbc.ee.common.dbSchema;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The batchUpdateClient class tests methods of Statement, PreparedStatement and
 * CallableStatement interfaces using Sun's J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class batchUpdateClient implements Serializable {

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "batchUpdate_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(batchUpdateClient.class);
		archive.addAsWebInfResource(batchUpdateClient.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final Logger logger = (Logger) System.getLogger(batchUpdateClient.class.getName());

	private static final String testName = "jdbc.ee.batchUpdate";

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements
	private transient Connection conn = null;

	private ResultSet rs = null;

	private Statement stmt = null;

	private DataSource ds1 = null;

	private dbSchema dbSch = null;

	private String drManager = null;

	private transient DatabaseMetaData dbmd = null;

	private Properties sqlp = null;

	private boolean supbatupdflag;

	private String fTableName = null;

	PreparedStatement pstmt = null;

	PreparedStatement pstmt1 = null;

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
				fTableName = System.getProperty("ftable", "");

				/*
				 * sqlp = new Properties(); String sqlStmt= System.getProperty("rsQuery","");
				 * InputStream istr= new FileInputStream(sqlStmt); sqlp.load(istr);
				 */
				sqlp = new Properties();
				// TODO
				Properties p = new Properties();

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
				dbSch.createData(conn);
				dbmd = conn.getMetaData();
				supbatupdflag = dbmd.supportsBatchUpdates();
				logger.log(Logger.Level.TRACE, "Driver Supports BatchUpdates  : " + supbatupdflag);
				msg = new JDBCTestMsg();

				if (!supbatupdflag) {
					logger.log(Logger.Level.TRACE, "Driver does not support Batch Updates ");
					throw new Exception("Driver does not support Batch Updates ");
				}

				Insert_Tab(p, conn);
				// conn.setAutoCommit(false);
				stmt = conn.createStatement();

			} catch (SQLException ex) {
				logger.log(Logger.Level.ERROR, "SQL Exception : " + ex.getMessage(), ex);
			}
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Setup Failed!");
			TestUtil.printStackTrace(e);
		}
	}

	/* This Method is to insert new Values into the Table */
	public void Insert_Tab(Properties testProps, Connection conn) throws Exception {
		String newName = null;
		float newPrice = 0;
		int newType = 0;
		String sDelete = System.getProperty("BatchUpdate_Delete");

		try {
			logger.log(Logger.Level.TRACE, "About to delete the Existing Rows");
			Statement stm = conn.createStatement();
			stm.execute(sDelete);
			logger.log(Logger.Level.TRACE, "Deleted the Previous Existed Rows ");

			String strCofSize = testProps.getProperty("cofSize");
			logger.log(Logger.Level.TRACE, "strCofSize: " + strCofSize);

			String strCofTypeSize = testProps.getProperty("cofTypeSize");
			logger.log(Logger.Level.TRACE, "strCofTypeSize : " + strCofTypeSize);

			int cofTypeSize = Integer.parseInt(strCofTypeSize);
			int cofSize = Integer.parseInt(strCofSize);

			// Inserting the New Value
			logger.log(Logger.Level.TRACE, "Adding the " + fTableName + " table rows");
			String updateString1 = System.getProperty("BatchInsert_String");
			pstmt = conn.prepareStatement(updateString1);

			int newKey = 1;
			for (int i = 1; i <= cofTypeSize && newKey <= cofSize; i++) {
				for (int j = 1; j <= i && newKey <= cofSize; j++) {
					newName = "COFFEE-" + newKey;
					newPrice = newKey + (float) .00;
					newType = i;
					pstmt.setInt(1, newKey);
					pstmt.setString(2, newName);
					pstmt.setFloat(3, newPrice);
					pstmt.setInt(4, newType);
					pstmt.executeUpdate();
					newKey = newKey + 1;
				}
			}

			logger.log(Logger.Level.TRACE, "Inserted the Rows ");
		} catch (SQLException sql) {
			logger.log(Logger.Level.ERROR, "SQL Exception " + sql.getMessage());
			throw new Exception("Call to SetupFailed!", sql);
		} catch (Exception ex) {
			logger.log(Logger.Level.ERROR, "Exception " + ex.getMessage());
			throw new Exception("Call to Setup Failed!", ex);
		}
	}

	/*
	 * @testName: testAddBatch01
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:700; JDBC:JAVADOC:701;
	 * JDBC:SPEC:23;
	 * 
	 * 
	 * @test_Strategy: Get a PreparedStatement object and call the addBatch() method
	 * with 3 SQL statements and call the executeBatch() method and it should return
	 * array of Integer values of length 3
	 *
	 */
	@Test
	public void testAddBatch01() throws Exception {
		int i = 0;
		int retValue[] = { 0, 0, 0 };
		try {
			String sPrepStmt = System.getProperty("CoffeeTab_Update", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setInt(1, 2);
			pstmt.addBatch();

			pstmt.setInt(1, 3);
			pstmt.addBatch();

			pstmt.setInt(1, 4);
			pstmt.addBatch();

			int[] updateCount = pstmt.executeBatch();
			int updateCountlen = updateCount.length;

			msg.setMsg("Successfully Updated");
			msg.setMsg("updateCount Length :" + updateCountlen);

			if (updateCountlen != 3) {
				msg.printTestError("addBatch does not add the SQL Statements to Batch ", "call to addBatch failed");

			} else {
				msg.setMsg("addBatch add the SQL statements to Batch ");
			}

			String sPrepStmt1 = System.getProperty("BatchUpdate_Query");

			pstmt1 = conn.prepareStatement(sPrepStmt1);

			// 2 is the number that is set First for Type Id in Prepared Statement
			for (int n = 2; n <= 4; n++) {
				pstmt1.setInt(1, n);
				rs = pstmt1.executeQuery();
				rs.next();
				retValue[i++] = rs.getInt(1);
			}

			pstmt1.close();

			for (int j = 0; j < updateCount.length; j++) {

				msg.addOutputMsg("" + updateCount[j], "" + retValue[j]);
				if (updateCount[j] != retValue[j] && updateCount[j] != Statement.SUCCESS_NO_INFO) {
					msg.printTestError("affected row count does not match with the updateCount value",
							"Call to addBatch is Failed!");
				}
			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to addBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to addBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to addBatch is Failed!");

		}
	}

	/*
	 * @testName: testAddBatch02
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:183; JDBC:JAVADOC:184;
	 * JDBC:JAVADOC:187; JDBC:JAVADOC:188; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a Statement object and call the addBatch() method with 3
	 * SQL statements and call the executeBatch() method and it should return an
	 * array of Integer of length 3.
	 *
	 */
	@Test
	public void testAddBatch02() throws Exception {
		int i = 0;
		int retValue[] = { 0, 0, 0 };
		int updCountLength = 0;

		try {
			String sUpdCoffee = System.getProperty("Upd_Coffee_Tab");
			String sDelCoffee = System.getProperty("Del_Coffee_Tab");
			String sInsCoffee = System.getProperty("Ins_Coffee_Tab");

			msg.setMsg("execute the addBatch method");
			stmt.addBatch(sUpdCoffee);
			stmt.addBatch(sDelCoffee);
			stmt.addBatch(sInsCoffee);

			int[] updateCount = stmt.executeBatch();
			updCountLength = updateCount.length;

			if (updCountLength != 3) {
				msg.printTestError("addBatch does not add the SQL Statements to Batch ", "Call to addBatch is Failed!");

			} else {
				msg.setMsg("addBatch add the SQL statements to Batch ");
			}

			String sPrepStmt1 = System.getProperty("BatchUpdate_Query");

			pstmt1 = conn.prepareStatement(sPrepStmt1);

			// 2 is the number that is set First for Type Id in Prepared Statement
			pstmt1.setInt(1, 1);
			rs = pstmt1.executeQuery();
			rs.next();
			retValue[i++] = rs.getInt(1);

			pstmt1.close();

			// 1 as delete Statement will delete only one row
			retValue[i++] = 1;
			// 1 as insert Statement will insert only one row
			retValue[i++] = 1;
			msg.setMsg("ReturnValue count : " + retValue.length);

			for (int j = 0; j < updateCount.length; j++) {

				msg.addOutputMsg("" + updateCount[j], "" + retValue[j]);
				if (updateCount[j] != retValue[j]) {
					msg.setMsg("affected row count does not match with the updateCount value");
				}
			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to addBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to addBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to addBatch is Failed!");

		}
	}

	/*
	 * @testName: testAddBatch03
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:700; JDBC:JAVADOC:701;
	 * JDBC:JAVADOC:187; JDBC:JAVADOC:188; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a CallableStatement object and call the addBatch() method
	 * with 3 SQL statements and call the executeBatch() method and it should return
	 * an array of Integer of length 3.
	 */
	@Test
	public void testAddBatch03() throws Exception {
		int i = 0;
		int retValue[] = { 0, 0, 0 };
		int updCountLength = 0;
		try {
			msg.setMsg("get the CallableStatement object");
			CallableStatement cstmt = conn.prepareCall("{call UpdCoffee_Proc(?)}");
			cstmt.setInt(1, 2);
			cstmt.addBatch();

			cstmt.setInt(1, 3);
			cstmt.addBatch();

			cstmt.setInt(1, 4);
			cstmt.addBatch();

			msg.setMsg("execute the executeBatch method");
			int[] updateCount = cstmt.executeBatch();
			updCountLength = updateCount.length;

			msg.setMsg("Successfully Updated");

			if (updCountLength != 3) {
				msg.printTestError("addBatch does not add the SQL Statements to Batch ", "Call to addBatch is Failed!");

			} else {
				msg.setMsg("addBatch add the SQL statements to Batch ");
			}

			String sPrepStmt1 = System.getProperty("BatchUpdate_Query");

			pstmt1 = conn.prepareStatement(sPrepStmt1);

			// 2 is the number that is set First for Type Id in Prepared Statement
			for (int n = 2; n <= 4; n++) {
				pstmt1.setInt(1, n);
				rs = pstmt1.executeQuery();
				rs.next();
				retValue[i++] = rs.getInt(1);
			}

			pstmt1.close();

			for (int j = 0; j < updateCount.length; j++) {
				msg.addOutputMsg("" + updateCount[j], "" + retValue[j]);
				if (updateCount[j] != retValue[j]) {
					msg.setMsg("addBatch does not add the SQL Statements to Batch");
				}
			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to addBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to addBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to addBatch is Failed!");

		}
	}

	/*
	 * @testName: testClearBatch01
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:185; JDBC:JAVADOC:186;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a PreparedStatement object and call the addBatch() method
	 * and call the clearBatch() method and then call executeBatch() to check the
	 * call of clearBatch()method The executeBatch() method should return a zero
	 * value.
	 */
	@Test
	public void testClearBatch01() throws Exception {
		try {
			String sPrepStmt = System.getProperty("CoffeeTab_Update", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setInt(1, 2);
			pstmt.addBatch();

			pstmt.setInt(1, 3);
			pstmt.addBatch();

			pstmt.setInt(1, 4);
			pstmt.addBatch();

			msg.setMsg("execute clearBatch() method");
			pstmt.clearBatch();
			int[] updateCount = pstmt.executeBatch();
			int updCountLength = updateCount.length;

			if (updCountLength == 0) {
				msg.setMsg("clearBatch Method clears the current Batch ");
			} else {
				msg.printTestError("clearBatch Method does not clear the Current Batch ",
						"Call to clearBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to clearBatch is Failed!");
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to clearBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to clearBatch is Failed!");

		}
	}

	/*
	 * @testName: testClearBatch02
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:185; JDBC:JAVADOC:186;
	 * JDBC:JAVADOC:183; JDBC:JAVADOC:184; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a Statement object and call the addBatch() method and
	 * call the clearBatch() method and then call executeBatch() to check the call
	 * of clearBatch()method.The executeBatch() method should return a zero value.
	 *
	 */
	@Test
	public void testClearBatch02() throws Exception {
		int updCountLength = 0;
		try {
			String sUpdCoffee = System.getProperty("Upd_Coffee_Tab");
			String sInsCoffee = System.getProperty("Ins_Coffee_Tab");
			String sDelCoffee = System.getProperty("Del_Coffee_Tab");

			msg.setMsg("execute addBatch method");
			stmt.addBatch(sUpdCoffee);
			stmt.addBatch(sDelCoffee);
			stmt.addBatch(sInsCoffee);

			msg.setMsg("execute clearBatch method");
			stmt.clearBatch();

			int[] updateCount = stmt.executeBatch();
			updCountLength = updateCount.length;

			if (updCountLength == 0) {
				msg.setMsg("clearBatch Method clears the current Batch ");
			} else {
				msg.printTestError("clearBatch Method does not clear the current Batch",
						"Call to clearBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to clearBatch is Failed!");
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to clearBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to clearBatch is Failed!");

		}
	}

	/*
	 * @testName: testClearBatch03
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:185; JDBC:JAVADOC:186;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a CallableStatement object and call the addBatch() method
	 * and call the clearBatch() method and then call executeBatch() to check the
	 * call of clearBatch()method. The executeBatch() method should return a zero
	 * value.
	 *
	 */
	@Test
	public void testClearBatch03() throws Exception {
		int updCountLength = 0;
		try {
			msg.setMsg("get the CallableStatement object");
			CallableStatement cstmt = conn.prepareCall("{call UpdCoffee_Proc(?)}");
			cstmt.setInt(1, 2);
			cstmt.addBatch();

			cstmt.setInt(1, 3);
			cstmt.addBatch();

			cstmt.setInt(1, 4);
			cstmt.addBatch();

			msg.setMsg("execute clearBatch method");
			cstmt.clearBatch();
			int[] updateCount = cstmt.executeBatch();

			updCountLength = updateCount.length;

			if (updCountLength == 0) {
				msg.setMsg("clearBatch Method clears the current Batch ");
			} else {
				msg.printTestError("clearBatch Method does not clear the current Batch",
						"Call to clearBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to addBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to clearBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to clearBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch01
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a PreparedStatement object and call the addBatch() method
	 * with a 3 valid SQL statements and call the executeBatch() method It should
	 * return an array of Integer values of length 3.
	 *
	 */
	@Test
	public void testExecuteBatch01() throws Exception {
		int i = 0;
		int retValue[] = { 0, 0, 0 };
		int updCountLength = 0;
		try {
			String sPrepStmt = System.getProperty("CoffeeTab_Update", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setInt(1, 1);
			pstmt.addBatch();

			pstmt.setInt(1, 2);
			pstmt.addBatch();

			pstmt.setInt(1, 3);
			pstmt.addBatch();

			msg.setMsg("execute the executeBatch Method");
			int[] updateCount = pstmt.executeBatch();
			updCountLength = updateCount.length;
			msg.setMsg("Successfully Updated");
			msg.setMsg("updateCount Length :" + updCountLength);

			if (updCountLength != 3) {
				msg.printTestError("executeBatch does not execute the Batch of SQL statements",
						"Call to executeBatch is Failed!");

			} else {
				msg.setMsg("executeBatch executes the Batch of SQL statements");
			}

			String sPrepStmt1 = System.getProperty("BatchUpdate_Query");

			pstmt1 = conn.prepareStatement(sPrepStmt1);

			for (int n = 1; n <= 3; n++) {
				pstmt1.setInt(1, n);
				rs = pstmt1.executeQuery();
				rs.next();
				retValue[i++] = rs.getInt(1);
			}

			pstmt1.close();

			msg.setMsg("retvalue length : " + retValue.length);

			for (int j = 0; j < updateCount.length; j++) {
				msg.addOutputMsg("" + updateCount[j], "" + retValue[j]);
				if (updateCount[j] != retValue[j] && updateCount[j] != Statement.SUCCESS_NO_INFO) {
					msg.printTestError("executeBatch does not execute the Batch of SQL statements",
							"Call to executeBatch is Failed!");
				}
			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch02
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * 
	 * @test_Strategy: Get a PreparedStatement object and call the executeBatch()
	 * method without calling addBatch() method.It should return an array of zero
	 * length.
	 */
	@Test
	public void testExecuteBatch02() throws Exception {
		try {
			String sPrepStmt = System.getProperty("CoffeeTab_Update", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setInt(1, 1);
			pstmt.setInt(1, 2);
			pstmt.setInt(1, 3);

			int[] updateCount = pstmt.executeBatch();
			int updCountLength = updateCount.length;
			msg.setMsg("UpdateCount Length : " + updCountLength);

			if (updCountLength == 0) {
				msg.setMsg("executeBatch does not execute Empty Batch");
			} else {
				msg.printTestError("executeBatch executes Empty Batch", "Call to executeBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch03
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a PreparedStatement object and call the addBatch() method
	 * and call the executeBatch() method with a select Statement It should throw
	 * BatchUpdateException
	 *
	 */
	@Test
	public void testExecuteBatch03() throws Exception {
		boolean bexpflag = false;
		try {
			String sPrepStmt = System.getProperty("CoffeeTab_Select", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);
			pstmt.setInt(1, 1);
			pstmt.addBatch();

			try {
				msg.setMsg("execute the executeBatch method");
				int[] updateCount = pstmt.executeBatch();

			} catch (BatchUpdateException b) {
				TestUtil.printStackTrace(b);

				bexpflag = true;
			}
			if (bexpflag) {
				msg.setMsg("executeBatch does not execute the Batch with a SQL select statement ");
			} else {
				msg.printTestError("executeBatch executes the Batch with a SQL select statement ",
						"Call to executeBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch04
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:183; JDBC:JAVADOC:184; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a Statement object and call the addBatch() method with 3
	 * valid SQL statements and call the executeBatch() method It should return an
	 * array of Integer values of length 3
	 */
	@Test
	public void testExecuteBatch04() throws Exception {
		int i = 0;
		int retValue[] = { 0, 0, 0 };
		int updCountLength = 0;
		try {
			String sUpdCoffee = System.getProperty("Upd_Coffee_Tab");
			String sInsCoffee = System.getProperty("Ins_Coffee_Tab");
			String sDelCoffee = System.getProperty("Del_Coffee_Tab");

			stmt.addBatch(sUpdCoffee);
			stmt.addBatch(sDelCoffee);
			stmt.addBatch(sInsCoffee);

			msg.setMsg("execute the executeBatch method");
			int[] updateCount = stmt.executeBatch();
			updCountLength = updateCount.length;

			msg.setMsg("Successfully Updated");
			msg.setMsg("updateCount Length :" + updCountLength);

			if (updCountLength != 3) {
				msg.printTestError("executeBatch does not execute the Batch of SQL statements",
						"Call to executeBatch is Failed!");

			} else {
				msg.setMsg("executeBatch executes the Batch of SQL statements");
			}

			String sPrepStmt1 = System.getProperty("BatchUpdate_Query");

			pstmt1 = conn.prepareStatement(sPrepStmt1);

			pstmt1.setInt(1, 1);
			rs = pstmt1.executeQuery();
			rs.next();
			retValue[i++] = rs.getInt(1);

			// 1 as Delete Statement will delete only one row
			retValue[i++] = 1;
			// 1 as Insert Statement will insert only one row
			retValue[i++] = 1;

			for (int j = 0; j < updateCount.length; j++) {
				msg.addOutputMsg("" + updateCount[j], "" + retValue[j]);

				if (updateCount[j] != retValue[j]) {
					msg.setMsg("affected row count does not match with the updateCount value");

				}
			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch05
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:183; JDBC:JAVADOC:184;
	 * 
	 * @test_Strategy: Get a Statement object and call the executeBatch() method
	 * without adding statements into a batch. It should return an array of Integer
	 * value of zero length
	 */
	@Test
	public void testExecuteBatch05() throws Exception {
		int updCountLength = 0;
		try {
			String sUpdCoffee = System.getProperty("Upd_Coffee_Tab");
			String sInsCoffee = System.getProperty("Ins_Coffee_Tab");
			String sDelCoffee = System.getProperty("Del_Coffee_Tab");

			int[] updateCount = stmt.executeBatch();
			updCountLength = updateCount.length;
			msg.setMsg("updateCount Length :" + updCountLength);

			if (updCountLength == 0) {
				msg.setMsg("executeBatch Method does not execute the Empty Batch ");
			} else {
				msg.printTestError("executeBatch Method executes the Empty Batch", "Call to executeBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch06
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:183; JDBC:JAVADOC:184; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a Statement object and call the addBatch() method and
	 * call the executeBatch() method with a violation in SQL constraints.It should
	 * throw an BatchUpdateException
	 */
	@Test
	public void testExecuteBatch06() throws Exception {
		boolean bexpflag = false;
		try {
			// Insert a row which is already Present
			String sInsCoffee = System.getProperty("Ins_Coffee_Tab");
			String sDelCoffee = System.getProperty("Del_Coffee_Tab");
			String sUpdCoffee = System.getProperty("Upd_Coffee_Tab");

			stmt.addBatch(sInsCoffee);
			stmt.addBatch(sInsCoffee);
			stmt.addBatch(sDelCoffee);

			try {
				int[] updateCount = stmt.executeBatch();
			} catch (BatchUpdateException b) {
				TestUtil.printStackTrace(b);

				bexpflag = true;
				int[] updCounts = b.getUpdateCounts();
				for (int i = 0; i < updCounts.length; i++) {
					msg.setMsg("Update counts of Successful Commands : " + updCounts[i]);
				}

			}
			if (bexpflag) {
				msg.setMsg("executeBatch does not execute the SQL statement with a violation SQL constraint");
			} else {
				msg.printTestError("executeBatch executes the SQL statement with a violation Constraints",
						"Call to executeBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed!");

		}
	}

	/*
	 * @testName: testExecuteBatch07
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:183; JDBC:JAVADOC:184; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a Statement object and call the addBatch() method and
	 * call the executeBatch() method with a select Statement It should throw an
	 * BatchUpdateException
	 */
	@Test
	public void testExecuteBatch07() throws Exception {
		boolean bexpflag = false;
		try {

			String sDelCoffee = System.getProperty("Del_Coffee_Tab");
			String sUpdCoffee = System.getProperty("Upd_Coffee_Tab");
			String sSelCoffee = System.getProperty("Sel_Coffee_Tab");

			msg.setMsg("sSelCoffee = " + sSelCoffee);
			Statement stmt = conn.createStatement();
			stmt.addBatch(sSelCoffee);

			try {
				int[] updateCount = stmt.executeBatch();
				msg.setMsg("updateCount Length : " + updateCount.length);
			} catch (BatchUpdateException be) {
				TestUtil.printStackTrace(be);

				bexpflag = true;
			}
			if (bexpflag) {
				msg.setMsg("executeBatch does not execute the Batch with a SQL select statement ");
			} else {
				msg.printTestError("executeBatch executes the Batch with a SQL select statement ",
						"Call to executeBatch is Failed");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed");

		}
	}

	/*
	 * @testName: testExecuteBatch08
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a CallableStatement object and call the addBatch() method
	 * with 3 valid SQL statements and call the executeBatch() method It should
	 * return an array of Integer Values of length 3.
	 */
	@Test
	public void testExecuteBatch08() throws Exception {
		int i = 0;
		int retValue[] = { 0, 0, 0 };
		int updCountLength = 0;
		try {
			msg.setMsg("get the CallableStatement object");
			CallableStatement cstmt = conn.prepareCall("{call UpdCoffee_Proc(?)}");

			cstmt.setInt(1, 4);
			cstmt.addBatch();

			cstmt.setInt(1, 3);
			cstmt.addBatch();

			cstmt.setInt(1, 1);
			cstmt.addBatch();

			int[] updateCount = cstmt.executeBatch();
			updCountLength = updateCount.length;

			msg.setMsg("Successfully Updated");
			msg.setMsg("updateCount Length :" + updCountLength);

			if (updCountLength != 3) {
				msg.printTestError("executeBatch does not execute the Batch of SQL statements",
						"Call to executeBatch is Failed");

			} else {
				msg.setMsg("executeBatch executes the Batch of SQL statements");
			}

			String sPrepStmt1 = System.getProperty("BatchUpdate_Query");

			pstmt1 = conn.prepareStatement(sPrepStmt1);

			// 2 is the number that is set First for Type Id in Prepared Statement
			for (int n = 1; n <= 3; n++) {
				pstmt1.setInt(1, n);
				rs = pstmt1.executeQuery();
				rs.next();
				retValue[i++] = rs.getInt(1);
			}

			pstmt1.close();

			for (int j = 0; j < updateCount.length; j++) {

				msg.addOutputMsg("" + updateCount[j], "" + retValue[j]);
				if (updateCount[j] != retValue[j]) {
					msg.setMsg("affected row count does not match with the updateCount value");
				}
			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed");

		}
	}

	/*
	 * @testName: testExecuteBatch09
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * 
	 * @test_Strategy: Get a CallableStatement object and call the executeBatch()
	 * method without adding the statements into Batch. It should return an array of
	 * Integer Value of zero length
	 */
	@Test
	public void testExecuteBatch09() throws Exception {
		int updCountLength = 0;
		try {
			// get the CallableStatement object
			CallableStatement cstmt = conn.prepareCall("{call UpdCoffee_Proc(?)}");
			cstmt.setInt(1, 1);
			cstmt.setInt(1, 2);
			cstmt.setInt(1, 3);

			int[] updateCount = cstmt.executeBatch();
			updCountLength = updateCount.length;

			msg.setMsg("updateCount Length :" + updCountLength);
			if (updCountLength == 0) {
				msg.setMsg("executeBatch Method does not execute the Empty Batch");
			} else {
				msg.printTestError("executeBatch Method executes the Empty Batch", "Call to executeBatch is Failed!");

			}
			msg.printTestMsg();

		} catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed");

		} catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed");

		}
	}

	/*
	 * @testName: testExecuteBatch12
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a CallableStatement object with different SQL statements
	 * in the stored Procedure and call the addBatch() method with 3 statements and
	 * call the executeBatch() method It should return an array of Integer Values of
	 * length 3.
	 */
	@Test
	public void testExecuteBatch12() throws Exception {

		try {

			msg.setMsg("get the CallableStatement object");
			CallableStatement cstmt = conn.prepareCall("{call Coffee_Proc(?)}");
			cstmt.setInt(1, 2);
			cstmt.addBatch();
			cstmt.setInt(1, 3);
			cstmt.addBatch();
			cstmt.setInt(1, 5);
			cstmt.addBatch();
			int[] updateCount = cstmt.executeBatch();
			int updCountLength = updateCount.length;

			msg.setMsg("updateCountLength : " + updCountLength);

			if (updCountLength != 3) {
				msg.printTestError("executeBatch does not execute the Batch of SQL statements",
						"Call to executeBatch is Failed");

			} else {
				msg.setMsg("executeBatch executes the Batch of SQL statements");
			}

			for (int i = 0; i < updCountLength; i++) {
				msg.setMsg("UpdateCount Value : " + updateCount[i]);
			}
			msg.printTestMsg();

		}

		catch (BatchUpdateException b) {
			msg.printSQLError(b, "BatchUpdateException :  Call to executeBatch is Failed!");

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to executeBatch is Failed");

		}

		catch (Exception e) {
			msg.printError(e, "Call to executeBatch is Failed");

		}
	}

	/*
	 * @testName: testContinueBatch01
	 * 
	 * @assertion_ids: JavaEE:SPEC:190; JDBC:JAVADOC:187; JDBC:JAVADOC:188;
	 * JDBC:JAVADOC:700; JDBC:JAVADOC:701; JDBC:SPEC:23;
	 * 
	 * @test_Strategy: Get a PreparedStatement object and call the addBatch() method
	 * with 3 SQL statements.Among these 3 SQL statements first is valid,second is
	 * invalid and third is again valid. Then call the executeBatch() method and it
	 * should return array of Integer values of length 3, if it supports continued
	 * updates. Then check whether the third command in the batch after the invalid
	 * command executed properly.
	 */
	@Test
	public void testContinueBatch01() throws Exception {
		int batchUpdates[] = { 0, 0, 0 };
		int updateCount[] = { 0, 0, 0 };
		int buCountlen = 0;
		int updateCountlen = 0;

		try {
			String sPrepStmt = System.getProperty("CoffeeTab_Continue1", "");
			msg.setMsg("Prepared Statement String :" + sPrepStmt);

			msg.setMsg("get the PreparedStatement object");
			pstmt = conn.prepareStatement(sPrepStmt);

			// Now add a legal update to the batch
			pstmt.setInt(1, 1);
			pstmt.setString(2, "Continue-1");
			pstmt.setString(3, "COFFEE-1");
			pstmt.addBatch();

			// Now add an illegal update to the batch by
			// forcing a unique constraint violation
			// Try changing the key_id of row 3 to 1.
			pstmt.setInt(1, 1);
			pstmt.setString(2, "Invalid");
			pstmt.setString(3, "COFFEE-3");
			pstmt.addBatch();

			// Now add a second legal update to the batch
			// which will be processed ONLY if the driver supports
			// continued batch processing.
			pstmt.setInt(1, 2);
			pstmt.setString(2, "Continue-2");
			pstmt.setString(3, "COFFEE-2");
			pstmt.addBatch();

			// The executeBatch() method will result in a
			// BatchUpdateException
			msg.setMsg("execute the method executeBatch");
			updateCount = pstmt.executeBatch();
		} catch (BatchUpdateException b) {
			TestUtil.printStackTrace(b);

			msg.setMsg("Caught expected BatchUpdateException");
			batchUpdates = b.getUpdateCounts();
			buCountlen = batchUpdates.length;
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to continueUpdate failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to continueUpdate failed!");

		}

		if (buCountlen == 1) {
			msg.setMsg("Driver does not support continued updates - OK");
			return;
		} else if (buCountlen == 3) {
			msg.setMsg("Driver supports continued updates.");
			// Check to see if the third row from the batch was added
			try {
				String query = System.getProperty("CoffeeTab_ContinueSelect1", "");
				msg.setMsg("Query is: " + query);
				rs = stmt.executeQuery(query);
				rs.next();
				int count = rs.getInt(1);
				rs.close();
				stmt.close();
				msg.setMsg("Count val is: " + count);

				// Make sure that we have the correct error code for
				// the failed update.

				if (!(batchUpdates[1] == -3 && count == 1)) {
					throw new Exception("Driver did not insert after error.");
				}
				msg.printTestMsg();

			} catch (SQLException sqle) {
				msg.printSQLError(sqle, "Call to continueUpdate failed!");

			}
		}
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			pstmt.close();
			stmt.close();
			// conn.setAutoCommit(true);
			dbSch.destroyData(conn);
			// Close the database
			dbSch.dbUnConnect(conn);
			logger.log(Logger.Level.INFO, "Cleanup ok;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "An error occurred while closing the database connection", e);
		}
	}
}
