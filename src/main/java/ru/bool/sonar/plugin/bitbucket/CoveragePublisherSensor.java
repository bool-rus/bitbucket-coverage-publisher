package ru.bool.sonar.plugin.bitbucket;

import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.resources.Project;
import ru.bool.sonar.plugin.bitbucket.http.BitbucketClient;
import ru.bool.sonar.plugin.bitbucket.transform.CoverageConverter;
import ru.bool.sonar.plugin.bitbucket.transform.PathResolver;
import ru.bool.sonar.plugin.bitbucket.transform.PostJsonBuilder;

@Phase(name = Phase.Name.POST)
@SuppressWarnings("deprecated")
public class CoveragePublisherSensor implements Sensor {

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
