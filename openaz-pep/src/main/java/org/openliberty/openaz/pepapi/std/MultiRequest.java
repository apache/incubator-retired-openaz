package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.RequestReference;
import com.att.research.xacml.std.StdMutableRequest;
import com.att.research.xacml.std.StdMutableRequestReference;
import com.att.research.xacml.std.StdRequestAttributesReference;
import org.openliberty.openaz.pepapi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
final class MultiRequest implements PepRequest {

    private static final String REQUEST_ATTR_ID_PREFIX = "attributes";

    private final Map<Identifier, PepRequestAttributes> pepRequestAttributesMapByCategory;

    private final MapperRegistry mapperRegistry;

    private final PepConfig pepConfig;

    private final Object[] sharedRequestObjects;

    private List<?> associations;

    private final AtomicInteger idCounter;

    private final StdMutableRequest wrappedRequest;

    private StdMutableRequestReference currentRequestReference;

    private RequestReference sharedRequestReference;

    static MultiRequest newInstance(PepConfig pepConfig, MapperRegistry mapperRegistry, List<?> associations, Object[] sharedRequestObjects) {
        MultiRequest m = new MultiRequest(pepConfig, mapperRegistry, associations, sharedRequestObjects);
        m.mapSharedRequestObjects();
        m.mapAssociations();
        return m;
    }

    private MultiRequest(PepConfig pepConfig, MapperRegistry mapperRegistry, List<?> associations, Object[] sharedRequestObjects) {
        this.pepRequestAttributesMapByCategory = new HashMap<Identifier, PepRequestAttributes>();
        this.sharedRequestObjects = sharedRequestObjects;
        this.associations = associations;
        this.mapperRegistry = mapperRegistry;
        this.pepConfig = pepConfig;
        this.idCounter = new AtomicInteger(1);
        this.wrappedRequest = new StdMutableRequest();
        this.currentRequestReference = new StdMutableRequestReference();
    }

    private void mapSharedRequestObjects() {
        if(sharedRequestObjects == null) {
            throw new IllegalArgumentException("One or more arguments are null");
        }
        for(Object o: sharedRequestObjects) {
            if(o == null) {
                throw new IllegalArgumentException("One or more arguments are null");
            }
            ObjectMapper mapper = mapperRegistry.getMapper(o.getClass());
            if(mapper == null) {
                throw new IllegalArgumentException("No mappers found for class: " + o.getClass().getName());
            }
            mapper.map(o, this);
        }
        //Collect
        sharedRequestReference = currentRequestReference;
    }

    private void mapAssociations() {
        if(associations == null) {
            throw new IllegalArgumentException("One or more arguments are null");
        }
        for(Object association: associations) {
            if(association == null) {
                throw new IllegalArgumentException("One or more arguments are null");
            }

            //Prepare
            pepRequestAttributesMapByCategory.clear();
            currentRequestReference = new StdMutableRequestReference(sharedRequestReference.getAttributesReferences());
            wrappedRequest.add(currentRequestReference);

            //Map
            ObjectMapper mapper = mapperRegistry.getMapper(association.getClass());
            if(mapper == null) {
                throw new IllegalArgumentException("No mappers found for class: " + association.getClass().getName());
            }
            mapper.map(association, this);
        }
    }

    @Override
    public PepRequestAttributes getPepRequestAttributes(Identifier categoryIdentifier) {
        PepRequestAttributes pepRequestAttributes = pepRequestAttributesMapByCategory.get(categoryIdentifier);
        if(pepRequestAttributes == null) {
            String xmlId = generateRequestAttributesXmlId();
            StdPepRequestAttributes p = new StdPepRequestAttributes(xmlId, categoryIdentifier);
            p.setIssuer(pepConfig.getIssuer());
            pepRequestAttributes = p;
            pepRequestAttributesMapByCategory.put(categoryIdentifier, pepRequestAttributes);
            wrappedRequest.add(pepRequestAttributes.getWrappedRequestAttributes());
            currentRequestReference.add(new StdRequestAttributesReference(xmlId));
        }
        return pepRequestAttributes;
    }

    private String generateRequestAttributesXmlId() {
        return REQUEST_ATTR_ID_PREFIX + idCounter.getAndIncrement();
    }

    @Override
    public Request getWrappedRequest() {
        return wrappedRequest;
    }
}
