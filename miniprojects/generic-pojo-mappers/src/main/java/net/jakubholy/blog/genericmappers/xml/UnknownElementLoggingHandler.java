package net.jakubholy.blog.genericmappers.xml;

import static java.util.logging.Level.FINE;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;

import org.w3c.dom.Node;

/**
 * Log unknown elements (elements that have no corresponding property on the bean).
 * <p>
 * Notice that it can easily happen that JAXB doesn't manage to map an XML element
 * with a Java property if name spaces are involved as JAXB requires
 * that they are declared correctly via {@link XmlElement#namespace()},
 * {@link XmlSchema#namespace()} (likely plus {@link XmlSchema#attributeFormDefault()})
 * and/or {@link XmlRootElement#namespace()}. JAXB is rather strict, unfortunately.
 */
class UnknownElementLoggingHandler implements ValidationEventHandler {
    @Override
    public boolean handleEvent(ValidationEvent event) {
        if (GenericXmlToBeanParser.log.isLoggable(FINE)) {
            Node node = event.getLocator().getNode();
            String nodeInfo = "(uri: " + node.getNamespaceURI() +
                    ", local: " + node.getLocalName() + ")";
            GenericXmlToBeanParser.log.fine("Unknown element: " + nodeInfo + " at " + event.getLocator());
        }
        return true;
    }
}