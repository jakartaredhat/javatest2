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
 * @(#)dbMetaClient6.java	1.26 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.dbMeta.dbMeta6;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
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

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The dbMetaClient6 class tests methods of DatabaseMetaData interface using
 * Sun's J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class dbMetaClient6 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "dbMetaClient6_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(dbMetaClient6.class);
		archive.addAsWebInfResource(dbMetaClient6.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.dbMeta.dbMeta6";

	private static final Logger logger = (Logger) System.getLogger(dbMetaClient6.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements

	private transient Connection conn = null;

	private transient DatabaseMetaData dbmd = null;

	private DataSource ds1 = null;

	private dbSchema dbSch = null;

	private String dbName = null, dbUser = null, drManager = null;

	private String sCatalogName = null, sSchemaName = null, sPtable = null, sFtable = null;

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
				dbName = System.getProperty("db1", "");
				dbUser = System.getProperty("user1", "");
				sPtable = System.getProperty("ptable", "TSTABLE1");
				sFtable = System.getProperty("ftable", "TSTABLE2");
				if (dbName.length() == 0)
					throw new Exception("Invalid db1  Database Name");
				if (dbUser.length() == 0)
					throw new Exception("Invalid Login Id");
				if (sPtable.length() == 0)
					throw new Exception("Invalid Primary table");
				if (sFtable.length() == 0)
					throw new Exception("Invalid Foreign table");
				if (drManager.length() == 0)
					throw new Exception("Invalid DriverManager Name");

				int nLocdbname = dbName.indexOf('=');
				sCatalogName = dbName.substring(nLocdbname + 1);
				sCatalogName = sCatalogName.trim();
				sSchemaName = dbUser;

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
	 * @testName: testGetCatalogSeparator
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:934; JDBC:JAVADOC:935;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Make a call to DatabaseMetadata.getCatalogSeparator() on that
	 * object. It should return a String and NULL if it is not supported.
	 *
	 */
	@Test
	public void testGetCatalogSeparator() throws Exception {
		try {
			// invoke getCatalogSeparator method
			msg.setMsg("Calling DatabaseMetaData.getCatalogSeparator");
			String sRetValue = dbmd.getCatalogSeparator();
			if (sRetValue == null)
				msg.setMsg("getCatalogSeparator is not supported");
			else
				msg.setMsg("getCatalogSeparator returns " + sRetValue);

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getCatalogSeparator is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getCatalogSeparator is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSchemasInDataManipulation
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:936; JDBC:JAVADOC:937;
	 * JavaEE:SPEC:193;
	 * 
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Make a call to DatabaseMetadata.supportsSchemasInDataManipulation()
	 * on that object. It should return a boolean value either true or false.
	 *
	 */
	@Test
	public void testSupportsSchemasInDataManipulation() throws Exception {
		try {
			// invoke supportsSchemasInDataManipulation method
			msg.setMsg("Calling DatabaseMetaData.supportsSchemasInDataManipulation");
			boolean retValue = dbmd.supportsSchemasInDataManipulation();
			if (retValue)
				msg.setMsg("supportsSchemasInDataManipulation is supported");
			else
				msg.setMsg("supportsSchemasInDataManipulation is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSchemasInDataManipulation is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSchemasInDataManipulation is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSchemasInProcedureCalls
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:938; JDBC:JAVADOC:939;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Make a call to DatabaseMetadata.supportsSchemasInProcedureCalls()
	 * on that object. It should return a boolean value; either true or false
	 *
	 */
	@Test
	public void testSupportsSchemasInProcedureCalls() throws Exception {
		try {
			// invoke supportsSchemasInProcedureCalls method
			msg.setMsg("Calling DatabaseMetaData.supportsSchemasInProcedureCalls");
			boolean retValue = dbmd.supportsSchemasInProcedureCalls();
			if (retValue)
				msg.setMsg("supportsSchemasInProcedureCalls is supported");
			else
				msg.setMsg("supportsSchemasInProcedureCalls is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSchemasInProcedureCalls is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSchemasInProcedureCalls is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSchemasInTableDefinitions
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:940; JDBC:JAVADOC:941;
	 * JavaEE:SPEC:193;
	 * 
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Make a call to DatabaseMetadata.supportsSchemasInTableDefinitions()
	 * on that object.It should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsSchemasInTableDefinitions() throws Exception {
		try {
			// invoke supportsSchemasInTableDefinitions method
			msg.setMsg("Calling DatabaseMetaData.supportsSchemasInTableDefinitions");
			boolean retValue = dbmd.supportsSchemasInTableDefinitions();
			if (retValue)
				msg.setMsg("supportsSchemasInTableDefinitions is supported");
			else
				msg.setMsg("supportsSchemasInTableDefinitions is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSchemasInTableDefinitions is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSchemasInTableDefinitions is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSchemasInIndexDefinitions
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:942; JDBC:JAVADOC:943;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Call to supportsSchemasInIndexDefinitions() on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsSchemasInIndexDefinitions() throws Exception {
		try {
			// invoke supportsSchemasInIndexDefinitions method
			msg.setMsg("Calling DatabaseMetaData.supportsSchemasInIndexDefinitions");
			boolean retValue = dbmd.supportsSchemasInIndexDefinitions();
			if (retValue)
				msg.setMsg("supportsSchemasInIndexDefinitions is supported");
			else
				msg.setMsg("supportsSchemasInIndexDefinitions is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSchemasInIndexDefinitions is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSchemasInIndexDefinitions is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSchemasInPrivilegeDefinitions
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:944; JDBC:JAVADOC:945;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Call to supportsSchemasInPrivilegeDefinitions() on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsSchemasInPrivilegeDefinitions() throws Exception {
		try {
			// invoke supportsSchemasInPrivilegeDefinitions method
			msg.setMsg("Calling DatabaseMetaData.supportsSchemasInPrivilegeDefinitions");
			boolean retValue = dbmd.supportsSchemasInPrivilegeDefinitions();
			if (retValue)
				msg.setMsg("supportsSchemasInPrivilegeDefinitions is supported");
			else
				msg.setMsg("supportsSchemasInPrivilegeDefinitions is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSchemasInPrivilegeDefinitions is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSchemasInPrivilegeDefinitions is Failed!");

		}
	}

	/*
	 * @testName: testSupportsCatalogsInDataManipulation
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:946; JDBC:JAVADOC:947;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Call to supportsCatalogsInDataManipulation()on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsCatalogsInDataManipulation() throws Exception {
		try {
			// invoke supportsCatalogsInDataManipulation method
			msg.setMsg("Calling DatabaseMetaData.supportsCatalogsInDataManipulation");
			boolean retValue = dbmd.supportsCatalogsInDataManipulation();
			if (retValue)
				msg.setMsg("supportsCatalogsInDataManipulation is supported");
			else
				msg.setMsg("supportsCatalogsInDataManipulation is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsCatalogsInDataManipulation is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsCatalogsInDataManipulation is Failed!");

		}
	}

	/*
	 * @testName: testSupportsCatalogsInProcedureCalls
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:948; JDBC:JAVADOC:949;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
	 * DataBase. Call to supportsCatalogsInProcedureCalls() on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsCatalogsInProcedureCalls() throws Exception {
		try {
			// invoke supportsCatalogsInProcedureCalls method
			msg.setMsg("Calling DatabaseMetaData.supportsCatalogsInProcedureCalls");
			boolean retValue = dbmd.supportsCatalogsInProcedureCalls();
			if (retValue)
				msg.setMsg("supportsCatalogsInProcedureCalls is supported");
			else
				msg.setMsg("supportsCatalogsInProcedureCalls is not supported");
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsCatalogsInProcedureCalls is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsCatalogsInProcedureCalls is Failed!");

		}
	}

	/*
	 * @testName: testSupportsCatalogsInTableDefinitions
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:950; JDBC:JAVADOC:951;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database Call the supportsCatalogsInTableDefinitions() method on that object.
	 * It should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsCatalogsInTableDefinitions() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsCatalogsInTableDefinitions()");
			// Invoke supportsCatalogsInTableDefinitions method
			boolean retValue = dbmd.supportsCatalogsInTableDefinitions();
			if (retValue)
				msg.setMsg("supportsCatalogsInTableDefinitions is supported");
			else
				msg.setMsg("supportsCatalogsInTableDefinitions is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsCatalogsInTableDefinitions is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsCatalogsInTableDefinitions is Failed!");

		}
	}

	/*
	 * @testName: testSupportsCatalogsInIndexDefinitions
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:952; JDBC:JAVADOC:953;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database Call the supportsCatalogsInIndexDefinitions() method on that object.
	 * It should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsCatalogsInIndexDefinitions() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsCatalogsInIndexDefinitions");
			// invoke supportsCatalogsInIndexDefinitions method
			boolean retValue = dbmd.supportsCatalogsInIndexDefinitions();
			if (retValue)
				msg.setMsg("supportsCatalogsInIndexDefinitions is supported");
			else
				msg.setMsg("supportsCatalogsInIndexDefinitions is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsCatalogsInIndexDefinitions is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsCatalogsInIndexDefinitions is Failed!");

		}
	}

	/*
	 * @testName: testSupportsCatalogsInPrivilegeDefinitions
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:954; JDBC:JAVADOC:955;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsCatalogsInPrivilegeDefinitions() method on that
	 * object. It should return a boolean value; either true or false
	 *
	 */
	@Test
	public void testSupportsCatalogsInPrivilegeDefinitions() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsCatalogsInPrivilegeDefinitions");
			// invoke supportsCatalogsInPrivilegeDefinitions method
			boolean retValue = dbmd.supportsCatalogsInPrivilegeDefinitions();
			if (retValue)
				msg.setMsg("supportsCatalogsInPrivilegeDefinitions is supported");
			else
				msg.setMsg("supportsCatalogsInPrivilegeDefinitions is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsCatalogInPrivilegeDefinitions is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsCatalogInPrivilegeDefinitions is Failed!");

		}
	}

	/*
	 * @testName: testSupportsPositionedDelete
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:956; JDBC:JAVADOC:957;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsPositionedDelete() method on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsPositionedDelete() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsPositionedDelete");
			// invoke supportsPositionedDelete method
			boolean retValue = dbmd.supportsPositionedDelete();
			if (retValue)
				msg.setMsg("supportsPositionedDelete is supported");
			else
				msg.setMsg("supportsPositionedDelete is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsPositionedDelete is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsPositionedDelete is Failed!");

		}
	}

	/*
	 * @testName: testSupportsPositionedUpdate
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:958; JDBC:JAVADOC:959;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsPositionedUpdate() method on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsPositionedUpdate() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsPositionedUpdate");
			// invoke supportsPositionedUpdate method
			boolean retValue = dbmd.supportsPositionedUpdate();
			if (retValue)
				msg.setMsg("supportsPositionedUpdate is supported");
			else
				msg.setMsg("supportsPositionedUpdate is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsPositionedUpdate is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsPositionedUpdate is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSelectForUpdate
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:960; JDBC:JAVADOC:961;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsSelectForUpdate() method on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsSelectForUpdate() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsSelectForUpdate");
			// invoke supportsSelectForUpdate method
			boolean retValue = dbmd.supportsSelectForUpdate();
			if (retValue)
				msg.setMsg("supportsSelectForUpdate is supported");
			else
				msg.setMsg("supportsSelectForUpdate is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSelectForUpdate is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSelectForUpdate is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSubqueriesInComparisons
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:964; JDBC:JAVADOC:965;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsSubqueriesInComparisons() method on that
	 * object. It should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsSubqueriesInComparisons() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsSubqueriesInComparisons");
			// invoke supportsSubqueriesInComparisons method
			boolean retValue = dbmd.supportsSubqueriesInComparisons();
			if (retValue)
				msg.setMsg("supportsSubqueriesInComparisons is supported");
			else
				msg.setMsg("supportsSubqueriesInComparisons is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSubqueriesInComparisons is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSubqueriesInComparisons is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSubqueriesInExists
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:966; JDBC:JAVADOC:967;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsSubqueriesInExists() method on that object. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsSubqueriesInExists() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsSubqueriesInExists");
			// invoke supportsSubqueriesInExists method
			boolean retValue = dbmd.supportsSubqueriesInExists();
			if (retValue)
				msg.setMsg("supportsSubqueriesInExists is supported");
			else
				msg.setMsg("supportsSubqueriesInExists is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSubqueriesInExists is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSubqueriesInExists is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSubqueriesInIns
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:968; JDBC:JAVADOC:969;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsSubqueriesInIns() method on that object. It
	 * should return a boolean value either true or false.
	 *
	 */
	@Test
	public void testSupportsSubqueriesInIns() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsSubqueriesInIns");
			// invoke supportsSubqueriesInIns method
			boolean retValue = dbmd.supportsSubqueriesInIns();
			if (retValue)
				msg.setMsg("supportsSubqueriesInIns is supported");
			else
				msg.setMsg("supportsSubqueriesInIns is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSubqueriesInIns is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSubqueriesInIns is Failed!");

		}
	}

	/*
	 * @testName: testSupportsSubqueriesInQuantifieds
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:970; JDBC:JAVADOC:971;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsSubqueriesInQuantifieds() method on that
	 * object. It should return a boolean value either true or false.
	 *
	 */
	@Test
	public void testSupportsSubqueriesInQuantifieds() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsSubqueriesInQuantifieds");
			// invoke supportsSubqueriesInQuantifieds method
			boolean retValue = dbmd.supportsSubqueriesInQuantifieds();
			if (retValue)
				msg.setMsg("supportsSubqueriesInQuantifieds is supported");
			else
				msg.setMsg("supportsSubqueriesInQuantifieds is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsSubqueriesInQuantifieds is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsSubqueriesInQuantifieds is Failed!");

		}
	}

	/*
	 * @testName: testSupportsCorrelatedSubqueries
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:972; JDBC:JAVADOC:973;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsCorrelatedSubqueries() method on that object.
	 * It should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsCorrelatedSubqueries() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsCorrelatedSubqueries");
			// invoke supportsCorrelatedSubqueries method
			boolean retValue = dbmd.supportsCorrelatedSubqueries();
			if (retValue)
				msg.setMsg("supportsCorrelatedSubqueries is supported");
			else
				msg.setMsg("supportsCorrelatedSubqueries is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsCorrelatedSubqueries is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsCorrelatedSubqueries is Failed!");

		}
	}

	/*
	 * @testName: testSupportsUnion
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:974; JDBC:JAVADOC:975;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsUnion() method on that object. It should return
	 * a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsUnion() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsUnion");
			// invoke supportsUnion method
			boolean retValue = dbmd.supportsUnion();
			if (retValue)
				msg.setMsg("supportsUnion is supported");
			else
				msg.setMsg("supportsUnion is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsUnion is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsUnion is Failed!");

		}
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			// Close the database
			dbSch.destroyData(conn);
			dbSch.dbUnConnect(conn);
			logger.log(Logger.Level.INFO, "Cleanup ok;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "An error occurred while closing the database connection", e);
		}
	}
}
