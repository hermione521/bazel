package com.google.devtools.build.benchmark.codegenerator;

import com.google.devtools.common.options.Options;
import com.google.devtools.common.options.OptionsParsingException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {

    GeneratorOptions opt = null;
    try {
      opt = Options.parse(GeneratorOptions.class, args).getOptions();
    } catch (OptionsParsingException e) {
      logger.log(Level.SEVERE, e.getMessage());
      System.exit(1);
    }

    // Check mode argument
    if (!("new".equals(opt.mode) || "modify".equals(opt.mode))) {
      System.err.println(Options.getUsage(GeneratorOptions.class));
      System.exit(1);
    }
    // Check output_dir argument
    if ("modify".equals(opt.mode)) {
      File dir = new File(opt.outputDir);
      if (!(dir.exists() && dir.isDirectory())) {
        logger.log(Level.SEVERE, "output_dir (%s) does not contain code for modification.\n", opt.outputDir);
        System.exit(1);
      }
    }
    // Check at least one type of package will be generated
    if (!(opt.aFewFiles || opt.manyFiles || opt.longChainedDeps
        || opt.paralledDeps)) {
      logger.log(Level.SEVERE, "No type of package is specified.");
      System.err.println(Options.getUsage(GeneratorOptions.class));
      System.exit(1);
    }


    // Generate or modify Java code
    if ("new".equals(opt.mode)) {
      JavaCodeGenerator.generateNewProject(opt.outputDir,
          opt.aFewFiles,
          opt.manyFiles,
          opt.longChainedDeps,
          opt.paralledDeps);
    } else {
      JavaCodeGenerator.modifyExistingProject(opt.outputDir,
          opt.aFewFiles,
          opt.manyFiles,
          opt.longChainedDeps,
          opt.paralledDeps);
    }

  }
}
