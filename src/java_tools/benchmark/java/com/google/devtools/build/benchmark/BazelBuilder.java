package com.google.devtools.build.benchmark;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.shell.Command;
import com.google.devtools.build.lib.shell.CommandException;
import com.google.devtools.build.lib.shell.CommandResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BazelBuilder implements Builder{

  private static final String BAZEL_BINARY_PATH = "bazel-bin/src/bazel";

  private Path buildBinary = null;
  private Path generatedCodeDir;
  private Path builderDir;
  private String currentCodeVersion = "";

  BazelBuilder(Path generatedCodeDir, Path builderDir) {
    this.generatedCodeDir = generatedCodeDir;
    this.builderDir = builderDir;
  }

  @Override
  public ImmutableList<String> getCodeVersionsBetween(String from, String to) {
    String[] gitLogCommand = {"git", "log", from + ".." + to, "--pretty=format:'%h'", "--reverse"};
    Command cmd = new Command(gitLogCommand, null, builderDir.toFile());
    CommandResult res;
    try {
      res = cmd.execute();
    } catch (CommandException e) {
      throw new IOException("Failed to get versions with command: " + Joiner.on(" ").join(gitLogCommand), e);
    }

    String output = new String(res.getStdout(), UTF_8).trim();
    String[] parts = output.split("\n");
    return ImmutableList.copyOf(parts);
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
  public ImmutableList<String> getCommandFromConfig(BuildTargetConfig config) {
    return ImmutableList.of("build", config.getBuildTarget(), config.getBuildArgsList);
  }

  @Override
  public double buildAndGetElapsedTime(Path buildBinary, ImmutableList<String> args) throws IOException {
    List<String> cmdList = new ArrayList();
    cmdList.add(buildBinary);
    cmdList.addAll(args);
    String[] cmdArr = new String[cmdList.size()];
    cmdArr = cmdList.toArray(cmdArr);

    // Run build command
    Command cmd = new Command(cmdArr, null, generatedCodeDir.toFile());
    CommandResult res;
    try {
      res = cmd.execute();
    } catch (CommandException e) {
      throw new IOException("Failed to build with command: " + Joiner.on(" ").join(cmdList), e);
    }

    // Get elapsed time from output
    String output = new String(res.getStdout(), UTF_8).trim();
    String pattern = "(?<=INFO: Elapsed time: )[0-9.]+";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(line);

    double elapsedTime = -1;
    if (m.find()) {
      try {
        elapsedTime = Double.parseDouble(m.group(0));
      } catch (NumberFormatException e) {
        throw new IOException("Failed to get elapsed time", e);
      }
    }
    return elapsedTime;
  }

  @Override
  public void clean() throws IOException{
    String[] cleanCommand = {"bazel", "clean"};
    Command cmd = new Command(cleanCommand, null, builderDir.toFile());
    try {
      cmd.execute();
    } catch (CommandException e) {
      throw new IOException("Failed to run `bazel clean`", e);
    }
  }

  @Override
  public void prepare() {
    if (!builderDir.toFile().isDirectory()) {
      Files.delete(builderDir);
    }
    if (Files.notExists(builderDir)) {
      Files.createDirectories(builderDir);

      String[] gitCloneCommand = {"git", "clone", "https://github.com/bazelbuild/bazel.git"};
      Command cmd = new Command(cleanCommand, null, builderDir.toFile());
      try {
        cmd.execute();
      } catch (CommandException e) {
        throw new IOException("Failed to clone bazel", e);
      }
    }
    // Assume the directory is what we need if not empty
  }
}
