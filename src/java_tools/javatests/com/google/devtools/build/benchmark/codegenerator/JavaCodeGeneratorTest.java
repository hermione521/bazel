package com.google.devtools.build.benchmark.codegenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test for JavaCodeGenerator.
 */
@RunWith(JUnit4.class)
public class JavaCodeGeneratorTest {

  private static final Path rootPath = Paths.get(System.getProperty("user.dir"));

  @Test
  public void testGenerateNewProject() {
    Path dir = rootPath.resolve("GenerateNewProject");
    JavaCodeGenerator.generateNewProject(dir.toString(), true, true, true, true);

    // Check dir contains 4 project directories
    File[] filesList = dir.toFile().listFiles();
    assertNotNull(filesList);
    String[] filenames = fileArrayToSortedStringArray(filesList);
    assertArrayEquals(new String[]{
        JavaCodeGenerator.TARGET_A_FEW_FILES,
        JavaCodeGenerator.TARGET_LONG_CHAINED_DEPS,
        JavaCodeGenerator.TARGET_MANY_FILES,
        JavaCodeGenerator.TARGET_PARALLEL_DEPS}, filenames);

    // Target 1: a few files
    checkProjectPathContains(dir, JavaCodeGenerator.TARGET_A_FEW_FILES);
    checkSimpleTarget(
        dir, JavaCodeGenerator.TARGET_A_FEW_FILES, JavaCodeGenerator.SIZE_A_FEW_FILES);

    // Target 2: many files
    checkProjectPathContains(dir, JavaCodeGenerator.TARGET_MANY_FILES);
    checkSimpleTarget(
        dir, JavaCodeGenerator.TARGET_MANY_FILES, JavaCodeGenerator.SIZE_MANY_FILES);

    // Target 3: long chained deps
    checkProjectPathContains(dir, JavaCodeGenerator.TARGET_LONG_CHAINED_DEPS);


    // Target 4: parallel deps
    checkProjectPathContains(dir, JavaCodeGenerator.TARGET_PARALLEL_DEPS);

  }

  private String[] fileArrayToSortedStringArray(File[] files) {
    Arrays.sort(files);
    String[] strings = new String[files.length];
    for (int i = 0; i < files.length; ++i) {
      strings[i] = files[i].toString();
    }
    return strings;
  }

  private void checkSimpleTarget(Path root, String targetName, int targetSize) {
    // Check Java files
    File[] filesList =
        root.resolve(targetName).resolve("com/example/generated").toFile().listFiles();
    assertNotNull(filesList);
    String[] filenames = fileArrayToSortedStringArray(filesList);
    String[] randomClassNames = new String[targetSize + 1];
    randomClassNames[0] = "Main.java";
    for (int i = 0; i < targetSize; ++i) {
      randomClassNames[i + 1] = "RandomClass" + i + ".java";
    }
    assertArrayEquals(filenames, randomClassNames);
  }

  private void checkProjectPathContains(Path root, String targetName) {
    // Check project dir contains BUILD and com
    File[] filesList = root.resolve(targetName).toFile().listFiles();
    assertNotNull(filesList);
    String[] filenames = fileArrayToSortedStringArray(filesList);
    assertArrayEquals(new String[]{"BUILD", "com"}, filenames);
  }

}
