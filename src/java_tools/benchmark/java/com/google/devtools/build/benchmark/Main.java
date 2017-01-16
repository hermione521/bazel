package com.google.devtools.build.benchmark;

import com.google.devtools.build.benchmark.BuildData.BuildGroupResult;
import com.google.devtools.common.options.Options;
import com.google.devtools.common.options.OptionsParsingException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {

    BenchmarkOptions opt = null;
    try {
      opt = Options.parse(BenchmarkOptions.class, args).getOptions();
    } catch (OptionsParsingException e) {
      logger.log(Level.SEVERE, e.getMessage());
      System.exit(1);
    }

    Path workspace = Paths.get(opt.workspace);
    if (!workspace.toFile().exists()) {
      workspace.toFile().mkdirs();
    }
    BuildGroupRunner runner = new BuildGroupRunner(workspace);
    BuildGroupResult result = runner.run(opt.from, opt.to);
    // store data
  }
}