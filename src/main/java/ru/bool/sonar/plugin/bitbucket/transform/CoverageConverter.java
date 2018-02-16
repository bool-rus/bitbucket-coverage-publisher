package ru.bool.sonar.plugin.bitbucket.transform;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.KeyValueFormat;

import java.util.*;

/**
 * Thread unsafe!
 */
public class CoverageConverter {
    private final SensorContext context;

    private final List<Integer> covered = new ArrayList<>(100);
    private final List<Integer> partial = new ArrayList<>(100);
    private final List<Integer> missed = new ArrayList<>(100);

    private Resource resource;
    private Map<Integer, Integer> lineHitsData;
    private Map<Integer, Integer> conditionsData;
    private Map<Integer, Integer> coveredConditionsData;

    public CoverageConverter(SensorContext context) {
        this.context = context;
    }

    private Map<Integer, Integer> invoke(Metric<String> metric) {
        Measure<String> measure = context.getMeasure(resource, metric);
        if (measure == null)
            return Collections.emptyMap();
        return KeyValueFormat.parseIntInt(
                measure.getData()
        );
    }

    private void processLine(Integer line, Integer hits) {
        if (hits == 0) {
            missed.add(line);
            return;
        }
        Integer conditions = conditionsData.get(line);
        if (conditions == null || conditions < 2) {
            covered.add(line);
            return;
        }
        Integer coveredConditions = coveredConditionsData.get(line);
        if (coveredConditions == null || coveredConditions == 0) {
            missed.add(line); //избыточное условие, но мало ли
            return;
        }
        if (coveredConditions < conditions) partial.add(line);
        else covered.add(line);
    }

    private void init(InputFile file) {
        covered.clear();
        partial.clear();
        missed.clear();
        resource = context.getResource(file);
        lineHitsData = invoke(CoreMetrics.COVERAGE_LINE_HITS_DATA);
        conditionsData = invoke(CoreMetrics.CONDITIONS_BY_LINE);
        coveredConditionsData = invoke(CoreMetrics.COVERED_CONDITIONS_BY_LINE);
    }

    public CoverageConverter convert(InputFile file) {
        init(file);
        lineHitsData.forEach(this::processLine);
        return this;
    }


    public String getCoverageData() {
        StringBuilder builder = new StringBuilder();
        boolean exist = false;

        if (!covered.isEmpty()) {
            builder.append("C:");
            StringJoiner joiner = new StringJoiner(",");
            covered.forEach(i -> joiner.add(i.toString()));
            builder.append(joiner);
            exist = true;
        }
        if (!partial.isEmpty()) {
            if (exist) builder.append(';');
            builder.append("P:");
            StringJoiner joiner = new StringJoiner(",");
            partial.forEach(i -> joiner.add(i.toString()));
            builder.append(joiner);
            exist = true;
        }
        if (!missed.isEmpty()) {
            if (exist) builder.append(';');
            builder.append("U:");
            StringJoiner joiner = new StringJoiner(",");
            missed.forEach(i -> joiner.add(i.toString()));
            builder.append(joiner);
        }
        return builder.toString();
    }
}
