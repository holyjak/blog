package net.jakubholy.blog.genericmappers.mongo;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Replace the characters that are not legal in JSON field names,
 * namely '.' and leading '$'.
 */
class KeySanitizingSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        if (value.startsWith("$")) {
            value = "#" + value.substring(1);
        }
        jgen.writeFieldName(value.replace('.', '-'));

    }

}
