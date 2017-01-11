package com.google.devtools.build.benchmark;

import com.google.devtools.build.benchmark.BuildData.BuildConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Builder {
  Path getBuildBinary(String codeVersion) throws IOException;

  /**
   * @return command formed with a list of arguments.
   */
  List<String> getCommandFromConfig(BuildConfig config);

  /**
   * Build the given buildConfig using the given binary.
   * @return elapsed time, -1 for build failure.
   */
  double buildAndGetElapsedTime(Path buildBinary, List<String> args);

  void clean();
}
