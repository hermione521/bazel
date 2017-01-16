package com.google.devtools.build.benchmark;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.benchmark.BuildData.BuildCaseResult;
import com.google.devtools.build.benchmark.BuildData.BuildGroupResult;
import com.google.devtools.build.benchmark.BuildData.BuildTargetConfig;
import com.google.devtools.build.benchmark.BuildData.BuildTargetResult;
import com.google.devtools.build.benchmark.codegenerator.JavaCodeGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuildGroupRunner {

  private static final Logger logger = Logger.getLogger(BuildGroupRunner.class.getName());

  private static final String GENERATED_CODE_FOR_COPY_DIR = "GeneratedCodeForCopy";
  private static final String GENERATED_CODE_DIR = "GeneratedCode";
  private static final String BUILDER_DIR = "BuilderBazel";

  private final Path workspace;
  private Builder builder = null;

  public BuildGroupRunner(Path workspace) {
    this.workspace = workspace;
  }

  public BuildGroupResult run(String from, String to) {
    BuildCase buildCase = new BazelBuildCase();
    ImmutableList<BuildTargetConfig> buildTargetConfigs = buildCase.getBuildTargetConfigs();

    try {
      prepareBuilder();
    } catch (IOException e) {
      logExceptionAndExit(e);
    }
    ImmutableList<String> codeVersions = null;
    try {
      codeVersions = buildCase.getCodeVersions(builder, from, to);
    } catch (IOException e) {
      logExceptionAndExit(e);
    }

    BuildGroupResult.Builder buildGroupResultBuilder = BuildGroupResult.newBuilder();
    boolean lastIsIncremental = true;
    for (String version : codeVersions) {
      Path buildBinary = null;
      try {
        buildBinary = builder.getBuildBinary(version);
      } catch (IOException e) {
        logExceptionAndExit(e);
      }

      BuildCaseResult.Builder buildCaseResultBuilder = BuildCaseResult.newBuilder();
      buildCaseResultBuilder.setCodeVersion(version);

      for (BuildTargetConfig config : buildTargetConfigs) {
        if (lastIsIncremental && !config.getIncremental()) {
          prepareGeneratedCode();
        }
        if (!lastIsIncremental && config.getIncremental()) {
          JavaCodeGenerator.modifyExistingProject(workspace.resolve(GENERATED_CODE_DIR).toString(), true, true, true, true);
        }
        if (config.getCleanBeforeBuild()) {
          try {
            builder.clean();
          } catch (IOException e) {
            logExceptionAndExit(e, "Target requires cleaning but failed: ");
          }
        }
        double elapsedTime = -1;
        try {
          elapsedTime = builder.buildAndGetElapsedTime(buildBinary, builder.getCommandFromConfig(config));
          System.out.println(elapsedTime);
        } catch (IOException e) {
          logExceptionAndExit(e, "Failed to build target: ");
        }

        buildCaseResultBuilder.addBuildTargetResults(
            BuildTargetResult.newBuilder().setResult(elapsedTime).setConfig(config).build());
      }

      buildGroupResultBuilder.addBuildCaseResults(buildCaseResultBuilder.build());
    }
  }

  private void prepareBuilder() throws IOException {
    builder = new BazelBuilder(workspace.resolve(GENERATED_CODE_DIR), workspace.resolve(BUILDER_DIR));
    builder.prepare();
  }

  // TODO(yueg): configurable target
  private void prepareGeneratedCode() {
    Path generatedCodePath = workspace.resolve(GENERATED_CODE_DIR);
    if (generatedCodePath.toFile().exists()) {
      // removeTreeUnder
    }

    Path copyDir = workspace.resolve(GENERATED_CODE_FOR_COPY_DIR);
    if (copyDir.toFile().exists()) {
      // copy directory
    } else {
      JavaCodeGenerator.generateNewProject(generatedCodePath.toString(), true, true, true, true);
    }
  }

  private void logExceptionAndExit(IOException e) {
    logger.log(Level.SEVERE, e.getMessage());
    System.exit(1);
  }

  private void logExceptionAndExit(IOException e, String message) {
    logger.log(Level.SEVERE, message + e.getMessage());
    System.exit(1);
  }
}