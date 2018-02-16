package ru.bool.sonar.plugin.bitbucket;

import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.config.Settings;

import java.text.MessageFormat;
@BatchSide
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class PluginConfig {
    private static final String COVERAGE_URL = "{0}/rest/code-coverage/1.0/commits/{1}";
    final Settings settings;
    public final String coverageUrl;
    public final String bbUsername;
    public final String bbPassword;
    public final boolean enabled;

    public PluginConfig(Settings settings) {
        this.settings = settings;
        coverageUrl = MessageFormat.format(
                COVERAGE_URL,
                settings.getString("sonar.stash.url"),
                settings.getString("sonar.stash.commit")
                );
        bbUsername = settings.getString("sonar.stash.username");
        bbPassword = settings.getString("sonar.stash.password");
        String analysisMode = settings.getString("sonar.analysis.mode");
        enabled = settings.getBoolean("sonar.stash.coverage.publish") &&
                 ("preview".equalsIgnoreCase(analysisMode) || "issues".equalsIgnoreCase(analysisMode));
    }
}
