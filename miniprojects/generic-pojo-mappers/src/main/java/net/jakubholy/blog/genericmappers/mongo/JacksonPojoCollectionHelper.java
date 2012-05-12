package net.jakubholy.blog.genericmappers.mongo;

import java.util.HashMap;
import java.util.Map;

import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.internal.MongoAnnotationIntrospector;
import net.vz.mongodb.jackson.internal.MongoJacksonHandlerInstantiator;
import net.vz.mongodb.jackson.internal.MongoJacksonMapperModule;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * Helper for working with Mongo collections via POJOs,
 * using the Jackson object mapper.
 */
class JacksonPojoCollectionHelper {

    private static final ObjectMapper MAP_KEY_SANITIZING_MAPPER = createCustomizedObjectMapper();
    private final DB mongoDb;
    private final Map<String, JacksonDBCollection<?, ?>> cache = new HashMap<String, JacksonDBCollection<?,?>>();

    public JacksonPojoCollectionHelper(DB mongoDb) {
        this.mongoDb = mongoDb;
    }

    /**
     * Create our customized object mapper that f.ex. takes care
     * of removing invalid characters from map keys.
     */
    private static ObjectMapper createCustomizedObjectMapper() {
        SimpleModule mapKeyModule =  new SimpleModule("MyMapKeySanitizingSerializerModule"
                , new Version(1, 0, 0, null));
        mapKeyModule.addKeySerializer(String.class, new KeySanitizingSerializer());

        final ObjectMapper customizedMapper = new ObjectMapper();
        customizedMapper.registerModule(mapKeyModule);

        // The following code has been copied from JacksonDBCollection and
        // with mongo-jackson-mapper 1.4.x it can be replaced with call to
        // MongoJacksonMapperModule.configure(mapper)
        customizedMapper.registerModule(MongoJacksonMapperModule.INSTANCE);
        customizedMapper.setHandlerInstantiator(new MongoJacksonHandlerInstantiator(
                new MongoAnnotationIntrospector(customizedMapper.getDeserializationConfig())));
        // end copy

        return customizedMapper;
    }

    <T> JacksonDBCollection<T,Object> wrap(DBCollection dbCollection, Class<T> type) {
        return JacksonDBCollection.wrap(dbCollection, type, Object.class, MAP_KEY_SANITIZING_MAPPER);
    }

    public <T> JacksonDBCollection<T, Object> getPojoCollectionFor(String collectionName, Class<T> pojoType) {
        DBCollection rawCollection = mongoDb.getCollection(collectionName);

        @SuppressWarnings("unchecked")
        JacksonDBCollection<T, Object> pojoCollection = (JacksonDBCollection<T, Object>) cache.get(collectionName);
        if (pojoCollection == null) {
          pojoCollection = wrap(rawCollection, pojoType);
          cache.put(collectionName, pojoCollection);
        }

        return pojoCollection;
    }

    public <T> JacksonDBCollection<T, Object> getPojoCollectionFor(String collectionName, T pojo) {
        @SuppressWarnings("unchecked")
        Class<T> pojoType = (Class<T>) pojo.getClass();
        return getPojoCollectionFor(collectionName, pojoType);
    }

}
