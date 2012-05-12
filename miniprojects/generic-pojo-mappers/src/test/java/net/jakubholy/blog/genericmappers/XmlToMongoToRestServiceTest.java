package net.jakubholy.blog.genericmappers;

import static com.sun.jersey.api.client.ClientResponse.Status.OK;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.jakubholy.blog.genericmappers.mongo.MongoStorage;
import net.jakubholy.blog.genericmappers.rest.TestMongoStorageProvider;
import net.jakubholy.blog.genericmappers.xml.GenericXmlToBeanParser;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

/**
 * Test it all: Parse a XML, store the data in Mongo DB, and serve it via a REST
 * service.
 * <p>
 * IMPORTANT: Requires MongoDB running at localhost at the default port
 */
public class XmlToMongoToRestServiceTest extends JerseyTest {

    private static final String COLLECTION_NAME = "entries";
    private MongoStorage storage;

    /**
     * Set up an embedded server running our Jersey REST web service
     * so that a test can call it.
     */
    public XmlToMongoToRestServiceTest() {
        super(new WebAppDescriptor.Builder()
          .contextPath("/")
          .initParam("com.sun.jersey.config.property.packages", "net.jakubholy.blog.genericmappers.rest")
          .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true") // Enable to get POJO mapped to JSON automatically
          .initParam("com.sun.jersey.config.feature.Trace", "true")
        //.initParam("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter")
        .build());
    }

    @Before
    public void setUpMongo() throws UnknownHostException {
        storage = new MongoStorage(new Mongo( "127.0.0.1" ));
        TestMongoStorageProvider.instance = storage;
        try {
            storage.dropDatabase();
        } catch (MongoException.Network e) {
            e.printStackTrace();
            fail("Failed to communicate with MongoDB, make sure that it is running at localhost " +
            		"at the default port. See System.err for details. E.: " + e.getMessage() +
            		", cause: " + e.getCause());
        }
    }

    @Test
    public void doAll() throws Exception {

        final long start = currentTimeMillis();

        final MyEntryPojo entryPojo = transformXmlToJavaBean();
        storeJavaBeanToMongoDb(entryPojo);
        fetchDocumentFromMongoViaRest();

        System.out.println(format(">>> All done in %d ms", currentTimeMillis() - start));
    }

    private MyEntryPojo transformXmlToJavaBean() {
        String xml = "<feed><entry>" +
                "<scalarAttribute>scalarAttrValue_XY</scalarAttribute>" +
               "<myId>id123</myId>" +
                "</entry></feed>";

        final long parsingStart = currentTimeMillis();

        Collection<MyEntryPojo> entries = new GenericXmlToBeanParser()
            .parseXml(xml)
            .atXPath("/feed/entry")
            .getBeans(MyEntryPojo.class);

        System.out.println(format(">>> XML->POJO done in %d ms", currentTimeMillis() - parsingStart));

        assertEquals(1, entries.size());
        final MyEntryPojo entryPojo = entries.iterator().next();
        assertEquals("id123", entryPojo.myId);
        return entryPojo;
    }

    private void storeJavaBeanToMongoDb(final MyEntryPojo entryPojo) {
        final long startMongo = currentTimeMillis();

        storage.insert(COLLECTION_NAME, entryPojo);

        System.out.println(format(">>> POJO->Mongo done in %d ms", currentTimeMillis() - startMongo));

        Iterator<Map<String, Object>> dbEntries = storage.getDocumentsAsMap(COLLECTION_NAME).iterator();

        assertTrue(dbEntries.hasNext());
        Map<String, Object> dbEntry1 = dbEntries.next();
        assertEquals("All: " + dbEntry1
                , "id123", dbEntry1.get("_id")); // Notice that Mongo saves the id as _id
    }

    private void fetchDocumentFromMongoViaRest() throws JSONException {
        long startWsCall = currentTimeMillis();

        ClientResponse response = resource().path("/list").path(COLLECTION_NAME).get(ClientResponse.class);

        System.out.println(format(">>> Mongo->REST call done in %d ms", currentTimeMillis() - startWsCall));

        if (!OK.equals(response.getClientResponseStatus())) {
            fail(format("Response failed, got %s, check sysout; response: %s"
                    , response.getClientResponseStatus()
                    , response.getEntity(String.class)));
        }

        JSONArray responseJson = response.getEntity(JSONArray.class);
        assertEquals(1, responseJson.length());

        JSONObject firstJsonObj = responseJson.getJSONObject(0);
        assertEquals("Differs from expected, whole object: " + firstJsonObj
                , "scalarAttrValue_XY"
                , firstJsonObj.optString("scalarAttribute"));
    }

}
