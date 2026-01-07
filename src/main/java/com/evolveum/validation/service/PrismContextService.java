package com.evolveum.validation.service;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.schema.MidPointPrismContextFactory;
import com.evolveum.midpoint.util.exception.SchemaException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Created by Dominik.
 */
@Service
public class PrismContextService {

    @Cacheable("prismContext")
    public PrismContext getPrismContext() throws SchemaException, IOException, SAXException {
        MidPointPrismContextFactory factory = new MidPointPrismContextFactory();
        PrismContext prismContext = factory.createPrismContext();
        prismContext.initialize();

        return prismContext;
    }
}
