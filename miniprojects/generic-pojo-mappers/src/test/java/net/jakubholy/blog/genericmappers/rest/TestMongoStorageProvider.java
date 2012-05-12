package net.jakubholy.blog.genericmappers.rest;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import net.jakubholy.blog.genericmappers.mongo.MongoStorage;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * Utility to inject an existing instance of {@link MongoStorage} into the
 * {@link {@link GenericCollectionResource}.
 */
@Provider
public class TestMongoStorageProvider implements InjectableProvider<Context, Type>, Injectable<MongoStorage> {

    public static MongoStorage instance;

    @Override
    public MongoStorage getValue() {
        System.err.println("TestMongoStorageProvider called and returns " + instance);
        return instance;
    }

    @Override
    public Injectable<MongoStorage> getInjectable(ComponentContext ic, Context a, Type c) {
        return c.equals(MongoStorage.class)? this : null;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

}
