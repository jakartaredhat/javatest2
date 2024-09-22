package org.glassfish.arquillian.porting;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import tck.arquillian.porting.lib.spi.AbstractTestArchiveProcessor;

import java.net.URL;
import java.util.logging.Logger;

public class GlassfishXmlProcessor extends AbstractTestArchiveProcessor {
    static Logger log = Logger.getLogger(GlassfishXmlProcessor.class.getName());


    /**
     * Called on completion of the Arquillian configuration.
     */
    public void initalize(@Observes ArquillianDescriptor descriptor) {
        // Must call to setup the ResourceProvider
        super.initalize(descriptor);
        log.info("Initialized GlassfishXmlProcessor");
    }


    @Override
    public void processClientArchive(JavaArchive javaArchive, Class<?> aClass, URL url) {

    }

    @Override
    public void processEjbArchive(JavaArchive javaArchive, Class<?> aClass, URL url) {

    }

    @Override
    public void processWebArchive(WebArchive webArchive, Class<?> aClass, URL url) {

    }

    @Override
    public void processRarArchive(JavaArchive javaArchive, Class<?> aClass, URL url) {

    }

    @Override
    public void processParArchive(JavaArchive javaArchive, Class<?> aClass, URL url) {

    }

    @Override
    public void processEarArchive(EnterpriseArchive enterpriseArchive, Class<?> aClass, URL url) {

    }
}
