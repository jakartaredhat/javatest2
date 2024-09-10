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
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.harness.Status;

import tck.arquillian.porting.lib.spi.TestArchiveProcessor;
import tck.arquillian.protocol.common.TargetVehicle;


// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The dbMetaClient6 class tests methods of DatabaseMetaData interface using
 * Sun's J2EE Reference Implementation.
 * 
 * @author
 * @version 1.7, 06/16/99
 */

public class dbMetaClient6EJB extends dbMetaClient6 implements Serializable {
  private static final String testName = "jdbc.ee.dbMeta.dbMeta6";
  
  @TargetsContainer("tck-javatest")
  @OverProtocol("appclient")
	@Deployment(name = "ejb",  order = 2)
	public static EnterpriseArchive createDeploymentejb(@ArquillianResource TestArchiveProcessor archiveProcessor) throws IOException {
		JavaArchive ejbClient = ShrinkWrap.create(JavaArchive.class, "dbMeta6_ejb_vehicle_client.jar");
		ejbClient.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		ejbClient.addPackages(true, "com.sun.ts.tests.common.vehicle");
		ejbClient.addPackages(true, "com.sun.ts.lib.harness");
		URL resURL = dbMetaClient6EJB.class.getResource("com/sun/ts/tests/common/vehicle/ejb/ejb_vehicle_client.xml");
		if (resURL != null) {
			ejbClient.addAsManifestResource(resURL, "application-client.xml");
		}
		ejbClient.addAsManifestResource(new StringAsset("Main-Class: com.sun.ts.tests.common.vehicle.VehicleClient\n"),
				"MANIFEST.MF");

		JavaArchive ejb = ShrinkWrap.create(JavaArchive.class, "dbMeta6_ejb_vehicle_ejb.jar");
		ejb.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		ejb.addPackages(true, "com.sun.ts.tests.common.vehicle");
		ejb.addPackages(true, "com.sun.ts.lib.harness");
		ejb.addClasses(dbMetaClient6EJB.class, dbMetaClient6.class);

		ejb.addAsManifestResource(dbMetaClient6EJB.class.getPackage(), "ejb_vehicle_ejb.xml");

		EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "dbMeta6_ejb_vehicle.ear");
		ear.addAsModule(ejbClient);
		ear.addAsModule(ejb);
		return ear;
	};



  /* Run test in standalone mode */
  public static void main(String[] args) {
    dbMetaClient6EJB theTests = new dbMetaClient6EJB();
    Status s = theTests.run(args, System.out, System.err);
    s.exit();
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
	@TargetVehicle("ejb")
  public void testGetCatalogSeparator() throws Exception {
		super.testGetCatalogSeparator();
  }

  /*
   * @testName: testSupportsSchemasInDataManipulation
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:936; JDBC:JAVADOC:937;
   * JavaEE:SPEC:193;
   * 
   * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
   * DataBase. Make a call to
   * DatabaseMetadata.supportsSchemasInDataManipulation() on that object. It
   * should return a boolean value either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsSchemasInDataManipulation() throws Exception {
		super.testSupportsSchemasInDataManipulation();
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
	@TargetVehicle("ejb")
  public void testSupportsSchemasInProcedureCalls() throws Exception {
		super.testSupportsSchemasInProcedureCalls();
  }

  /*
   * @testName: testSupportsSchemasInTableDefinitions
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:940; JDBC:JAVADOC:941;
   * JavaEE:SPEC:193;
   * 
   * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
   * DataBase. Make a call to
   * DatabaseMetadata.supportsSchemasInTableDefinitions() on that object.It
   * should return a boolean value; either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsSchemasInTableDefinitions() throws Exception {
		super.testSupportsSchemasInTableDefinitions();
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
	@TargetVehicle("ejb")
  public void testSupportsSchemasInIndexDefinitions() throws Exception {
		super.testSupportsSchemasInIndexDefinitions();
  }

  /*
   * @testName: testSupportsSchemasInPrivilegeDefinitions
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:944; JDBC:JAVADOC:945;
   * JavaEE:SPEC:193;
   *
   * @test_Strategy: Get the DataBaseMetaData object from the Connection to the
   * DataBase. Call to supportsSchemasInPrivilegeDefinitions() on that object.
   * It should return a boolean value; either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsSchemasInPrivilegeDefinitions() throws Exception {
		super.testSupportsSchemasInPrivilegeDefinitions();
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
	@TargetVehicle("ejb")
  public void testSupportsCatalogsInDataManipulation() throws Exception {
		super.testSupportsCatalogsInDataManipulation();
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
	@TargetVehicle("ejb")
  public void testSupportsCatalogsInProcedureCalls() throws Exception {
		super.testSupportsCatalogsInProcedureCalls();
  }

  /*
   * @testName: testSupportsCatalogsInTableDefinitions
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:950; JDBC:JAVADOC:951;
   * JavaEE:SPEC:193;
   *
   * @test_Strategy: Get a DatabaseMetadata object from the connection to the
   * database Call the supportsCatalogsInTableDefinitions() method on that
   * object. It should return a boolean value; either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsCatalogsInTableDefinitions() throws Exception {
		super.testSupportsCatalogsInTableDefinitions();
  }

  /*
   * @testName: testSupportsCatalogsInIndexDefinitions
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:952; JDBC:JAVADOC:953;
   * JavaEE:SPEC:193;
   *
   * @test_Strategy: Get a DatabaseMetadata object from the connection to the
   * database Call the supportsCatalogsInIndexDefinitions() method on that
   * object. It should return a boolean value; either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsCatalogsInIndexDefinitions() throws Exception {
		super.testSupportsCatalogsInIndexDefinitions();
  }

  /*
   * @testName: testSupportsCatalogsInPrivilegeDefinitions
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:954; JDBC:JAVADOC:955;
   * JavaEE:SPEC:193;
   *
   * @test_Strategy: Get a DatabaseMetadata object from the connection to the
   * database and call the supportsCatalogsInPrivilegeDefinitions() method on
   * that object. It should return a boolean value; either true or false
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsCatalogsInPrivilegeDefinitions() throws Exception {
		super.testSupportsCatalogsInPrivilegeDefinitions();
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
	@TargetVehicle("ejb")
  public void testSupportsPositionedDelete() throws Exception {
		super.testSupportsPositionedDelete();
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
	@TargetVehicle("ejb")
  public void testSupportsPositionedUpdate() throws Exception {
		super.testSupportsPositionedUpdate();
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
	@TargetVehicle("ejb")
  public void testSupportsSelectForUpdate() throws Exception {
		super.testSupportsSelectForUpdate();
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
	@TargetVehicle("ejb")
  public void testSupportsSubqueriesInComparisons() throws Exception {
		super.testSupportsSubqueriesInComparisons();
  }

  /*
   * @testName: testSupportsSubqueriesInExists
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:966; JDBC:JAVADOC:967;
   * JavaEE:SPEC:193;
   *
   * @test_Strategy: Get a DatabaseMetadata object from the connection to the
   * database and call the supportsSubqueriesInExists() method on that object.
   * It should return a boolean value; either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsSubqueriesInExists() throws Exception {
		super.testSupportsSubqueriesInExists();
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
	@TargetVehicle("ejb")
  public void testSupportsSubqueriesInIns() throws Exception {
		super.testSupportsSubqueriesInIns();
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
	@TargetVehicle("ejb")
  public void testSupportsSubqueriesInQuantifieds() throws Exception {
		super.testSupportsSubqueriesInQuantifieds();
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
	@TargetVehicle("ejb")
  public void testSupportsCorrelatedSubqueries() throws Exception {
		super.testSupportsCorrelatedSubqueries();
  }

  /*
   * @testName: testSupportsUnion
   * 
   * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:974; JDBC:JAVADOC:975;
   * JavaEE:SPEC:193;
   *
   * @test_Strategy: Get a DatabaseMetadata object from the connection to the
   * database and call the supportsUnion() method on that object. It should
   * return a boolean value; either true or false.
   *
   */
	@Test
	@TargetVehicle("ejb")
  public void testSupportsUnion() throws Exception {
		super.testSupportsUnion();
  }

}
