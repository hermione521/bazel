package com.google.devtools.build.benchmark;

public class BuildGroupRunner {

  private static final String GENERATED_CODE_DIR = "GeneratedCode";
  private static final String BUILDER_DIR = "BuilderBazel";

  private final Path workspace;
  private Builder builder = null;

  public BuildGroupRunner(Path workspace) {
    this.workspace = workspace;
  }

  public void run() {
    BuildCase buildCase = new BazelBuildCase();
    ImmutableList<BuildTargetConfig> buildTargetConfigs = BazelBuildCase.getBuildTargetConfigs();

    prepareBuilder();
    ImmutatbleList<String> codeVersions = getCodeVersions(builder, from, to);

    boolean lastIsIncremental = true;
    for (String version : codeVersions) {
      Path buildBinary = builder.getBuildBinary(codeVersions);
      for (BuildTargetConfig config : buildTargetConfigs) {
        if (lastIsIncremental && !config.isIncremental()) {
          prepareGeneratedCode();
        }
        if (!lastIsIncremental && config.isIncremental()) {
          JavaCodeGenerator.modifyExistingProject(workspace.resolve(GENERATED_CODE_DIR), true, true, true, true);
        }
        if (config.isCleanBeforeBuild()) {
          builder.clean();
        }
        double elapsedTime = builder.buildAndGetElapsedTime(buildBinary, builder.getCommandFromConfig(config));
      }
    }
  }

  private prepareBuilder() {
    builder = new BazelBuilder(workspace.resolve(GENERATED_CODE_DIR), workspace.resolve(BUILDER_DIR));
    builder.prepare();
  }

  private prepareGeneratedCode() {

    JavaCodeGenerator.generateNewProject(workspace.resolve(GENERATED_CODE_DIR), true, true, true, true);

  }
}