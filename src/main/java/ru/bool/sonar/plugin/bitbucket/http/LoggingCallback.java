package ru.bool.sonar.plugin.bitbucket.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingCallback implements Callback {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCallback.class);

    @Override
    public void onFailure(Call call, IOException e) {
        LOGGER.warn("Something wrong on send coverage info to BitBucket", e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {
            LOGGER.info("Coverage data succesfully sended");
        } else {
            LOGGER.warn("Something wrong on send coverage info to BitBucket");
        }
    }
}
