package com.google.devtools.build.benchmark.codegenerator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import javax.lang.model.element.Modifier;

/**
 * Helper class of {@code JavaCodeGenerator}
 */
public class JavaCodeGeneratorHelper {

  private static final MethodSpec randomMethod = genRandomMethod("PrintSth");
  private static final MethodSpec somethingElseMethod = genRandomMethod("PrintSthElse");

  private static MethodSpec genRandomMethod(String methodName) {
    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(void.class)
        .addStatement("$T rand = new Random()", Random.class)
        .addStatement("int n = rand.nextInt(100)")
        .addStatement("$T.out.format($S, $S, $L)", System.class,
            "This is method(%s) with random number(%d)\n", methodName, "n")
        .build();
  }

  private static TypeSpec genRandomClass(boolean addExtraMethod, String className) {

    TypeSpec.Builder klassBuilder = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addMethod(randomMethod);

    if (addExtraMethod) {
      klassBuilder.addMethod(somethingElseMethod);
    }

    return klassBuilder.build();
  }

  private static TypeSpec genMainClass() {
    MethodSpec method = MethodSpec.methodBuilder("main")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(void.class)
        .addParameter(String[].class, "args")
        .build();

    return TypeSpec.classBuilder("Main")
        .addModifiers(Modifier.PUBLIC)
        .addMethod(method)
        .build();
  }

  private static void writeClassToDir(
      TypeSpec klass, String packageName, Path path) throws IOException {
    JavaFile javaFile = JavaFile.builder(packageName, klass)
        .build();
    try {
      javaFile.writeTo(path);
    } catch (IOException e) {
      throw new IOException("Failed to write java file", e);
    }
  }

  private static void createAndAppendFile(Path path, String content) throws IOException {
    try {
      if (!Files.exists(path)) {
        Files.createFile(path);
      }
      Files.write(path, content.getBytes(), StandardOpenOption.APPEND);
    }catch (IOException e) {
      throw new IOException("Write to BUILD file failed", e);
    }
  }

  static void writeRandomClassToDir(boolean addExtraMethod,
      String className, String packageName, Path projectPath) throws IOException {
    TypeSpec klass = genRandomClass(addExtraMethod, className);
    writeClassToDir(klass, packageName, projectPath);
  }

  static void writeMainClassToDir(String packageName, Path projectPath) throws IOException {
    TypeSpec main = genMainClass();
    writeClassToDir(main, packageName, projectPath);
  }

  static void buildFileWithNextDeps(int index, String deps, Path projectPath) throws IOException {
    Path buildFilePath = projectPath.resolve("BUILD");

    String buildFileContent =
        "java_library(\n" +
            "    name=\"Deps" + index + "\",\n" +
            "    srcs=glob([ \"com/example/deps" + index + "/*.java\" ]),\n" +
            deps +
            "    visibility=[ \"//visibility:public\" ],\n" +
            ")\n";

    createAndAppendFile(buildFilePath, buildFileContent);
  }

  static void buildFileWithMainClass(
      String targetName, String deps, Path projectPath) throws IOException {
    Path buildFilePath = projectPath.resolve("BUILD");

    String buildFileContent =
        "java_binary(\n" +
            "    name=\"" + targetName + "\",\n" +
            "    srcs=glob([ \"com/example/generated/*.java\" ]),\n" +
            "    main_class=\"com.example.generated.Main\",\n" +
            deps +
            ")\n";

    createAndAppendFile(buildFilePath, buildFileContent);
  }

  static void targetWithNextHelper(
      boolean addExtraMethod, int index, boolean callNext, Path projectPath) throws IOException {
    ClassName nextClass = ClassName.get("com.example.deps" + (index + 1), "Deps" + (index + 1));

    MethodSpec callNextMethod = MethodSpec.methodBuilder("CallNext")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(void.class)
        .addStatement("$T.PrintSth()", nextClass)
        .build();

    TypeSpec.Builder klassBuilder = TypeSpec.classBuilder("Deps" + index)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addMethod(randomMethod);
    if (addExtraMethod) {
      klassBuilder.addMethod(somethingElseMethod);
    }
    if (callNext) {
      klassBuilder.addMethod(callNextMethod);
    }
    TypeSpec klass = klassBuilder.build();

    writeClassToDir(klass, "com.example.deps" + index, projectPath);
  }

  static void parallelDepsMainClassHelper(int count, Path projectPath) throws IOException {
    MethodSpec.Builder callDepsBuilder = MethodSpec.methodBuilder("main")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(String[].class, "args")
        .returns(void.class);
    for (int i = 1; i < count; ++i) {
      ClassName callingClass = ClassName.get("com.example.deps" + i, "Deps" + i);
      callDepsBuilder.addStatement("$T.PrintSth()", callingClass);
    }
    MethodSpec callDeps = callDepsBuilder.build();
    TypeSpec klass = TypeSpec.classBuilder("Main")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addMethod(callDeps)
        .build();
    writeClassToDir(klass, "com.example.generated", projectPath);
  }
}