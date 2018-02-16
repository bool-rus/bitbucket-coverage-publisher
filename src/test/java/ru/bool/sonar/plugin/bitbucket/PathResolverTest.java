package ru.bool.sonar.plugin.bitbucket;

import org.junit.Test;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.api.batch.bootstrap.internal.ProjectBuilderContext;
import org.sonar.api.batch.fs.InputFile;
import ru.bool.sonar.plugin.bitbucket.transform.PathResolver;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathResolverTest {

    @Test
    public void resolve1() {
        test("/1/2","/1/2/3","3");
    }
    @Test
    public void resolve2() {
        test("/1/2/","/1/2/3","3");
    }
    @Test
    public void resolve3() {
        test("1/2", "1/2/3", "3");
    }
    @Test
    public void resolve4() {
        test("/1/2","/1/2/3/4/5","3/4/5");
    }

    private void test(String basePath, String absolutePath, String expected) {

        File base = new File(basePath);
        File absolute = new File(absolutePath);


        ProjectReactor reactor = mock(ProjectReactor.class);
        ProjectDefinition definition = mock(ProjectDefinition.class);
        InputFile file = mock(InputFile.class);

        when(reactor.getRoot()).thenReturn(definition);
        when(definition.getBaseDir()).thenReturn(base);
        when(file.path()).thenReturn(absolute.toPath());


        PathResolver resolver = new PathResolver();
        resolver.build(new ProjectBuilderContext(reactor));

        String result = resolver.resolve(file);

        assertEquals(expected, result);
    }
}