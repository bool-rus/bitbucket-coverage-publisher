package ru.bool.sonar.plugin;

import org.sonar.api.Plugin;
import ru.bool.sonar.plugin.bitbucket.http.BitbucketClient;
import ru.bool.sonar.plugin.bitbucket.CoveragePublisherSensor;
import ru.bool.sonar.plugin.bitbucket.transform.PathResolver;
import ru.bool.sonar.plugin.bitbucket.PluginConfig;

public class BitbucketCoveragePublisherPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(
                CoveragePublisherSensor.class,
                PluginConfig.class,
                PathResolver.class,
                BitbucketClient.class
        );
    }
}
