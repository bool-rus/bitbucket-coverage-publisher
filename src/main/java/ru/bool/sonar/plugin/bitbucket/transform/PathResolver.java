package ru.bool.sonar.plugin.bitbucket.transform;

import org.sonar.api.batch.bootstrap.ProjectBuilder;
import org.sonar.api.batch.fs.InputFile;

import java.io.File;

public class PathResolver extends ProjectBuilder {

    private File baseDir = new File("/");

    @Override
    public void build(Context context) {
        baseDir = context.projectReactor().getRoot().getBaseDir();
    }

    public String resolve(InputFile f) {
        return baseDir.toURI().relativize(
                f.path().toUri()
        ).getPath();
    }
}
