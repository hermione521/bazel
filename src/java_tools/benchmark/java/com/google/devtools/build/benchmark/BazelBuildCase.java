package com.google.devtools.build.benchmark;

public class BazelBuildCase implements BuildCase {

  private static final ImmutableList<String> BUILD_TARGET_NAMES = ImmutableList.of(
      "AFewFiles",
      "ManyFiles",
      "LongChainedDeps",
      "ParallelDeps");

  private ImmutableList<BuildTargetConfig> buildTargetConfigs = null;

  @Override
  public ImmutatbleList<String> getCodeVersions(Builder builder, String from, String to) {
    return builder.getCodeVersionsBetween(from, to);
  }

  @Override
  public ImmutableList<BuildTargetConfig> getBuildTargetConfigs() {
    if (buildTargetConfigs != nul) {
      return buildTargetConfigs;
    }

    ImmutableList.Builder<BuildTargetConfig> resultBuilder = ImmutableList.builder();

    // BuildTargetConfig = EnvironmentConfig * TargetConfig
    ImmutableList<BuildTargetConfig.Builder> configList = ImmutableList.of(
        fullCleanBuildConfig(),
        incrementalBuildConfig);
    for (BuildTargetConfig.Builder builder : configList) {
      String description = builder.getDescription();
      for (String targetName : BUILD_TARGET_NAMES) {
        resultBuilder.add(
            builder
                .setBuildTarget(targetName + "/" + targetName)
                .setDescription(description + ", " + targetName)
                .build());
      }
    }

    buildTargetConfigs = resultBuilder.build()
    return buildTargetConfigs;
  }

  private BuildTargetConfig.Builder fullCleanBuildConfig() {
    BuildTargetConfig.Builder builder = BuildTargetConfig.builder();
    builder
        .setDescription("Full clean build")
        .setCleanBeforeBuild(true)
        .setIncremental(false);
    return builder;
  }

  private BuildTargetConfig.Builder incrementalBuildConfig() {
    BuildTargetConfig.Builder builder = BuildTargetConfig.builder();
    builder
        .setDescription("Incremental build")
        .setCleanBeforeBuild(false)
        .setIncremental(true);
    return builder;
  }

}
