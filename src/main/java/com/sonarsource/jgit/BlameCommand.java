package com.sonarsource.jgit;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Parameters(commandDescription = "Show what revision and author last modified each line of a file")
public class BlameCommand implements SubCommand {

  @Parameter(description = "<file>", required = true, arity = 1)
  private List<String> files;

  public void execute(MainCommand main) {

    File currentDir = new File(".");
    Repository repo = buildRepository(currentDir);
    try {
      Git git = Git.wrap(repo);
      File gitBaseDir = repo.getWorkTree();
      blame(git, gitBaseDir, files.get(0));
    } catch (GitAPIException e) {
      throw new RuntimeException("Unable to blame", e);
    } finally {
      repo.close();
    }
  }

  private static void blame(Git git, File gitBaseDir, String path) throws GitAPIException {
    org.eclipse.jgit.blame.BlameResult blameResult = git.blame()
      // Equivalent to -w command line option
      .setTextComparator(RawTextComparator.WS_IGNORE_ALL)
      .setFilePath(path).call();
    RawText resultContents = blameResult.getResultContents();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    for (int i = 0; i < resultContents.size(); i++) {
      String revision = blameResult.getSourceCommit(i) != null ? blameResult.getSourceCommit(i).getName().substring(0, 8) : "<null>";
      String code = resultContents.getString(i);
      PersonIdent sourceAuthor = blameResult.getSourceAuthor(i);
      String email = sourceAuthor != null ? StringUtils.rightPad(sourceAuthor.getEmailAddress(), 24).substring(0, 24) : "<null>";
      String commitDate = sourceAuthor != null ? sdf.format(sourceAuthor.getWhen()) : "<null>";
      String line = "" + (i + 1);
      System.out.println(String.format("%s (%s %s %s) %s", revision, email, commitDate, StringUtils.leftPad(line, 4), code));
    }
  }

  private static Repository buildRepository(File basedir) {
    try {
      return new RepositoryBuilder()
        .findGitDir(basedir)
        .setMustExist(true)
        .build();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to open Git repository", e);
    }
  }

}
