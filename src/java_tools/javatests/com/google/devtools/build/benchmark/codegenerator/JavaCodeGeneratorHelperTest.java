package com.google.devtools.build.benchmark.codegenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test for JavaCodeGeneratorHelper.
 */
@RunWith(JUnit4.class)
public class JavaCodeGeneratorHelperTest {

  private static final Path rootPath = Paths.get(System.getProperty("user.dir"));

  @Test
  public void testWriteRandomClassToDir() throws IOException {
    Path dir = rootPath.resolve("TestWriteRandomClassToDir");
    JavaCodeGeneratorHelper.writeRandomClassToDir(false, "ClassName", "com.package.name", dir);

    Path javaFile = dir.resolve("com/package/name/ClassName.java");
    assertTrue(javaFile.toFile().exists());

    String content = new Scanner(javaFile).useDelimiter("\\Z").next();
    String expected = "package com.package.name;\n"
        + "\n"
        + "import java.lang.System;\n"
        + "import java.util.Random;\n"
        + "\n"
        + "public final class ClassName {\n"
        + "  public static void PrintSth() {\n"
        + "    Random rand = new Random();\n"
        + "    int n = rand.nextInt(100);\n"
        + "    System.out.format(\"This is method(%s) with random number(%d)\\n\", \"PrintSth\", n);\n"
        + "  }\n"
        + "}";
    assertEquals(expected, content);
  }

  @Test
  public void testWriteRandomClassToDirExtraMethod() throws IOException {
    Path dir = rootPath.resolve("TestWriteRandomClassToDir");
    JavaCodeGeneratorHelper.writeRandomClassToDir(true, "ClassNameExtra", "com.package.name", dir);

    Path javaFile = dir.resolve("com/package/name/ClassNameExtra.java");
    assertTrue(javaFile.toFile().exists());

    String content = new Scanner(javaFile).useDelimiter("\\Z").next();
    String expected = "package com.package.name;\n"
        + "\n"
        + "import java.lang.System;\n"
        + "import java.util.Random;\n"
        + "\n"
        + "public final class ClassNameExtra {\n"
        + "  public static void PrintSth() {\n"
        + "    Random rand = new Random();\n"
        + "    int n = rand.nextInt(100);\n"
        + "    System.out.format(\"This is method(%s) with random number(%d)\\n\", \"PrintSth\", n);\n"
        + "  }\n"
        + "\n"
        + "  public static void PrintSthElse() {\n"
        + "    Random rand = new Random();\n"
        + "    int n = rand.nextInt(100);\n"
        + "    System.out.format(\"This is method(%s) with random number(%d)\\n\", \"PrintSthElse\", n);\n"
        + "  }\n"
        + "}";
    assertEquals(expected, content);
  }

  @Test
  public void testWriteMainClassToDir() throws IOException {
    Path dir = rootPath.resolve("TestWriteMainClassToDir");
    JavaCodeGeneratorHelper.writeMainClassToDir("com.package.name", dir);

    Path javaFile = dir.resolve("com/package/name/Main.java");
    assertTrue(javaFile.toFile().exists());

    String content = new Scanner(javaFile).useDelimiter("\\Z").next();
    String expected = "package com.package.name;\n"
        + "\n"
        + "import java.lang.String;\n"
        + "\n"
        + "public class Main {\n"
        + "  public static void main(String[] args) {\n"
        + "  }\n"
        + "}";
    assertEquals(expected, content);
  }

  @Test
  public void testBuildFileWithNextDeps() throws IOException {
    Path dir = rootPath.resolve("BuildFileWithNextDeps");
    Files.createDirectories(dir);
    JavaCodeGeneratorHelper.buildFileWithNextDeps(42, "<this is deps>", dir);

    Path buildFile = dir.resolve("BUILD");
    assertTrue(buildFile.toFile().exists());

    String content = new Scanner(buildFile).useDelimiter("\\Z").next();
    String expected = "java_library(\n"
        + "    name=\"Deps42\",\n"
        + "    srcs=glob([ \"com/example/deps42/*.java\" ]),\n"
        + "<this is deps>    visibility=[ \"//visibility:public\" ],\n"
        + ")";
    assertEquals(expected, content);
  }

  @Test
  public void testBuildFileWithMainClass() throws IOException {
    Path dir = rootPath.resolve("BuildFileWithMainClass");
    Files.createDirectories(dir);
    JavaCodeGeneratorHelper.buildFileWithMainClass("Target", "<this is deps>", dir);

    Path buildFile = dir.resolve("BUILD");
    assertTrue(buildFile.toFile().exists());

    String content = new Scanner(buildFile).useDelimiter("\\Z").next();
    String expected = "java_binary(\n"
        + "    name=\"Target\",\n"
        + "    srcs=glob([ \"com/example/generated/*.java\" ]),\n"
        + "    main_class=\"com.example.generated.Main\",\n"
        + "<this is deps>)";
    assertEquals(expected, content);
  }

  @Test
  public void testTargetWithNextHelper() throws IOException {
    Path dir = rootPath.resolve("TargetWithNextHelper");
    JavaCodeGeneratorHelper.targetWithNextHelper(false, 42, true, dir);

    Path javaFile = dir.resolve("com/example/deps42/Deps42.java");
    assertTrue(javaFile.toFile().exists());

    String content = new Scanner(javaFile).useDelimiter("\\Z").next();
    String expected = "package com.example.deps42;\n"
        + "\n"
        + "import com.example.deps43.Deps43;\n"
        + "import java.lang.System;\n"
        + "import java.util.Random;\n"
        + "\n"
        + "public final class Deps42 {\n"
        + "  public static void PrintSth() {\n"
        + "    Random rand = new Random();\n"
        + "    int n = rand.nextInt(100);\n"
        + "    System.out.format(\"This is method(%s) with random number(%d)\\n\", \"PrintSth\", n);\n"
        + "  }\n"
        + "\n"
        + "  public static void CallNext() {\n"
        + "    Deps43.PrintSth();\n"
        + "  }\n"
        + "}";
    assertEquals(expected, content);
  }

  @Test
  public void testTargetWithNextHelperExtra() throws IOException {
    Path dir = rootPath.resolve("TargetWithNextHelperExtra");
    JavaCodeGeneratorHelper.targetWithNextHelper(true, 42, false, dir);

    Path javaFile = dir.resolve("com/example/deps42/Deps42.java");
    assertTrue(javaFile.toFile().exists());

    String content = new Scanner(javaFile).useDelimiter("\\Z").next();
    String expected = "package com.example.deps42;\n"
        + "\n"
        + "import java.lang.System;\n"
        + "import java.util.Random;\n"
        + "\n"
        + "public final class Deps42 {\n"
        + "  public static void PrintSth() {\n"
        + "    Random rand = new Random();\n"
        + "    int n = rand.nextInt(100);\n"
        + "    System.out.format(\"This is method(%s) with random number(%d)\\n\", \"PrintSth\", n);\n"
        + "  }\n"
        + "\n"
        + "  public static void PrintSthElse() {\n"
        + "    Random rand = new Random();\n"
        + "    int n = rand.nextInt(100);\n"
        + "    System.out.format(\"This is method(%s) with random number(%d)\\n\", \"PrintSthElse\", n);\n"
        + "  }\n"
        + "}";
    assertEquals(expected, content);
  }

  @Test
  public void testParallelDepsMainClassHelper() throws IOException {
    Path dir = rootPath.resolve("ParallelDepsMainClassHelper");
    JavaCodeGeneratorHelper.parallelDepsMainClassHelper(4, dir);

    Path javaFile = dir.resolve("com/example/generated/Main.java");
    assertTrue(javaFile.toFile().exists());

    String content = new Scanner(javaFile).useDelimiter("\\Z").next();
    String expected = "package com.example.generated;\n"
        + "\n"
        + "import com.example.deps1.Deps1;\n"
        + "import com.example.deps2.Deps2;\n"
        + "import com.example.deps3.Deps3;\n"
        + "import java.lang.String;\n"
        + "\n"
        + "public final class Main {\n"
        + "  public static void main(String[] args) {\n"
        + "    Deps1.PrintSth();\n"
        + "    Deps2.PrintSth();\n"
        + "    Deps3.PrintSth();\n"
        + "  }\n"
        + "}";
    assertEquals(expected, content);
  }
}
