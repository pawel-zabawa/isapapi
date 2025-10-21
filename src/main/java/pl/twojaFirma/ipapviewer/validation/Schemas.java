package pl.twojaFirma.ipapviewer.validation;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.fasterxml.jackson.databind.JsonNode;
import pl.twojaFirma.ipapviewer.infra.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class Schemas {

    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    private static final Map<String, JsonSchema> CACHE = Map.of(
            "categories", load("schemas/ipap-categories.schema.json"),
            "acts", load("schemas/ipap-acts.schema.json")
    );

    private Schemas() {
    }

    public static JsonSchema schema(String key) {
        return CACHE.get(key);
    }

    private static JsonSchema load(String path) {
        try (InputStream input = Schemas.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Schema not found: " + path);
            }
            JsonNode node = JsonUtil.mapper().readTree(input);
            return FACTORY.getSchema(node);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load schema " + path, e);
        }
    }
}
