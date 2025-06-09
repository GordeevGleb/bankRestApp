package org.example.bank_app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OpenApiExporter implements CommandLineRunner {

    private final OpenAPI openAPI;

    public OpenApiExporter(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        // Указание пути к файлу
        File outputDir = new File("/Bank_REST/docs");
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (!created) {
                throw new RuntimeException("Не удалось создать директорию: " + outputDir.getAbsolutePath());
            }
        }

        File file = new File(outputDir, "openapi.yaml");
        mapper.writeValue(file, openAPI);

        System.out.println("✅ OpenAPI YAML экспортирован: " + file.getAbsolutePath());
    }
}
