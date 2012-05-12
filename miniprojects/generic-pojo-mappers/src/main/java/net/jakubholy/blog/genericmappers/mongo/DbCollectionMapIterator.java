package net.jakubholy.blog.genericmappers.mongo;

import java.util.Iterator;
import java.util.Map;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Iterate over search results returning the elements as Maps instead of
 * the native BSON objects.
 */
final class DbCollectionMapIterator implements Iterator<Map<String, Object>> {
    private final Iterator<DBObject> allDocuments;

    public DbCollectionMapIterator(DBCursor allDocuments) {
        this.allDocuments = allDocuments.iterator();
    }

    @Override
    public boolean hasNext() {
        return allDocuments.hasNext();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> next() {
        return allDocuments.next().toMap();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
