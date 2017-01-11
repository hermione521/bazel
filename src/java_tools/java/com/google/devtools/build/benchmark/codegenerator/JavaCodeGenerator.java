package com.google.devtools.build.benchmark.codegenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Create 4 types of Java project, or modify existing ones.
 */
public class JavaCodeGenerator {

  static final String TARGET_A_FEW_FILES = "AFewFiles";
  static final int SIZE_A_FEW_FILES = 10;

  static final String TARGET_MANY_FILES = "ManyFiles";
  static final int SIZE_MANY_FILES = 1000;

  static final String TARGET_LONG_CHAINED_DEPS = "LongChainedDeps";
  static final int SIZE_LONG_CHAINED_DEPS = 20;

  static final String TARGET_PARALLEL_DEPS = "ParallelDeps";
  static final int SIZE_PARALLEL_DEPS = 20;


  static void generateNewProject(String outputDir,
      boolean aFewFiles, boolean manyFiles, boolean longChainedDeps, boolean parallelDeps) {
    Path dir = Paths.get(outputDir);
    if (aFewFiles) {
      createTargetWithAFewFiles(dir.resolve(TARGET_A_FEW_FILES));
    }
    if (manyFiles) {
      createTargetWithManyFiles(dir.resolve(TARGET_MANY_FILES));
    }
    if (longChainedDeps) {
      createTargetWithLongChainedDeps(dir.resolve(TARGET_LONG_CHAINED_DEPS));
    }
    if (parallelDeps) {
      createTargetWithParallelDeps(dir.resolve(TARGET_PARALLEL_DEPS));
    }
  }

  static void modifyExistingProject(String outputDir,
      boolean aFewFiles, boolean manyFiles, boolean longChainedDeps, boolean parallelDeps) {
    Path dir = Paths.get(outputDir);
    if (aFewFiles) {
      modifyTargetWithAFewFiles(dir.resolve(TARGET_A_FEW_FILES));
    }
    if (manyFiles) {
      modifyTargetWithManyFiles(dir.resolve(TARGET_MANY_FILES));
    }
    if (longChainedDeps) {
      modifyTargetWithLongChainedDeps(dir.resolve(TARGET_LONG_CHAINED_DEPS));
    }
    if (parallelDeps) {
      modifyTargetWithParallelDeps(dir.resolve(TARGET_PARALLEL_DEPS));
    }
  }


  /**
   * Target type 1: Create targets with few files (~10 files)
   */
  private static void createTargetWithAFewFiles(Path projectPath) {
    if (checkPathExists(projectPath)) {
      return;
    }

    try {
      Files.createDirectories(projectPath);

      for (int i = 0; i < SIZE_A_FEW_FILES; ++i) {
        JavaCodeGeneratorHelper.writeRandomClassToDir(
            /* addExtraMethod = */ false, "RandomClass" + i, "com.example.generated", projectPath);
      }

      JavaCodeGeneratorHelper.writeMainClassToDir("com.example.generated", projectPath);
      JavaCodeGeneratorHelper.buildFileWithMainClass(TARGET_A_FEW_FILES, "", projectPath);
    } catch (IOException e) {
      System.err.println("Error creating target with a few files: " + e.getMessage());
    }
  }

  /**
   * Target type 1: Modify targets with few files (~10 files)
   */
  private static void modifyTargetWithAFewFiles(Path projectPath) {
    File dir = projectPath.toFile();
    if (!(dir.exists() && dir.isDirectory())) {
      System.err.format(
          "Project dir (%s) does not contain code for modification.\n", projectPath.toString());
      return;
    }
    try {
      JavaCodeGeneratorHelper.writeRandomClassToDir(
          /* addExtraMethod = */ true, "RandomClass0", "com.example.generated", projectPath);
    } catch (IOException e) {
      System.err.println("Error modifying target with a few files: " + e.getMessage());
    }
  }

  /**
   * Target type 2: Create targets with many files (~ 1000 Java files in a single library)
   */
  private static void createTargetWithManyFiles(Path projectPath) {
    if (checkPathExists(projectPath)) {
      return;
    }

    try {
      Files.createDirectories(projectPath);

      for (int i = 0; i < SIZE_MANY_FILES; ++i) {
        JavaCodeGeneratorHelper.writeRandomClassToDir(
            /* addExtraMethod = */ false, "RandomClass" + i, "com.example.generated", projectPath);
      }

      JavaCodeGeneratorHelper.writeMainClassToDir("com.example.generated", projectPath);
      JavaCodeGeneratorHelper.buildFileWithMainClass(TARGET_MANY_FILES, "", projectPath);
    } catch (IOException e) {
      System.err.println("Error creating targets with many files: " + e.getMessage());
    }
  }

  /**
   * Target type 2: Modify targets with many files (~ 1000 Java files in a single library)
   */
  private static void modifyTargetWithManyFiles(Path projectPath) {
    File dir = projectPath.toFile();
    if (!(dir.exists() && dir.isDirectory())) {
      System.err.format(
          "Project dir (%s) does not contain code for modification.\n", projectPath.toString());
      return;
    }
    try {
      for (int i = 0; i < SIZE_MANY_FILES; ++i) {
        JavaCodeGeneratorHelper.writeRandomClassToDir(
            /* addExtraMethod = */ true, "RandomClass0", "com.example.generated", projectPath);
      }
    } catch (IOException e) {
      System.err.println("Error modifying targets with many files: " + e.getMessage());
    }
  }


  /**
   * Target type 3: Create targets with a few long chained dependencies (A -> B -> C -> … -> Z)
   */
  private static void createTargetWithLongChainedDeps(Path projectPath) {
    if (checkPathExists(projectPath)) {
      return;
    }

    try {
      Files.createDirectories(projectPath);

      int count = SIZE_LONG_CHAINED_DEPS;

      // Call next one for 0..(count-2)
      for (int i = 0; i < count - 1; ++i) {
        JavaCodeGeneratorHelper.targetWithNextHelper(false, i, true, projectPath);
        JavaCodeGeneratorHelper.buildFileWithNextDeps(
            i, "    deps=[ \":Deps" + (i + 1) + "\" ],\n", projectPath);
      }
      // Don't call next one for (count-1)
      JavaCodeGeneratorHelper.targetWithNextHelper(false, count - 1, false, projectPath);
      JavaCodeGeneratorHelper.buildFileWithNextDeps(count - 1, "", projectPath);

      JavaCodeGeneratorHelper.writeMainClassToDir("com.example.generated", projectPath);

      String deps = "    deps=[ \":Deps0\" ],\n";
      JavaCodeGeneratorHelper.buildFileWithMainClass(TARGET_LONG_CHAINED_DEPS, deps, projectPath);
    } catch (IOException e) {
      System.err.println(
          "Error creating targets with a few long chained dependencies: " + e.getMessage());
    }
  }

  /**
   * Target type 3: Modify targets with a few long chained dependencies (A -> B -> C -> … -> Z)
   */
  private static void modifyTargetWithLongChainedDeps(Path projectPath) {
    File dir = projectPath.toFile();
    if (!(dir.exists() && dir.isDirectory())) {
      System.err.format(
          "Project dir (%s) does not contain code for modification.\n", projectPath.toString());
      return;
    }
    try {
      JavaCodeGeneratorHelper.targetWithNextHelper(
          true, (SIZE_LONG_CHAINED_DEPS + 1) >> 1, true, projectPath);
    } catch (IOException e) {
      System.err.println(
          "Error modifying targets with a few long chained dependencies: " + e.getMessage());
    }
  }

  /**
   * Target type 4: Create targets with lots of parallel dependencies (A -> B, C, D, E, F, G, H)
   */
  private static void createTargetWithParallelDeps(Path projectPath) {
    if (checkPathExists(projectPath)) {
      return;
    }

    try {
      Files.createDirectories(projectPath);

      int count = SIZE_PARALLEL_DEPS;

      // parallel dependencies B~Z
      for (int i = 1; i < count; ++i) {
        JavaCodeGeneratorHelper.writeRandomClassToDir(
            false, "Deps" + i, "com.example.deps" + i, projectPath);
        JavaCodeGeneratorHelper.buildFileWithNextDeps(i, "", projectPath);
      }

      // A(Main)
      JavaCodeGeneratorHelper.parallelDepsMainClassHelper(count, projectPath);

      String deps = "    deps=[ ";
      for (int i = 1; i < count; ++i) {
        deps += "\":Deps" + i + "\", ";
      }
      deps += "], \n";
      JavaCodeGeneratorHelper.buildFileWithMainClass(TARGET_PARALLEL_DEPS, deps, projectPath);
    } catch (IOException e) {
      System.err.println(
          "Error creating targets with lots of parallel dependencies: " + e.getMessage());
    }
  }

  /**
   * Target type 4: Modify targets with lots of parallel dependencies (A -> B, C, D, E, F, G, H)
   */
  private static void modifyTargetWithParallelDeps(Path projectPath) {
    File dir = projectPath.toFile();
    if (!(dir.exists() && dir.isDirectory())) {
      System.err.format(
          "Project dir (%s) does not contain code for modification.\n", projectPath.toString());
      return;
    }
    try {
      JavaCodeGeneratorHelper.writeRandomClassToDir(
          true, "Deps1", "com.example.deps1", projectPath);
    } catch (IOException e) {
      System.err.println(
          "Error creating targets with lots of parallel dependencies: " + e.getMessage());
    }
  }

  private static boolean checkPathExists(Path path) {
    File dir = path.toFile();
    if (dir.exists()) {
      System.err.println("File or directory exists, not rewriting it: " + path.toString());
      return true;
    }

    return false;
  }

}
