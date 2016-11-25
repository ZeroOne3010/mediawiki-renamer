package zeroone3010.mediawiki.renamer;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Scanner;

public class WikiRenamerRunner {

    public static void main(final String... args) throws LoginException {
        validateArgs(args);
        displayInstructions();
        final MediaWikiBot mediaWikiBot = new MediaWikiBot(args[2]);
        mediaWikiBot.login(args[0], args[1]);

        final WikiRenamer renamer = new WikiRenamer(mediaWikiBot);
        final String categoryName = readInput("Please enter the name of a category:");
        final String unless = readInput("Define an exception rule -- ignore pages whose name contain this string:");
        final String append = readInput("Define a string to append to the page names:");
        final List<String> articleNames = renamer.findCategoryPages(categoryName);
        final RenameRules renameRules = renamer.prepareRename(articleNames,
                name -> !name.contains(unless),
                name -> String.format("%s%s", name, append));

        System.out.println("About to rename articles like this:");
        renameRules.getRenamings()
                .forEach((oldName, newName) -> System.out.println(String.format("\"%s\" -> \"%s\"", oldName, newName)));
        System.out.println("\nThe following articles will not be renamed:");
        renameRules.getWillNotBeRenamed()
                .forEach(name -> System.out.println(String.format("\"%s\"", name)));

        if (!readBooleanInput("Does this look good?")) {
            System.out.println("Quitting without renaming.");
            System.exit(0);
        }
        System.out.println(String.format("Renaming %d articles...", renameRules.getRenamings().size()));

        final long startTime = System.currentTimeMillis();
        renamer.applyRename(renameRules, null);
        final long endTime = System.currentTimeMillis();

        System.out.println(String.format("Completed in %d milliseconds.", endTime - startTime));
    }

    private static void displayInstructions() {
        System.out.println("This application will help you rename several wiki articles at once.");
        System.out.println("Here's how it's going to work. You'll first be prompted for the name");
        System.out.println("of a category that contains the articles you intend to rename. Next you");
        System.out.println("may define an exception rule: a string that may appear in the name of");
        System.out.println("a page, which will make the page be ignored in the renaming process.");
        System.out.println("Finally you'll be prompted for a string to be appended into the page");
        System.out.println("names. The application then shows you its plan and you will need to");
        System.out.println("accept or reject that plan. If you accept, the application will");
        System.out.println("perform the renaming process.");
    }

    private static boolean readBooleanInput(final String question) {
        final String input = readInput(String.format("%s [y/n] ", question));
        if (input.matches("[ynYN]")) {
            return "y".equalsIgnoreCase(input);
        }
        return readBooleanInput(question);
    }

    private static String readInput(final String question) {
        final Scanner reader = new Scanner(System.in);
        System.out.println(question);
        return reader.nextLine();
    }

    private static void validateArgs(final String... args) {
        if (args == null || args.length != 3) {
            System.out.println("Invalid parameters: expected [username] [password] [wikiUrl]");
            System.out.println("Example: java -jar WikiRenamer.jar snafu foobar http://en.wikipedia.org");
            throw new IllegalArgumentException("Invalid parameters");
        }
    }
}
