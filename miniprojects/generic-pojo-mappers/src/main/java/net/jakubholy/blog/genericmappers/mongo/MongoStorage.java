package net.jakubholy.blog.genericmappers.mongo;

import java.util.Iterator;
import java.util.Map;

import net.vz.mongodb.jackson.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class MongoStorage {

    private Mongo mongo;

    private final JacksonPojoCollectionHelper pojoDbHelper;

    public MongoStorage(Mongo mongo) {
        this.mongo = mongo;
        this.mongo.setWriteConcern(WriteConcern.SAFE);
        this.pojoDbHelper = new JacksonPojoCollectionHelper(getMongoDb());
    }

    /**
     * Fetch all the documents from a collection and return them as Java maps.
     * @param collectionName (required)
     */
    public Iterable<Map<String, Object>> getDocumentsAsMap(String collectionName) {
        try {
            final DB db = getMongoDb();
            final DBCollection collection = db.getCollection(collectionName);

            final DBCursor allDocuments = collection.find();

            return new Iterable<Map<String, Object>>() {

                @Override
                public Iterator<Map<String, Object>> iterator() {
                    return new DbCollectionMapIterator(allDocuments);
                }
            };

        } catch (MongoException me) {
            throw new RuntimeException(me);
        }
    }

    public <T> void updateById(String collectionName, T pojo, Object id) {
        JacksonDBCollection<T, Object> pojos = pojoDbHelper.getPojoCollectionFor(collectionName, pojo);
        final int updateCount = pojos.updateById(id, pojo).getN();
        if (updateCount != 1) {
            throw new IllegalStateException("The object to be updated doesn't " + "exist in the collection '"
                    + collectionName + "' with the id '" + id + "'. Update pojo: " + pojo);
        }
    }

    /**
     * Look ma, automatic POJO to Mongo's BSON object mapping without any manual transformations!
     * @param collectionName (required) the collection to insert the document representing the pojo into
     * @param pojo (required) the Java bean to store, optionally annotated with some Jackson annotations
     */
    public <T> void insert(String collectionName, T pojo) {
        try {
            // SAFE = Require Mongo to wait for response from the DB to verify
            // there isn't a duplicate
            pojoDbHelper.getPojoCollectionFor(collectionName, pojo).insert(pojo, WriteConcern.SAFE);
        } catch (MongoException.DuplicateKey e) {
            throw new IllegalStateException("A document with the same id exists already: " + e.getMessage());
        }
    }

    /** For testing */
    public void dropDatabase() {
        getMongoDb().dropDatabase();
    }

    public <T> T findOneById(String collectionName, Object id, Class<T> type) {
        return pojoDbHelper.getPojoCollectionFor(collectionName, type).findOneById(id);
    }

    private DB getMongoDb() {
        return mongo.getDB("myDb");
    }
}
