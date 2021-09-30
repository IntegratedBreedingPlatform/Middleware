package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.service.api.BrapiView;
import org.generationcp.middleware.service.api.study.CategoryDTO;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CategorySerializer extends JsonSerializer<List<CategoryDTO>> {

    @Override
    public void serialize(final List<CategoryDTO> value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {

        if (serializers.getActiveView() != null && CollectionUtils.isNotEmpty(value)) {
            if (BrapiView.BrapiV2.class.equals(serializers.getActiveView())) {
                gen.writeObject(value);
            } else {
                gen.writeObject(value.stream().map(CategoryDTO::getValue).collect(Collectors.toList()));
            }
        } else {
            gen.writeObject(value);
        }

    }
}
