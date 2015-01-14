package com.sonarsource.jgit;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters()
public class MainCommand {

  @Parameter(names = "--help", help = true)
  private boolean help;

  public boolean isHelp() {
    return help;
  }

}
