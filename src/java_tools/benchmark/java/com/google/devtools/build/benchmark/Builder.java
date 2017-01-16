package com.google.devtools.build.benchmark;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.benchmark.BuildData.BuildTargetConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Builder {

  void prepare() throws IOException;

  Path getBuildBinary(String codeVersion) throws IOException;

  ImmutableList<String> getCodeVersionsBetween(String from, String to) throws IOException;

  /**
   * @return command formed with a list of arguments.
   */
  ImmutableList<String> getCommandFromConfig(BuildTargetConfig config);

  /**
   * Build the given buildConfig using the given binary.
   * @return elapsed time, -1 for build failure.
   */
  double buildAndGetElapsedTime(Path buildBinary, ImmutableList<String> args) throws IOException;

  void clean() throws IOException;
}
