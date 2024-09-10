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
 * @(#)callStmtClient19.java	1.20 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.callStmt.callStmt19;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.harness.Status;

import tck.arquillian.porting.lib.spi.TestArchiveProcessor;
import tck.arquillian.protocol.common.TargetVehicle;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The callStmtClient19 class tests methods of CallableStatement interface (to
 * check the Support for IN, OUT and INOUT parameters of Stored Procedure) using
 * Sun's J2EE Reference Implementation.
 * 
 * @author
 * @version 1.7, 06/16/99
 */

public class callStmtClient19AppClient extends callStmtClient19 implements Serializable {
  private static final String testName = "jdbc.ee.callStmt.callStmt19";
  
  @TargetsContainer("tck-javatest")
  @OverProtocol("appclient")
	@Deployment(name = "appclient", order = 2)
	public static JavaArchive createDeploymentAppclient(@ArquillianResource TestArchiveProcessor archiveProcessor) throws IOException {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "callStmt19_appclient_vehicle_client.jar");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addPackages(true, "com.sun.ts.tests.common.vehicle");
		archive.addPackages(true, "com.sun.ts.lib.harness");
		archive.addClasses(callStmtClient19AppClient.class, callStmtClient19.class);
		  // The appclient-client descriptor
	     URL appClientUrl = callStmtClient19AppClient.class.getResource("appclient_vehicle_client.xml");
	     if(appClientUrl != null) {
	     	archive.addAsManifestResource(appClientUrl, "application-client.xml");
	     }
	     // The sun appclient-client descriptor
	     URL sunAppClientUrl = callStmtClient19AppClient.class.getResource("callStmt19_appclient_vehicle_client.jar.sun-application-client.xml");
	     if(sunAppClientUrl != null) {
	     	archive.addAsManifestResource(sunAppClientUrl, "sun-application-client.xml");
	     }
	     // Call the archive processor
	     archiveProcessor.processClientArchive(archive, callStmtClient19AppClient.class, sunAppClientUrl);
		System.out.println(archive.toString(true));
		return archive;
	};


  public static void main(String[] args) {
    callStmtClient19AppClient theTests = new callStmtClient19AppClient();
    Status s = theTests.run(args, System.out, System.err);
    s.exit();
  }


  /*
   * @testName: testSetObject221
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Date object for SQL
   * Type Date and call statement.executeQuery method and call getObject method
   * of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject221() throws Exception {
		super.testSetObject221();
  }

  /*
   * @testName: testSetObject223
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Time object for SQL
   * Type Char and call statement.executeQuery method and call getObject method
   * of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject223() throws Exception {
		super.testSetObject223();
  }

  /*
   * @testName: testSetObject224
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Time object for SQL
   * Type Varchar and call statement.executeQuery method and call getObject
   * method of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject224() throws Exception {
		super.testSetObject224();
  }

  /*
   * @testName: testSetObject225
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Time object for SQL
   * Type Longvarchar and call statement.executeQuery method and call getObject
   * method of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject225() throws Exception {
		super.testSetObject225();
  }

  /*
   * @testName: testSetObject226
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Time object for SQL
   * Type Time and call statement.executeQuery method and call getObject method
   * of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject226() throws Exception {
		super.testSetObject226();
  }

  /*
   * @testName: testSetObject227
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Timestamp object for
   * SQL Type Char and call statement.executeQuery method and call getObject
   * method of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject227() throws Exception {
		super.testSetObject227();
  }

  /*
   * @testName: testSetObject228
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Timestamp object for
   * SQL Type Varchar and call statement.executeQuery method and call getObject
   * method of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject228() throws Exception {
		super.testSetObject228();
  }

  /*
   * @testName: testSetObject229
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Timestamp object for
   * SQL Type Longvarchar and call statement.executeQuery method and call
   * getObject method of ResultSet. It should return a String object that is
   * been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject229() throws Exception {
		super.testSetObject229();
  }

  /*
   * @testName: testSetObject231
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Timestamp object for
   * SQL Type Time and call statement.executeQuery method and call getObject
   * method of ResultSet. It should return a String object that is been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject231() throws Exception {
		super.testSetObject231();
  }

  /*
   * @testName: testSetObject232
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:694;
   * JDBC:JAVADOC:695; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setObject(int
   * parameterIndex, Object x,int jdbcType) method to set Timestamp object for
   * SQL Type Timestamp and call statement.executeQuery method and call
   * getObject method of ResultSet. It should return a String object that is
   * been set.
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetObject232() throws Exception {
		super.testSetObject232();
  }

  /*
   * @testName: testRegisterOutParameter01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1237;
   * JDBC:JAVADOC:1238; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setBigDecimal(int
   * parameterIndex, int jdbcType) method to set maximum BigDecimal value in
   * null column and call registerOutParameter(int parameterIndex,int jdbcType,
   * int scale) method and call getBigDecimal method. It should return a
   * BigDecimal object that is been set. (Note: This test case also checks the
   * support for INOUT parameter in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter01() throws Exception {
		super.testRegisterOutParameter01();
  }

  /*
   * @testName: testRegisterOutParameter02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1237;
   * JDBC:JAVADOC:1238; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setBigDecimal(int
   * parameterIndex, int jdbcType) method to set minimum BigDecimal value in
   * maximum value column and call registerOutParameter(int parameterIndex,int
   * jdbcType,int scale) method and call getBigDecimal method. It should return
   * a BigDecimal object that is been set. (Note: This test case also checks the
   * support for INOUT parameter in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter02() throws Exception {
		super.testRegisterOutParameter02();
  }

  /*
   * @testName: testRegisterOutParameter03
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1237;
   * JDBC:JAVADOC:1238; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setBigDecimal(int
   * parameterIndex, int jdbcType) method to set maximum Decimal value in null
   * column and call registerOutParameter(int parameterIndex,int jdbcType, int
   * scale) method and call getBigDecimal method. It should return a BigDecimal
   * object that is been set. (Note: This test case also checks the support for
   * INOUT parameter in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter03() throws Exception {
		super.testRegisterOutParameter03();
  }

  /*
   * @testName: testRegisterOutParameter04
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1237;
   * JDBC:JAVADOC:1238; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setBigDecimal(int
   * parameterIndex, int jdbcType) method to set minimum Decimal value in
   * maximum value column in Decimal table and call registerOutParameter(int
   * parameterIndex,int jdbcType,int scale) method and call getBigDecimal
   * method. It should return a BigDecimal object that is been set. (Note: This
   * test case also checks the support for INOUT parameter in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter04() throws Exception {
		super.testRegisterOutParameter04();
  }

  /*
   * @testName: testRegisterOutParameter05
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1235;
   * JDBC:JAVADOC:1236; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setDouble(int
   * parameterIndex, int jdbcType) method to set maximum Double value in null
   * column and call registerOutParameter(int parameterIndex,int jdbcType)
   * method and call getDouble method. It should return a double value that is
   * been set. (Note: This test case also checks the support for INOUT parameter
   * in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter05() throws Exception {
		super.testRegisterOutParameter05();
  }

  /*
   * @testName: testRegisterOutParameter06
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1235;
   * JDBC:JAVADOC:1236; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setDouble(int
   * parameterIndex, int jdbcType) method to set minimum double value in maximum
   * value column in Double table and call registerOutParameter(int
   * parameterIndex,int jdbcType) method and call getDouble method. It should
   * return a double value that is been set. (Note: This test case also checks
   * the support for INOUT parameter in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter06() throws Exception {
		super.testRegisterOutParameter06();
  }

  /*
   * @testName: testRegisterOutParameter07
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1235;
   * JDBC:JAVADOC:1236; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setDouble(int
   * parameterIndex, int jdbcType) method to set maximum Float value in null
   * column and call registerOutParameter method and call getDouble method. It
   * should return a double value that is been set. (Note: This test case also
   * checks the support for INOUT parameter in Stored Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter07() throws Exception {
		super.testRegisterOutParameter07();
  }

  /*
   * @testName: testRegisterOutParameter08
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:1235;
   * JDBC:JAVADOC:1236; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. execute the stored procedure and call the setDouble() method to
   * set minimum float value in maximum value column in Float table and call
   * registerOutParameter(int parameterIndex,int jdbcType) method and call
   * getDouble method. It should return a double value that is been set. (Note:
   * This test case also checks the support for INOUT parameter in Stored
   * Procedure)
   *
   */
	@Test
	@TargetVehicle("appclient")
  public void testRegisterOutParameter08() throws Exception {
		super.testRegisterOutParameter08();
  }
}
