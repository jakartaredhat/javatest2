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
 * @(#)callStmtClient7.java	1.16 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.callStmt.callStmt7;

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
 * The callStmtClient7 class tests methods of CallableStatement interface (to
 * check the Support for IN, OUT and INOUT parameters of Stored Procedure) using
 * Sun's J2EE Reference Implementation.
 * 
 * @author
 * @version 1.7, 06/16/99
 */

public class callStmtClient7AppClient extends callStmtClient7 implements Serializable {
	
	 @TargetsContainer("tck-javatest")
	  @OverProtocol("appclient")
	@Deployment(name = "appclient", order = 2)
	public static JavaArchive createDeploymentAppclient(@ArquillianResource TestArchiveProcessor archiveProcessor) throws IOException {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "callStmt7_appclient_vehicle_client.jar");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addPackages(true, "com.sun.ts.tests.common.vehicle");
		archive.addPackages(true, "com.sun.ts.lib.harness");
		archive.addClasses(callStmtClient7AppClient.class, callStmtClient7.class);
		  // The appclient-client descriptor
	     URL appClientUrl = callStmtClient7AppClient.class.getResource("appclient_vehicle_client.xml");
	     if(appClientUrl != null) {
	     	archive.addAsManifestResource(appClientUrl, "application-client.xml");
	     }
	     // The sun appclient-client descriptor
	     URL sunAppClientUrl = callStmtClient7AppClient.class.getResource("callStmt7_appclient_vehicle_client.jar.sun-application-client.xml");
	     if(sunAppClientUrl != null) {
	     	archive.addAsManifestResource(sunAppClientUrl, "sun-application-client.xml");
	     }
	     // Call the archive processor
	     archiveProcessor.processClientArchive(archive, callStmtClient7AppClient.class, sunAppClientUrl);
		System.out.println(archive.toString(true));
		return archive;
	};

  private static final String testName = "jdbc.ee.callStmt.callStmt7";

  /* Run test in standalone mode */
  public static void main(String[] args) {
    callStmtClient7AppClient theTests = new callStmtClient7AppClient();
    Status s = theTests.run(args, System.out, System.err);
    s.exit();
  }


  /*
   * @testName: testSetFloat01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:668;
   * JDBC:JAVADOC:669; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using setFloat(int parameterIndex,float x),update the column the
   * minimum value of Float_Tab. Now execute a query to get the minimum value
   * and retrieve the result of the query using the getFloat(int columnIndex)
   * method.Compare the returned value, with the minimum value extracted from
   * the tssql.stmt file. Both of them should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetFloat01() throws Exception {
		super.testSetFloat01();
  }

  /*
   * @testName: testSetFloat02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:668;
   * JDBC:JAVADOC:669; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using setFloat(int parameterIndex,float x),update the column the
   * maximum value of Float_Tab. Now execute a query to get the maximum value
   * and retrieve the result of the query using the getFloat(int columnIndex)
   * method.Compare the returned value, with the maximum value extracted from
   * the tssql.stmt file. Both of them should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetFloat02() throws Exception {
		super.testSetFloat02();
  }

  /*
   * @testName: testSetDouble01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:670;
   * JDBC:JAVADOC:671; JDBC:JAVADOC:386; JDBC:JAVADOC:387; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using setDouble(int parameterIndex,double x),update the column
   * the minimum value of Double_Tab. Now execute a query to get the minimum
   * value and retrieve the result of the query using the getDouble(int
   * columnIndex) method.Compare the returned value, with the minimum value
   * extracted from the tssql.stmt file. Both of them should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetDouble01() throws Exception {
		super.testSetDouble01();
  }

  /*
   * @testName: testSetDouble02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:670;
   * JDBC:JAVADOC:671; JDBC:JAVADOC:386; JDBC:JAVADOC:387; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using setDouble(int parameterIndex,double x),update the column
   * the maximum value of Double_Tab. Now execute a query to get the maximum
   * value and retrieve the result of the query using the getDouble(int
   * columnIndex) method.Compare the returned value, with the maximum value
   * extracted from the tssql.stmt file. Both of them should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetDouble02() throws Exception {
		super.testSetDouble02();
  }

  /*
   * @testName: testSetBytes01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:676;
   * JDBC:JAVADOC:677; JavaEE:SPEC:186;
   * 
   * @test_Strategy: This test case is meant for checking the support for IN
   * parameter in CallableStatement Interface. Get a CallableStatement object
   * from the connection to the database. Using the IN parameter of that
   * object,update the column Binary_Val of Binary_Tab with a byte array.Execute
   * a query to get the byte array and retrieve the result of the query using
   * the getBytes(int parameterIndex) method.It should return the byte array
   * object that has been set.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetBytes01() throws Exception {
		super.testSetBytes01();
  }

  /*
   * @testName: testSetBytes02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:676;
   * JDBC:JAVADOC:677; JavaEE:SPEC:186;
   *
   * @test_Strategy: This test case is meant for checking the support for IN
   * parameter in CallableStatement Interface. Get a CallableStatement object
   * from the connection to the database Using the IN parameter of that
   * object,update the column Varbinary_Val of Varbinary_Tab with a byte
   * array.Execute a query to get the byte array and retrieve the result of the
   * query using the getBytes(int parameterIndex) method.It should return the
   * byte array object that has been set.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetBytes02() throws Exception {
		super.testSetBytes02();
  }

  /*
   * @testName: testSetDate01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:678;
   * JDBC:JAVADOC:679; JDBC:JAVADOC:392; JDBC:JAVADOC:393; JavaEE:SPEC:186;
   *
   * @test_Strategy: This test case is meant for checking the support for IN
   * parameter in CallableStatement Interface. Get a CallableStatement object
   * from the connection to the database. Using the IN parameter of that
   * object,update the column Mfg_Date of Date_Tab with the null value.Execute a
   * query to get the null value and retrieve the result of the query using the
   * getDate(int parameterIndex) method.Check if it is null.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetDate01() throws Exception {
		super.testSetDate01();
  }

  /*
   * @testName: testSetDate02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:678;
   * JDBC:JAVADOC:679; JDBC:JAVADOC:392; JDBC:JAVADOC:393; JavaEE:SPEC:186;
   *
   * @test_Strategy: This test case is meant for checking the support for IN
   * parameter in CallableStatement Interface. Get a CallableStatement object
   * from the connection to the database. Using the IN parameter of that
   * object,update the column Null_Val of Date_Tab with a non null Date value
   * extracted from tssql.stmt file Execute a query to get the non null Date
   * value and retrieve the result of the query using the getDate(int
   * parameterIndex) method. Compare the returned value with the value extracted
   * from tssql.stmt file. Both of them should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetDate02() throws Exception {
		super.testSetDate02();
  }

  /*
   * @testName: testSetDate03
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:678;
   * JDBC:JAVADOC:679; JDBC:JAVADOC:612; JDBC:JAVADOC:613;JavaEE:SPEC:186;
   *
   * @test_Strategy: This test case is meant for checking the support for IN
   * parameter in CallableStatement Interface. Get a CallableStatement object
   * from the connection to the database. Using the IN parameter of that
   * object,update the column Mfg_Date of Date_Tab with the null value.Execute a
   * query to get the null value and retrieve the result of the query using the
   * getDate(int parameterIndex,Calender cal) method.Check if it is null.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetDate03() throws Exception {
		super.testSetDate03();
  }

  /*
   * @testName: testSetDate04
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:714;
   * JDBC:JAVADOC:715; JDBC:JAVADOC:612; JDBC:JAVADOC:613; JavaEE:SPEC:186;
   *
   * @test_Strategy: This test case is meant for checking the support for IN
   * parameter in CallableStatement Interface. Get a CallableStatement object
   * from the connection to the database Using the IN parameter of that
   * object,update the column Null_Val of Date_Tab with a non null Date value
   * extracted from tssql.stmt file. Execute a query to get the non null Date
   * value and retrieve the result of the query using the getDate(int
   * parameterIndex,Calender cal) method.Compare the returned value with the
   * value extracted from tssql.stmt file.Both of them should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetDate04() throws Exception {
		super.testSetDate04();
  }

  /*
   * @testName: testSetTime01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:680;
   * JDBC:JAVADOC:681; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using the setTime(int parameterIndex, Time x) method,update the
   * column value with the Non-Null Time value. Call the getTime(int columnno)
   * method to retrieve this value. Extract the Time value from the tssql.stmt
   * file. Compare this value with the value returned by the getTime(int
   * columnno) method. Both the values should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetTime01() throws Exception {
		super.testSetTime01();
  }

  /*
   * @testName: testSetTime02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:716;
   * JDBC:JAVADOC:717; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using the setTime(int parameterIndex, Time x, Calendar cal)
   * method,update the column value with the Non-Null Time value using the
   * Calendar Object. Call the getTime(int columnno) method to retrieve this
   * value. Extract the Time value from the tssql.stmt file. Compare this value
   * with the value returned by the getTime(int columnno) method. Both the
   * values should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetTime02() throws Exception {
		super.testSetTime02();
  }

  /*
   * @testName: testSetTimestamp01
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:682;
   * JDBC:JAVADOC:683; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using the setTimestamp(int parameterIndex, Timestamp x)
   * method,update the column value with the Non-Null Timestamp value. Call the
   * getTimestamp(int columnno) method to retrieve this value. Extract the
   * Timestamp value from the tssql.stmt file. Compare this value with the value
   * returned by the getTimestamp(int columnno) method. Both the values should
   * be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetTimestamp01() throws Exception {
		super.testSetTimestamp01();
  }

  /*
   * @testName: testSetTimestamp02
   * 
   * @assertion_ids: JDBC:SPEC:9; JDBC:SPEC:10; JDBC:JAVADOC:718;
   * JDBC:JAVADOC:719; JavaEE:SPEC:186;
   *
   * @test_Strategy: Get a CallableStatement object from the connection to the
   * database. Using the setTimestamp(int parameterIndex, Time x, Calendar cal)
   * method,update the column value with the Non-Null Timestamp value using the
   * Calendar Object. Call the getTimestamp(int columnno) method to retrieve
   * this value. Extract the Timestamp value from the tssql.stmt file. Compare
   * this value with the value returned by the getTimestamp(int columnno)
   * method. Both the values should be equal.
   */
	@Test
	@TargetVehicle("appclient")
  public void testSetTimestamp02() throws Exception {
		super.testSetTimestamp02();
  }

}
