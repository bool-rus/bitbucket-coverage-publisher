package ru.bool.sonar.plugin.bitbucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.resources.Project;
import ru.bool.sonar.plugin.bitbucket.http.BitbucketClient;
import ru.bool.sonar.plugin.bitbucket.transform.CoverageConverter;
import ru.bool.sonar.plugin.bitbucket.transform.PathResolver;
import ru.bool.sonar.plugin.bitbucket.transform.PostJsonBuilder;

import java.util.HashMap;
import java.util.Map;

@Phase(name = Phase.Name.POST)
public class CoveragePublisherSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoveragePublisherSensor.class);

    private final PluginConfig pluginConfig;
    private final PathResolver pathResolver;
    private final BitbucketClient bitbucketClient;

    public CoveragePublisherSensor(PluginConfig pluginConfig, PathResolver pathResolver, BitbucketClient bitbucketClient) {
        this.pluginConfig = pluginConfig;
        this.pathResolver = pathResolver;
        this.bitbucketClient = bitbucketClient;
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        FileSystem fileSystem = context.fileSystem();
        PostJsonBuilder postJsonBuilder = new PostJsonBuilder(pathResolver);
        CoverageConverter converter = new CoverageConverter(context);
        Map<InputFile, String> coverages = new HashMap<>();
        fileSystem.inputFiles(fileSystem.predicates().all()).forEach(f ->
                postJsonBuilder.addCoverage(f, converter.convert(f).getCoverageData())
        );
        bitbucketClient.send(postJsonBuilder.build());
    }

    @Override
    public boolean shouldExecuteOnProject(org.sonar.api.resources.Project project) {
        return pluginConfig.enabled;
    }
}
