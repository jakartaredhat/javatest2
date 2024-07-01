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
 * @(#)callStmtClient1.java	1.20 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.callStmt.callStmt1;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sun.ts.lib.util.TSNamingContextInterface;
// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;
import com.sun.ts.tests.jdbc.ee.common.DataSourceConnection;
import com.sun.ts.tests.jdbc.ee.common.DriverManagerConnection;
import com.sun.ts.tests.jdbc.ee.common.JDBCTestMsg;
import com.sun.ts.tests.jdbc.ee.common.rsSchema;

/**
 * The callStmtClient1 class tests methods of DatabaseMetaData interface using
 * Sun's J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class callStmtClient1 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "callStmt1_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(callStmtClient1.class);
		archive.addAsWebInfResource(callStmtClient1.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		System.out.println(archive.toString(true));
		return archive;
	};
	
	private static final String testName = "jdbc.ee.callStmt.callStmt1";

	private static final Logger logger = (Logger) System.getLogger(callStmtClient1.class.getName());

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
				ex.printStackTrace();
				logger.log(Logger.Level.ERROR, "SQL Exception : " + ex.getMessage(), ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Logger.Level.ERROR, "Setup Failed!");
		}
	}

	/*
	 * @testName: testGetBigDecimal01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1269;
	 * JDBC:JAVADOC:1270; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getBigDecimal(int
	 * parameterIndex) method to retrieve the maximum value of the Numeric_Tab.
	 * Extract the maximum value from the tssql.stmt file. Compare this value with
	 * the value returned by the getBigDecimal(int parameterIndex) method.Both the
	 * values should be equal.
	 */
	@Test
	public void testGetBigDecimal01() throws Exception {
		BigDecimal maxBigDecimalVal = null;
		BigDecimal oRetVal = null;
		try {
			rsSch.createTab("Numeric_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Numeric_Proc(?,?,?)}");

			msg.setMsg("Register the output parameter");
			cstmt.registerOutParameter(1, java.sql.Types.NUMERIC, 15);
			cstmt.registerOutParameter(2, java.sql.Types.NUMERIC, 15);
			cstmt.registerOutParameter(3, java.sql.Types.NUMERIC, 15);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getBigDecimal method");
			oRetVal = cstmt.getBigDecimal(1);
			String sRetStr = rsSch.extractVal("Numeric_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Numeric_Tab");
			maxBigDecimalVal = new BigDecimal(sRetStr);

			msg.addOutputMsg("" + maxBigDecimalVal, "" + oRetVal);

			if ((oRetVal.compareTo(maxBigDecimalVal) == 0)) {
				msg.setMsg("getBigDecimal returns the Maximum value ");
			} else {
				msg.printTestError("getBigDecimal() did not return the Maximum value", "test getBigDecimal Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBigDecimal Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getBigDecimal Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Numeric_Tab", conn);

			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetBigDecimal02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1269;
	 * JDBC:JAVADOC:1270; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getBigDecimal(int
	 * parameterIndex) method to retrieve the minimum value of the Numeric_Tab.
	 * Extract the minimum value from the tssql.stmt file.Compare this value with
	 * the value returned by the getBigDecimal(int parameterIndex).Both the values
	 * should be equal.
	 */
	@Test
	public void testGetBigDecimal02() throws Exception {
		BigDecimal oRetVal = null;
		BigDecimal minBigDecimalVal = null;
		try {
			rsSch.createTab("Numeric_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Numeric_Proc(?,?,?)}");

			msg.setMsg("Register the output parameter");
			cstmt.registerOutParameter(1, java.sql.Types.NUMERIC, 15);
			cstmt.registerOutParameter(2, java.sql.Types.NUMERIC, 15);
			cstmt.registerOutParameter(3, java.sql.Types.NUMERIC, 15);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getBigDecimal method");
			oRetVal = cstmt.getBigDecimal(2);
			String sRetStr = rsSch.extractVal("Numeric_Tab", 2, sqlp, conn);
			msg.setMsg("extracted MIN_VAL from Numeric_Tab");
			minBigDecimalVal = new BigDecimal(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if ((oRetVal.compareTo(minBigDecimalVal) == 0))
				msg.setMsg("getBigDecimal returns the Minimum value ");
			else {
				msg.printTestError("getBigDecimal() did not return the Minimum value", "test getBigdecimal failed!");

			}

			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBigDecimal Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getBigDecimal Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Numeric_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetBigDecimal03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1269;
	 * JDBC:JAVADOC:1270; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getBigDecimal(int
	 * parameterIndex) method to retrieve the null value from Numeric_Tab. Check if
	 * it returns null
	 */
	@Test
	public void testGetBigDecimal03() throws Exception {
		try {
			rsSch.createTab("Numeric_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Numeric_Proc(?,?,?)}");

			msg.setMsg("Register the output parameter");
			cstmt.registerOutParameter(1, java.sql.Types.NUMERIC, 15);
			cstmt.registerOutParameter(2, java.sql.Types.NUMERIC, 15);
			cstmt.registerOutParameter(3, java.sql.Types.NUMERIC, 15);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getBigDecimal method");
			BigDecimal oRetVal = cstmt.getBigDecimal(3);

			msg.addOutputMsg("null", "" + oRetVal);

			if (oRetVal == null)
				msg.setMsg("getBigDecimal returns the Null value ");

			else {
				msg.printTestError("getBigDecimal() did not return the Null value", "test getBigDecimal Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBigDecimal Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getBigDecimal Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Numeric_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetDouble01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1255;
	 * JDBC:JAVADOC:1256; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDouble(int
	 * parameterIndex) method to retrieve the maximum value of the Float_Tab.
	 * Extract the maximum value from the tssql.stmt file.Compare this value with
	 * the value returned by the getDouble(int parameterIndex). Both the values
	 * should be equal.
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
			double fRetVal = cstmt.getDouble(1);
			oRetVal = new Double(fRetVal);
			String sRetStr = rsSch.extractVal("Double_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Double_Tab");
			maxDoubleVal = new Double(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(maxDoubleVal)) {
				msg.setMsg("getDouble returns the Maximum value ");
			} else {
				msg.printTestError("getDouble() did not return the Maximum value", "test getDouble Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDouble Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getDouble Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}

				rsSch.dropTab("Float_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetDouble02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1255;
	 * JDBC:JAVADOC:1256; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDouble(int
	 * parameterIndex) method to retrieve the minimum value of the Float_Tab.
	 * Extract the minimum value from the tssql.stmt file. Compare this value with
	 * the value returned by the getDouble(int parameterIndex). Both the values
	 * should be equal.
	 */
	@Test
	public void testGetDouble02() throws Exception {
		Double oRetVal = null;
		Double minDoubleVal = null;
		try {
			rsSch.createTab("Double_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Double_Proc(?,?,?)}");

			msg.setMsg("Register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(2, java.sql.Types.DOUBLE);
			cstmt.registerOutParameter(3, java.sql.Types.DOUBLE);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getDouble method");
			double fRetVal = cstmt.getDouble(2);
			oRetVal = new Double(fRetVal);
			String sRetStr = rsSch.extractVal("Double_Tab", 2, sqlp, conn);
			msg.setMsg("extracted MIN_VAL from Double_Tab");
			minDoubleVal = new Double(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(minDoubleVal))
				msg.setMsg("getDouble returns the Minimum value ");
			else {
				msg.printTestError("getDouble() did not return the Minimum value", "test getDouble Failed!");
			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDouble Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getDouble Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}

				rsSch.dropTab("Float_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetDouble03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1255;
	 * JDBC:JAVADOC:1256; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getDouble(int
	 * parameterIndex) method to retrieve the null value from Float_Tab. Check if it
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
			msg.setMsg("invoke getFloat method");
			double oRetVal = cstmt.getDouble(3);

			msg.addOutputMsg("0.0", new Double(oRetVal).toString());

			if (oRetVal == 0)
				msg.setMsg("getDouble returns the Null value ");
			else {
				msg.printTestError("getDouble() did not return the Null value", "test getDouble Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getDouble Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getDouble Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Float_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1247;
	 * JDBC:JAVADOC:1248; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JDBC:JAVADOC:3;
	 * JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getShort(int
	 * parameterIndex) method to retrieve the maximum value of the Smallint_Tab.
	 * Extract the maximum value from the tssql.stmt file.Compare this value with
	 * the value returned by the getShort(int parameterIndex). Both the values
	 * should be equal.
	 */
	@Test
	public void testGetShort01() throws Exception {
		Short oRetVal = null;
		Short maxShortVal = null;
		try {
			rsSch.createTab("Smallint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Smallint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.SMALLINT, 0);
			cstmt.registerOutParameter(2, java.sql.Types.SMALLINT, 0);
			cstmt.registerOutParameter(3, java.sql.Types.SMALLINT, 0);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();
			msg.setMsg("invoke getShort method");
			short rRetVal = cstmt.getShort(1);
			oRetVal = new Short(rRetVal);
			String sRetStr = rsSch.extractVal("Smallint_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Smallint_Tab");
			maxShortVal = new Short(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(maxShortVal))
				msg.setMsg("getShort returns the Maximum value ");
			else {
				msg.printTestError("getShort() did not return the Maximum value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Smallint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1247;
	 * JDBC:JAVADOC:1248; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getShort(int
	 * parameterIndex) method to retrieve the minimum value of the Smallint_Tab.
	 * Extract the minimum value from the tssql.stmt file.Compare this value with
	 * the value returned by the getShort(int parameterIndex). Both the values
	 * should be equal.
	 */
	@Test
	public void testGetShort02() throws Exception {
		Short oRetVal = null;
		Short minShortVal = null;
		try {
			rsSch.createTab("Smallint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Smallint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.SMALLINT, 0);
			cstmt.registerOutParameter(2, java.sql.Types.SMALLINT, 15);
			cstmt.registerOutParameter(3, java.sql.Types.SMALLINT, 0);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getShort method");
			short rRetVal = cstmt.getShort(2);
			oRetVal = new Short(rRetVal);
			String sRetStr = rsSch.extractVal("Smallint_Tab", 2, sqlp, conn);
			msg.setMsg("extracted MIN_VAL from Smallint_Tab");
			minShortVal = new Short(sRetStr);
			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(minShortVal))
				msg.setMsg("getShort returns the Minimum value ");
			else {
				msg.printTestError("getShort() did not return the Minimum value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Smallint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetShort03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1247;
	 * JDBC:JAVADOC:1248; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getShort(int
	 * parameterIndex) method to retrieve the null value from the Smallint_Tab.
	 * Check if it returns null
	 */
	@Test
	public void testGetShort03() throws Exception {
		try {
			rsSch.createTab("Smallint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Smallint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.SMALLINT, 0);
			cstmt.registerOutParameter(2, java.sql.Types.SMALLINT, 0);
			cstmt.registerOutParameter(3, java.sql.Types.SMALLINT, 0);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getShort method");
			short rRetVal = cstmt.getShort(3);

			msg.addOutputMsg("0", Short.toString(rRetVal));

			if (rRetVal == 0)
				msg.setMsg("getShort returns the Null value ");
			else {
				msg.printTestError("getShort() did not return the Null value", "test getShort Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getShort is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getShort Failed!");

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Smallint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetString01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1241;
	 * JDBC:JAVADOC:1242; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getString(int
	 * parameterIndex) method to retrieve a non null String value from Char_Tab.
	 * Extract the same String value from the tssql.stmt file.Compare this value
	 * with the value returned by the getString(int parameterIndex). Both the values
	 * should be equal.
	 */
	@Test
	public void testGetString01() throws Exception {
		try {
			rsSch.createTab("Char_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Char_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.CHAR);
			cstmt.registerOutParameter(2, java.sql.Types.CHAR);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getString method");
			String sRetVal = cstmt.getString(1);
			sRetVal = sRetVal.trim();
			String sRetStr = rsSch.extractVal("Char_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Smallint_Tab");
			sRetStr = sRetStr.trim();
			msg.addOutputMsg(sRetStr, sRetVal);

			if (sRetVal.equals(sRetStr.substring(1, sRetStr.length() - 1)))
				msg.setMsg("getString returns the non null String value");
			else {
				msg.printTestError("getString() did not return the non null String value", "test getString Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();

		} catch (SQLException sqle) {

			msg.printSQLError(sqle, "Call to getString is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getString Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetString02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1241;
	 * JDBC:JAVADOC:1242; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getString(int
	 * parameterIndex) method to retrieve the null value from Char_Tab. Check if it
	 * returns null
	 */
	@Test
	public void testGetString02() throws Exception {
		try {
			rsSch.createTab("Char_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Char_Proc(?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.CHAR);
			cstmt.registerOutParameter(2, java.sql.Types.CHAR);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getString method");
			String sRetVal = cstmt.getString(2);

			msg.addOutputMsg("null", sRetVal);

			if (sRetVal == null)
				msg.setMsg("getString returns null value" + sRetVal);
			else {
				msg.printTestError("getString() did not return the null value", "test getString Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getString is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getString Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Char_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetInt01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1249;
	 * JDBC:JAVADOC:1250; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JDBC:JAVADOC:4;
	 * JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getInt(int parameterIndex)
	 * method to retrieve the maximum value from Integer_Tab. Extract the maximum
	 * value from the tssql.stmt file.Compare this value with the value returned by
	 * the getInt(int parameterIndex). Both the values should be equal.
	 */
	@Test
	public void testGetInt01() throws Exception {
		Integer oRetVal = null;
		Integer maxIntegerVal = null;
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

			msg.setMsg("invoke getInt method");
			int nRetVal = cstmt.getInt(1);
			oRetVal = new Integer(nRetVal);
			String sRetStr = rsSch.extractVal("Integer_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Integer_Tab");
			maxIntegerVal = new Integer(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(maxIntegerVal))
				msg.setMsg("getInt returns the Maximum value ");
			else {
				msg.printTestError("getInt() did not return the Maximum value", "test getInt Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getInt is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getInt is Failed!");
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

	/*
	 * @testName: testGetInt02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1249;
	 * JDBC:JAVADOC:1250; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getInt(int parameterIndex)
	 * method to retrieve the minimum value from Integer_Tab. Extract the minimum
	 * value from the tssql.stmt file. Compare this value with the value returned by
	 * the getInt(int parameterIndex). Both the values should be equal.
	 */
	@Test
	public void testGetInt02() throws Exception {
		Integer oRetVal = null;
		Integer minIntegerVal = null;
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

			msg.setMsg("invoke getInt method");
			int nRetVal = cstmt.getInt(2);
			oRetVal = new Integer(nRetVal);
			String sRetStr = rsSch.extractVal("Integer_Tab", 2, sqlp, conn);
			msg.setMsg("extracted MIN_VAL from Integer_Tab");
			minIntegerVal = new Integer(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(minIntegerVal))
				msg.setMsg("getInt returns the Minimum value ");
			else {
				msg.printTestError("getInt() did not return the Maximum value", "test getInt Failed!");
			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getInt is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getInt is Failed!");
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

	/*
	 * @testName: testGetInt03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1249;
	 * JDBC:JAVADOC:1250; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getInt(int parameterIndex)
	 * method to retrieve the null value. Check if it returns null
	 */
	@Test
	public void testGetInt03() throws Exception {
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
			msg.setMsg("invoke getInt method");
			int nRetVal = cstmt.getInt(3);

			msg.addOutputMsg("0", new Integer(nRetVal).toString());
			if (nRetVal == 0)
				msg.setMsg("getInt returns the Null value ");
			else {
				msg.printTestError("getInt() did not return the null value", "test getInt Failed!");
			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getInt is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getInt is Failed!");
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

	/*
	 * @testName: testGetBoolean01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1243;
	 * JDBC:JAVADOC:1244; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JDBC:JAVADOC:1;
	 * JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getBoolean(int
	 * parameterIndex) method to retrieve the maximum value of Bit_Tab. Extract the
	 * maximum value from the tssql.stmt file.Compare this value with the value
	 * returned by the getBoolean(int parameterIndex).Both the values should be
	 * equal.
	 */
	@Test
	public void testGetBoolean01() throws Exception {
		Boolean oRetVal = null;
		Boolean maxBooleanVal = null;
		try {
			rsSch.createTab("Bit_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Bit_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.BIT);
			cstmt.registerOutParameter(2, java.sql.Types.BIT);
			cstmt.registerOutParameter(3, java.sql.Types.BIT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getBoolean method");
			boolean bRetVal = cstmt.getBoolean(1);
			oRetVal = new Boolean(bRetVal);
			String sRetStr = rsSch.extractVal("Boolean_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Boolean_Tab");
			maxBooleanVal = new Boolean(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(maxBooleanVal))
				msg.setMsg("getBoolean returns the Maximum value ");
			else {
				msg.printTestError("getBoolean() did not return the Maximum value", "test getBoolean failed");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBoolean is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getBoolean is Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Bit_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetBoolean02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1243;
	 * JDBC:JAVADOC:1244; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JDBC:JAVADOC:1;
	 * JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getBoolean(int
	 * parameterIndex) method.to retrieve the minimum value of Bit_Tab. Extract the
	 * minimum value from the tssql.stmt file. Compare this value with the value
	 * returned by the getBoolean(int parameterIndex) Both the values should be
	 * equal.
	 */
	@Test
	public void testGetBoolean02() throws Exception {
		Boolean oRetVal = null;
		Boolean minBooleanVal = null;
		try {
			rsSch.createTab("Bit_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Bit_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.BIT);
			cstmt.registerOutParameter(2, java.sql.Types.BIT);
			cstmt.registerOutParameter(3, java.sql.Types.BIT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getBoolean method");
			boolean bRetVal = cstmt.getBoolean(2);
			oRetVal = new Boolean(bRetVal);
			String sRetStr = rsSch.extractVal("Boolean_Tab", 2, sqlp, conn);
			msg.setMsg("extracted MIN_VAL from Boolean_Tab");
			minBooleanVal = new Boolean(sRetStr);

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(minBooleanVal))
				msg.setMsg("getBoolean returns the Minimum value ");
			else {
				msg.printTestError("getBoolean() did not return the Minimum value", "getBoolean Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getBoolean is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getBoolean is Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Bit_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetLong01
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1251;
	 * JDBC:JAVADOC:1252; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JDBC:JAVADOC:5;
	 * JavaEE:SPEC:183; JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getLong(int
	 * parameterIndex) method to retrieve the maximum value of Bigint_Tab. Extract
	 * the maximum value from the tssql.stmt file.Compare this value with the value
	 * returned by the getLong(int parameterIndex). Both the values should be equal.
	 */
	@Test
	public void testGetLong01() throws Exception {
		Long oRetVal = null;
		Long maxLongVal = null;
		try {
			rsSch.createTab("Bigint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Bigint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");

			cstmt.registerOutParameter(1, java.sql.Types.BIGINT);
			cstmt.registerOutParameter(2, java.sql.Types.BIGINT);
			cstmt.registerOutParameter(3, java.sql.Types.BIGINT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getLong method");
			long lRetVal = cstmt.getLong(1);
			oRetVal = new Long(lRetVal);
			String sRetStr = rsSch.extractVal("Bigint_Tab", 1, sqlp, conn);
			msg.setMsg("extracted MAX_VAL from Bigint_Tab");
			maxLongVal = new Long(sRetStr);
			msg.addOutputMsg(sRetStr, oRetVal.toString());

			if (oRetVal.equals(maxLongVal))
				msg.setMsg("getLong returns the Maximum value ");
			else {
				msg.printTestError("getLong() did not return the Maximum value", "test getLong Failed!");

			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getLong is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getLong is Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Bigint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetLong02
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1251;
	 * JDBC:JAVADOC:1252; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getLong(int
	 * parameterIndex) method to retrieve the minimum value from Bigint_Tab. Extract
	 * the minimum value from the tssql.stmt file.Compare this value with the value
	 * returned by the getLong(int parameterIndex) Both the values should be equal.
	 */
	@Test
	public void testGetLong02() throws Exception {
		Long oRetVal = null;
		Long minLongVal = null;
		try {
			rsSch.createTab("Bigint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Bigint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.BIGINT);
			cstmt.registerOutParameter(2, java.sql.Types.BIGINT);
			cstmt.registerOutParameter(3, java.sql.Types.BIGINT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getLong method");
			long lRetVal = cstmt.getLong(2);
			oRetVal = new Long(lRetVal);
			String sRetStr = rsSch.extractVal("Bigint_Tab", 2, sqlp, conn);
			minLongVal = new Long(sRetStr);
			msg.setMsg("extracted MIN_VAL from Bigint_Tab");

			msg.addOutputMsg(sRetStr, oRetVal.toString());
			if (oRetVal.equals(minLongVal))
				msg.setMsg("getLong returns the Minimum value ");
			else {
				msg.printTestError("getLong() did not return the Minimum value", "test getLong Failed!");
			}
			msg.printTestMsg();
			msg.printOutputMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getLong is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getLong is Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Bigint_Tab", conn);
			} catch (Exception e) {
			}
			;
		}
	}

	/*
	 * @testName: testGetLong03
	 * 
	 * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1251;
	 * JDBC:JAVADOC:1252; JDBC:JAVADOC:1145; JDBC:JAVADOC:1146; JavaEE:SPEC:183;
	 * JavaEE:SPEC:185;
	 *
	 * @test_Strategy: Get a CallableStatement object from the connection to the
	 * database.Execute the stored procedure and call the getLong(int
	 * parameterIndex) method to retrieve the null value from Bigint_Tab. Check if
	 * it returns null
	 */
	@Test
	public void testGetLong03() throws Exception {
		try {
			rsSch.createTab("Bigint_Tab", sqlp, conn);

			msg.setMsg("get the CallableStatement object");
			cstmt = conn.prepareCall("{call Bigint_Proc(?,?,?)}");

			msg.setMsg("register the output parameters");
			cstmt.registerOutParameter(1, java.sql.Types.BIGINT);
			cstmt.registerOutParameter(2, java.sql.Types.BIGINT);
			cstmt.registerOutParameter(3, java.sql.Types.BIGINT);

			msg.setMsg("execute the procedure");
			cstmt.executeUpdate();

			msg.setMsg("invoke getLong method");
			long lRetVal = cstmt.getLong(3);

			msg.addOutputMsg("0", new Long(lRetVal).toString());

			if (lRetVal == 0)
				msg.setMsg("getLong returns the Null value ");
			else {
				msg.printTestError("getLong() did not return the null value", "test getLong Failed!");
			}
			msg.printTestMsg();
			msg.printOutputMsg();
		}

		catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getLong is Failed!");
		} catch (Exception e) {
			msg.printError(e, "Call to getLong is Failed!");
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				rsSch.dropTab("Bigint_Tab", conn);
			} catch (Exception e) {
			}
			;
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
