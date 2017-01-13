package com.google.devtools.build.benchmark;

public interface BuildCase {

  ImmutableList<BuildTargetConfig> getBuildTargetConfigs();

  ImmutatbleList<String> getCodeVersions(Builder builder, String from, String to);
}