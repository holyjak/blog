package net.jakubholy.blog.genericmappers.xml;

import java.util.logging.Logger;

/**
 * Parse XML nodes at a particular XPath into POJO beans using JAXB.
 * <p>
 * The bean type must be annotated at least with
 * @XmlRootElement(name="(name of the corresponding XML element)") and optionally
 * also with other ones, if the XML element and Java property names differ or there
 * is a name-space or some special processing is needed. See examples in the code base
 * and the test class.
 * <p>
 * Thread-safe. Do cache and reuse it for its (lazy) initialization is expensive.
 *
 * <h3>Example</h3>
 * XML:
 * <pre>{@code
 *  <feed>
 *    <entry>
 *      <myAttr1>123</myAttr1>
 *      <myns:myAttr2Xml xmlns:myns='http://example.com'>myValue2</myns:myAttr2Xml>
 *    <entry>
 *    <entry>...</entry>
 *  </feed>
 * }</pre>
 * <p>
 * POJO:
 * <pre>{@code
 *  @XmlRootElement(name="entry")
 *  public class FeedEntry {
 *    public int myAttr1;
 *    // Element whose name in XML differs and is fully qualified with a name space
 *    @XmlElement(namespace="http://example.com", name="myAttr2Xml")
 *    public String myAttr2;
 *  }
 * }</pre>
 * <p>
 * Parsing code:
 * <pre>{@code
 *  // Little Groovy code;
 *  // Normally you'd cache an instance to do the expensive initialization just once
 *  def entries = new GenericXmlToPojoParser().parseFrom("/feed/entry", xml, FeedEntry.class);
 *  assert entries.size() == 2;
 *  def entry1 = entries.iterator().next();
 *  assert entry1.myAttr1 == 123 && "myValue2".equals(entry1.myAttr2);
 * }</pre>
 *
 * <h3>TO DO</h3>
 * <pre>
 * - add caching of JAXBContext, XPathExpression
 * - implement validation as suggested in the BeanValidatingUnmarshalListener
 * </pre>
 *
 * @see XmlElements
 *
 * @author jholy
 *
 */
public class GenericXmlToBeanParser {

    static final Logger log = Logger.getLogger(GenericXmlToBeanParser.class.getName());

    /**
     * Parse the given XML so that it can be mapped to POJO beans etc.
     * @param xml (required)
     * @return XmlElements that provide access to the content of the XML
     */
    public XmlElements parseXml(String xml) {
        return new XmlElements(xml);
    }

}