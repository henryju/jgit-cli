package com.sonarsource.jgit;

public interface SubCommand {

  void execute(MainCommand main);
}
