package zeroone3010.mediawiki.renamer;

import net.sourceforge.jwbf.mediawiki.actions.editing.MovePage;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class WikiRenamer {
    private final MediaWikiBot bot;

    public WikiRenamer(final MediaWikiBot bot) {
        this.bot = bot;
    }

    public void renamePage(final String from, final String to, final String reason) {
        final MovePage movePage = new MovePage(bot, from, to, reason, true, false);
        bot.getPerformedAction(movePage);
    }

    public List<String> findCategoryPages(final String categoryName) {
        final CategoryMembersSimple categoryMembers = new CategoryMembersSimple(bot, categoryName);
        return StreamSupport.stream(categoryMembers.spliterator(), false).collect(toList());
    }

    public RenameRules prepareRename(final List<String> pages,
                                     final Predicate<String> renameCriteria,
                                     final Function<String, String> renameRule) {
        final Map<String, String> renamings = pages.stream()
                .filter(renameCriteria)
                .collect(toMap(page -> page, renameRule::apply));
        final List<String> willNotBeRenamed = pages.stream()
                .filter(renameCriteria.negate())
                .collect(toList());

        return new RenameRules(pages, willNotBeRenamed, renamings);
    }

    public void applyRename(final RenameRules rules, final String reason) {
        rules.getRenamings().forEach((originalName, newName) -> renamePage(originalName, newName, reason));
    }
}
