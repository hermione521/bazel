package com.google.devtools.build.benchmark;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.devtools.build.lib.shell.Command;
import com.google.devtools.build.lib.shell.CommandException;
import com.google.devtools.build.lib.shell.CommandResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BazelBuilder implements Builder{

  private static final String GENERATED_CODE_DIR = "GeneratedCode";
  private static final String BAZEL_BINARY_PATH = "bazel-bin/src/bazel";

  private Path buildBinary = null;
  private Path generatedCodeDir;
  private Path builderDir;
  private String currentCodeVersion = "";

  BazelBuilder(Path workspace, Path builderDir) {
    generatedCodeDir = workspace.resolve(GENERATED_CODE_DIR);
    this.builderDir = builderDir;
  }

  @Override
  public Path getBuildBinary(String codeVersion) throws IOException {
    if (buildBinary != null && currentCodeVersion.equals(codeVersion)) {
      return buildBinary;
    }

    // git checkout codeVersion
    String[] checkoutCommand = {"git", "checkout", codeVersion};
    Command cmd = new Command(buildBazelCommand, null, builderDir.toFile());
    try {
      cmd.execute();
    } catch (CommandException e) {
      throw new IOException("Failed to checkout code version " + codeVersion, e);
    }

    // bazel build src:bazel
    String[] buildBazelCommand = {"bazel", "build", "src:bazel"};
    cmd = new Command(buildBazelCommand, null, builderDir.toFile());
    CommandResult res;
    try {
      res = cmd.execute();
    } catch (CommandException e) {
      throw new IOException("Failed to build bazel", e);
    }

    // Get binary path
    String output = new String(res.getStdout(), UTF_8).trim();
    if (output.contains(BAZEL_BINARY_PATH)) {
      buildBinary = builderDir.resolve(BAZEL_BINARY_PATH);
      currentCodeVersion = codeVersion;
      return buildBinary;
    }
    // else build failed
    throw new IOException("Bazel binary " + BAZEL_BINARY_PATH + "is not generated.");
  }

  @Override
  public List<String> getCommandFromConfig(BuildConfig config) {
    return null;
  }

  @Override
  public double buildAndGetElapsedTime(Path buildBinary, List<String> args) {
    return 0;
  }

  @Override
  public void clean() {
    String[] cleanCommand = {"bazel", "clean"};
    Command cmd = new Command(cleanCommand, null, builderDir.toFile());
    try {
      cmd.execute();
    } catch (CommandException e) {
      throw new IOException("Failed to run `bazel clean`", e);
    }
  }
}
