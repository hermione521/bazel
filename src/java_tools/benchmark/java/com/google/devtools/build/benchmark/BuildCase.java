package com.google.devtools.build.benchmark;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.benchmark.BuildData.BuildTargetConfig;

import java.io.IOException;

public interface BuildCase {

  ImmutableList<BuildTargetConfig> getBuildTargetConfigs();

  ImmutableList<String> getCodeVersions(Builder builder, String from, String to) throws IOException;
}