package ru.bool.sonar.plugin.bitbucket.http;

import okhttp3.*;
import org.sonar.api.batch.BatchSide;
import ru.bool.sonar.plugin.bitbucket.PluginConfig;

@BatchSide
public class BitbucketClient {
    private final OkHttpClient client;
    private final PluginConfig pluginConfig;
    private final String basicAuth;

    public BitbucketClient(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        client = new OkHttpClient();
        basicAuth = Credentials.basic(
                pluginConfig.bbUsername,
                pluginConfig.bbPassword
        );
    }

    public void send(String body) {

        client.newCall(new Request.Builder().
                url(pluginConfig.coverageUrl).
                header("Authorization", basicAuth).
                header("Content-Type", "application/json").
                post(RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        body
                )).build()).enqueue(new LoggingCallback());
    }

}
