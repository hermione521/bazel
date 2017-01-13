package com.google.devtools.build.benchmark.codegenerator;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class GeneratorOptions extends OptionsBase {

  @Option(
      name = "mode",
      defaultValue = "",
      category = "generator",
      valueHelp = "{'new', 'modify'}",
      help = "'new' for generating new code, or 'modify' for modifying existing code."
  )
  public String mode;

  @Option(
      name = "output_dir",
      defaultValue = "",
      category = "generator",
      valueHelp = "path",
      help = "directory where we put generated code or modify the existing code."
  )
  public String outputDir;

  @Option(
      name = "a_few_files",
      defaultValue = "false",
      category = "generator",
      help = "if we generate a package with a few files."
  )
  public boolean aFewFiles;

  @Option(
      name = "many_files",
      defaultValue = "false",
      category = "generator",
      help = "if we generate a package with many files."
  )
  public boolean manyFiles;

  @Option(
      name = "long_chained_deps",
      defaultValue = "false",
      category = "generator",
      help = "if we generate a package with long chained dependencies."
  )
  public boolean longChainedDeps;

  @Option(
      name = "parallel_deps",
      defaultValue = "false",
      category = "generator",
      help = "if we generate a package with parallel dependencies."
  )
  public boolean paralledDeps;

}
