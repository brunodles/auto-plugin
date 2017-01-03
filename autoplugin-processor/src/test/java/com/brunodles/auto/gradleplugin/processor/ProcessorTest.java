package com.brunodles.auto.gradleplugin.processor;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

@RunWith(JUnit4.class)
public class ProcessorTest {

    @Test
    public void autoService() {

        assert_().about(javaSources())
                .that(Arrays.asList(
                        JavaFileObjects.forResource("test/SomePlugin.java"),
                        JavaFileObjects.forResource("test/SomePlugin2.java"),
                        JavaFileObjects.forResource("test/SomePlugin3.java")
                ))
                .processedWith(new Processor())
                .compilesWithoutError()
                .and().generatesFiles(
                JavaFileObjects.forResource("META-INF/services/test.SomePlugin.properties"),
                JavaFileObjects.forResource("META-INF/services/test.SomePlugin2.properties"),
                JavaFileObjects.forResource("META-INF/services/SomePlugin2CustomName.properties"),
                JavaFileObjects.forResource("META-INF/services/test.SomePlugin3.properties"),
                JavaFileObjects.forResource("META-INF/services/SomePlugin3CustomName1.properties"),
                JavaFileObjects.forResource("META-INF/services/SomePlugin3CustomName2.properties")
        );
    }

}