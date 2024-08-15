package ee.jakarta.tck.persistence.jpa22.query.stream;

import ee.jakarta.tck.persistence.jpa22.query.stream.Client;
import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tck.arquillian.porting.lib.spi.TestArchiveProcessor;
import tck.arquillian.protocol.common.TargetVehicle;



@ExtendWith(ArquillianExtension.class)
@Tag("persistence")
@Tag("platform")
@Tag("web")
@Tag("tck-appclient")

public class ClientStateless3Test extends ee.jakarta.tck.persistence.jpa22.query.stream.Client {
    static final String VEHICLE_ARCHIVE = "jpa_jpa22_query_stream_stateless3_vehicle";

        /**
        EE10 Deployment Descriptors:
        jpa_jpa22_query_stream: META-INF/persistence.xml
        jpa_jpa22_query_stream_appmanaged_vehicle_client: META-INF/application-client.xml
        jpa_jpa22_query_stream_appmanaged_vehicle_ejb: jar.sun-ejb-jar.xml
        jpa_jpa22_query_stream_appmanagedNoTx_vehicle_client: META-INF/application-client.xml
        jpa_jpa22_query_stream_appmanagedNoTx_vehicle_ejb: jar.sun-ejb-jar.xml
        jpa_jpa22_query_stream_pmservlet_vehicle_web: WEB-INF/web.xml
        jpa_jpa22_query_stream_puservlet_vehicle_web: WEB-INF/web.xml
        jpa_jpa22_query_stream_stateful3_vehicle_client: META-INF/application-client.xml
        jpa_jpa22_query_stream_stateful3_vehicle_ejb: jar.sun-ejb-jar.xml
        jpa_jpa22_query_stream_stateless3_vehicle_client: META-INF/application-client.xml
        jpa_jpa22_query_stream_stateless3_vehicle_ejb: jar.sun-ejb-jar.xml
        jpa_jpa22_query_stream_vehicles: 

        Found Descriptors:
        Client:

        /com/sun/ts/tests/common/vehicle/stateless3/stateless3_vehicle_client.xml
        Ejb:

        Ear:

        */
        @TargetsContainer("tck-appclient")
        @OverProtocol("appclient")
        @Deployment(name = VEHICLE_ARCHIVE, order = 2)
        public static EnterpriseArchive createDeploymentVehicle(@ArquillianResource TestArchiveProcessor archiveProcessor) {
        // Client
            // the jar with the correct archive name
            JavaArchive jpa_jpa22_query_stream_stateless3_vehicle_client = ShrinkWrap.create(JavaArchive.class, "jpa_jpa22_query_stream_stateless3_vehicle_client.jar");
            // The class files
            jpa_jpa22_query_stream_stateless3_vehicle_client.addClasses(
            com.sun.ts.tests.common.vehicle.VehicleRunnerFactory.class,
            com.sun.ts.tests.common.vehicle.ejb3share.UseEntityManager.class,
            com.sun.ts.tests.common.vehicle.ejb3share.EJB3ShareIF.class,
            com.sun.ts.lib.harness.EETest.Fault.class,
            com.sun.ts.tests.common.vehicle.ejb3share.UseEntityManagerFactory.class,
            com.sun.ts.tests.common.vehicle.EmptyVehicleRunner.class,
            ee.jakarta.tck.persistence.common.PMClientBase.class,
            com.sun.ts.tests.common.vehicle.VehicleRunnable.class,
            com.sun.ts.tests.common.vehicle.ejb3share.UserTransactionWrapper.class,
            com.sun.ts.tests.common.vehicle.stateless3.Stateless3VehicleIF.class,
            com.sun.ts.lib.harness.EETest.class,
            com.sun.ts.tests.common.vehicle.stateless3.Stateless3VehicleRunner.class,
            com.sun.ts.lib.harness.ServiceEETest.class,
            com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper.class,
            com.sun.ts.lib.harness.EETest.SetupException.class,
            com.sun.ts.tests.common.vehicle.VehicleClient.class,
            com.sun.ts.tests.common.vehicle.ejb3share.NoopTransactionWrapper.class
            );
            // The application-client.xml descriptor
            URL resURL = Client.class.getResource("/com/sun/ts/tests/common/vehicle/stateless3/stateless3_vehicle_client.xml");
            if(resURL != null) {
              jpa_jpa22_query_stream_stateless3_vehicle_client.addAsManifestResource(resURL, "application-client.xml");
            }
            // The sun-application-client.xml file need to be added or should this be in in the vendor Arquillian extension?
            resURL = Client.class.getResource("//com/sun/ts/tests/common/vehicle/stateless3/stateless3_vehicle_client.jar.sun-application-client.xml");
            if(resURL != null) {
              jpa_jpa22_query_stream_stateless3_vehicle_client.addAsManifestResource(resURL, "application-client.xml");
            }
            jpa_jpa22_query_stream_stateless3_vehicle_client.addAsManifestResource(new StringAsset("Main-Class: " + Client.class.getName() + "\n"), "MANIFEST.MF");
            archiveProcessor.processClientArchive(jpa_jpa22_query_stream_stateless3_vehicle_client, Client.class, resURL);


        // Ejb
            // the jar with the correct archive name
            JavaArchive jpa_jpa22_query_stream_stateless3_vehicle_ejb = ShrinkWrap.create(JavaArchive.class, "jpa_jpa22_query_stream_stateless3_vehicle_ejb.jar");
            // The class files
            jpa_jpa22_query_stream_stateless3_vehicle_ejb.addClasses(
                com.sun.ts.tests.common.vehicle.ejb3share.EJB3ShareBaseBean.class,
                com.sun.ts.tests.common.vehicle.VehicleRunnerFactory.class,
                com.sun.ts.tests.common.vehicle.ejb3share.UseEntityManager.class,
                com.sun.ts.tests.common.vehicle.ejb3share.EJB3ShareIF.class,
                com.sun.ts.lib.harness.EETest.Fault.class,
                com.sun.ts.tests.common.vehicle.ejb3share.UseEntityManagerFactory.class,
                ee.jakarta.tck.persistence.common.PMClientBase.class,
                com.sun.ts.tests.common.vehicle.VehicleRunnable.class,
                ee.jakarta.tck.persistence.jpa22.query.stream.Client.ObjectCounter.class,
                com.sun.ts.tests.common.vehicle.ejb3share.UserTransactionWrapper.class,
                com.sun.ts.tests.common.vehicle.stateless3.Stateless3VehicleBean.class,
                com.sun.ts.tests.common.vehicle.stateless3.Stateless3VehicleIF.class,
                com.sun.ts.lib.harness.EETest.class,
                com.sun.ts.lib.harness.ServiceEETest.class,
                com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper.class,
                ee.jakarta.tck.persistence.jpa22.query.stream.Client.class,
                com.sun.ts.lib.harness.EETest.SetupException.class,
                com.sun.ts.tests.common.vehicle.VehicleClient.class,
                com.sun.ts.tests.common.vehicle.ejb3share.NoopTransactionWrapper.class
            );
            // The ejb-jar.xml descriptor
            URL ejbResURL = Client.class.getResource("//vehicle/stateless3/stateless3_vehicle_ejb.xml");
            if(ejbResURL != null) {
              jpa_jpa22_query_stream_stateless3_vehicle_ejb.addAsManifestResource(ejbResURL, "ejb-jar.xml");
            }
            // The sun-ejb-jar.xml file
            ejbResURL = Client.class.getResource("//vehicle/stateless3/stateless3_vehicle_ejb.jar.sun-ejb-jar.xml");
            if(ejbResURL != null) {
              jpa_jpa22_query_stream_stateless3_vehicle_ejb.addAsManifestResource(ejbResURL, "sun-ejb-jar.xml");
            }
            archiveProcessor.processEjbArchive(jpa_jpa22_query_stream_stateless3_vehicle_ejb, Client.class, ejbResURL);

        // Par
            // the jar with the correct archive name
            JavaArchive jpa_jpa22_query_stream = ShrinkWrap.create(JavaArchive.class, "jpa_jpa22_query_stream.jar");
            // The class files
            jpa_jpa22_query_stream.addClasses(
                ee.jakarta.tck.persistence.jpa22.query.stream.Employee.class,
                ee.jakarta.tck.persistence.jpa22.query.stream.Insurance.class,
                ee.jakarta.tck.persistence.jpa22.query.stream.Department.class
            );
            // The persistence.xml descriptor
            URL parURL = Client.class.getResource("persistence.xml");
            if(parURL != null) {
              jpa_jpa22_query_stream.addAsManifestResource(parURL, "persistence.xml");
            }
            archiveProcessor.processParArchive(jpa_jpa22_query_stream, Client.class, parURL);
            // The orm.xml file
            parURL = Client.class.getResource("orm.xml");
            if(parURL != null) {
              jpa_jpa22_query_stream.addAsManifestResource(parURL, "orm.xml");
            }

        // Ear
            EnterpriseArchive jpa_jpa22_query_stream_vehicles_ear = ShrinkWrap.create(EnterpriseArchive.class, "jpa_jpa22_query_stream_vehicles.ear");

            // Any libraries added to the ear

            // The component jars built by the package target
            jpa_jpa22_query_stream_vehicles_ear.addAsModule(jpa_jpa22_query_stream_stateless3_vehicle_ejb);
            jpa_jpa22_query_stream_vehicles_ear.addAsModule(jpa_jpa22_query_stream_stateless3_vehicle_client);

            jpa_jpa22_query_stream_vehicles_ear.addAsLibrary(jpa_jpa22_query_stream);



            // The application.xml descriptor
            URL earResURL = Client.class.getResource("/com/sun/ts/tests/jpa/jpa22/query/stream/");
            if(earResURL != null) {
              jpa_jpa22_query_stream_vehicles_ear.addAsManifestResource(earResURL, "application.xml");
            }
            // The sun-application.xml descriptor
            earResURL = Client.class.getResource("/com/sun/ts/tests/jpa/jpa22/query/stream/.ear.sun-application.xml");
            if(earResURL != null) {
              jpa_jpa22_query_stream_vehicles_ear.addAsManifestResource(earResURL, "sun-application.xml");
            }
            archiveProcessor.processEarArchive(jpa_jpa22_query_stream_vehicles_ear, Client.class, earResURL);
        return jpa_jpa22_query_stream_vehicles_ear;
        }

        @Test
        @Override
        @TargetVehicle("stateless3")
        public void getResultStreamTest() throws java.lang.Exception {
            super.getResultStreamTest();
        }

        @Test
        @Override
        @TargetVehicle("stateless3")
        public void getTypedResultStreamTest() throws java.lang.Exception {
            super.getTypedResultStreamTest();
        }


}