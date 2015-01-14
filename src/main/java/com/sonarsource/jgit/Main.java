package com.sonarsource.jgit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws GitAPIException {

    MainCommand cm = new MainCommand();
    JCommander jc = new JCommander(cm);

    Map<String, SubCommand> subs = new LinkedHashMap<>();
    subs.put("blame", new BlameCommand());

    for (Map.Entry<String, SubCommand> entry : subs.entrySet()) {
      jc.addCommand(entry.getKey(), entry.getValue());
    }

    try {
      jc.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      jc.usage();
      System.exit(1);
    }

    if (cm.isHelp()) {
      jc.usage();
    } else {
      subs.get(jc.getParsedCommand()).execute(cm);
    }
  }

}
