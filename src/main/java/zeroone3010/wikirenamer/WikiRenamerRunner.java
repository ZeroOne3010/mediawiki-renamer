package zeroone3010.wikirenamer;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WikiRenamerRunner {

    public static void main(final String... args) throws LoginException {
        validateArgs(args);
        final MediaWikiBot mediaWikiBot = new MediaWikiBot(args[2]);
        mediaWikiBot.login(args[0], args[1]);

        final WikiRenamer renamer = new WikiRenamer(mediaWikiBot);
        final List<String> articleNames = renamer.findCategoryPages("City-states (Civ5)");
        final RenameRules renameRules = renamer.prepareRename(articleNames,
                name -> !name.contains("(Civ"),
                name -> String.format("%s (Civ5)", name));

        System.out.println("About to rename articles like this:");
        renameRules.getRenamings()
                .forEach((oldName, newName) -> System.out.println(String.format("\"%s\" -> \"%s\"", oldName, newName)));
        System.out.println("\nThe following articles will not be renamed:");
        renameRules.getWillNotBeRenamed()
                .forEach(name -> System.out.println(String.format("\"%s\"", name)));

        if (!continuePrompt("Does this look good?")) {
            System.out.println("Quitting without renaming.");
            System.exit(0);
        }
        System.out.println(String.format("Renaming %d articles...", renameRules.getRenamings().size()));

        final long startTime = System.currentTimeMillis();
        renamer.applyRename(renameRules, null);
        final long endTime = System.currentTimeMillis();

        System.out.println(String.format("Completed in %d milliseconds.", endTime - startTime));
    }

    private static boolean continuePrompt(final String question) {
        final Scanner reader = new Scanner(System.in);
        System.out.println(String.format("%s [y/n] ", question));
        final String input = reader.next(Pattern.compile("[ynYN]"));
        return "y".equalsIgnoreCase(input);
    }

    private static void validateArgs(final String... args) {
        if (args == null || args.length != 3) {
            System.out.println("Invalid parameters: expected [username] [password] [wikiUrl]");
            System.out.println("Example: java -jar WikiRenamer.jar snafu foobar http://en.wikipedia.org");
            throw new IllegalArgumentException("Invalid parameters");
        }
    }
}
