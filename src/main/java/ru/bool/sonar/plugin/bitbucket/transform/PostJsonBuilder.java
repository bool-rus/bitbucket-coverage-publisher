package ru.bool.sonar.plugin.bitbucket.transform;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sonar.api.batch.fs.InputFile;

import java.util.HashMap;
import java.util.Map;

public class PostJsonBuilder {
    private final Map<InputFile, String> coverages = new HashMap<>();
    private final PathResolver pathResolver;

    public PostJsonBuilder(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void addCoverage(InputFile file, String coverage) {
        if (!coverage.isEmpty()) coverages.put(file, coverage);
    }

    public String build() {
        JSONObject jsonBody = new JSONObject();
        JSONArray files = new JSONArray();
        jsonBody.put("files", files);
        coverages.forEach((f, c) -> {
            if (c.isEmpty()) return;
            JSONObject entry = new JSONObject();
            entry.put("path", pathResolver.resolve(f));
            entry.put("coverage", c);
            files.add(entry);
        });
        return jsonBody.toString();
    }
}
