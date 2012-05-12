package net.jakubholy.blog.genericmappers.rest;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.jakubholy.blog.genericmappers.mongo.MongoStorage;

@Path("/list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.WILDCARD)
public class GenericCollectionResource {

    private MongoStorage storage;

    public GenericCollectionResource(@Context MongoStorage genericStorage) {
        this.storage = genericStorage;
    }

    private static final Logger log = Logger.getLogger(GenericCollectionResource.class.getName());

    @GET
    @Path("/{collectionName}")
    public Response listStoredCollection(@PathParam("collectionName") String collectionName) {
        try {
            Iterable<Map<String, Object>> collectionElements = storage.getDocumentsAsMap(collectionName);

            return Response.ok()
                    .entity(collectionElements)
                    .expires(getDefaultCachePeriod())
                    .build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to list the collection " + collectionName, e);
            return Response.serverError().entity("Failure: " + e).build();
        }
    }

    /**
     * How long to cache responses by default? It should be short enough to
     * avoid the risks of stale data and long enough not to crash during
     * request spikes.
     */
    private Date getDefaultCachePeriod() {
        return new Date(System.currentTimeMillis() + 5000);
    }

}
