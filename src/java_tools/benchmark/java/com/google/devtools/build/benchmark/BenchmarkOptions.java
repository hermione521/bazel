package com.google.devtools.build.benchmark;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class BenchmarkOptions extends OptionsBase {

  @Option(
      name = "workspace",
      defaultValue = "",
      category = "benchmark",
      valueHelp = "path",
      help = "Directory where we put all the code and results."
  )
  public String workspace;

  @Option(
      name = "from",
      defaultValue = "",
      category = "benchmark",
      valueHelp = "string",
      help = "Use code versions from this (not included)."
  )
  public String from;

  @Option(
      name = "to",
      defaultValue = "",
      category = "benchmark",
      valueHelp = "string",
      help = "Use code versions to this (included)."
  )
  public String to;

}
