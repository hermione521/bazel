package com.google.devtools.build.benchmark;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.benchmark.BuildData.BuildTargetConfig;

import java.io.IOException;

public class BazelBuildCase implements BuildCase {

  private static final ImmutableList<String> BUILD_TARGET_NAMES = ImmutableList.of(
      "AFewFiles",
      "ManyFiles",
      "LongChainedDeps",
      "ParallelDeps");

  private ImmutableList<BuildTargetConfig> buildTargetConfigs = null;

  @Override
  public ImmutableList<String> getCodeVersions(Builder builder, String from, String to) throws IOException {
    return builder.getCodeVersionsBetween(from, to);
  }

  @Override
  public ImmutableList<BuildTargetConfig> getBuildTargetConfigs() {
    if (buildTargetConfigs != null) {
      return buildTargetConfigs;
    }

    ImmutableList.Builder<BuildTargetConfig> resultBuilder = ImmutableList.builder();

    // BuildTargetConfig = EnvironmentConfig * TargetConfig
    ImmutableList<BuildTargetConfig.Builder> configList = ImmutableList.of(
        fullCleanBuildConfig(),
        incrementalBuildConfig());
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

    buildTargetConfigs = resultBuilder.build();
    return buildTargetConfigs;
  }

  private BuildTargetConfig.Builder fullCleanBuildConfig() {
    return BuildTargetConfig.newBuilder()
        .setDescription("Full clean build")
        .setCleanBeforeBuild(true)
        .setIncremental(false);
  }

  private BuildTargetConfig.Builder incrementalBuildConfig() {
    return BuildTargetConfig.newBuilder()
        .setDescription("Incremental build")
        .setCleanBeforeBuild(false)
        .setIncremental(true);
  }

}
