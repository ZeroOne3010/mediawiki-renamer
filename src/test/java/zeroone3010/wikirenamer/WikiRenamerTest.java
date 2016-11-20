package zeroone3010.wikirenamer;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WikiRenamerTest {
    @Test
    public void prepareRename_should_rename_correctly() {
        final WikiRenamer renamer = new WikiRenamer(null);
        final List<String> pages = Arrays.asList("Tyrion", "Jaime", "Tywin");
        final RenameRules rules = renamer.prepareRename(pages, x -> true, name -> name + " Lannister");
        final Map<String, String> expected = new HashMap() {{
            put("Tyrion", "Tyrion Lannister");
            put("Jaime", "Jaime Lannister");
            put("Tywin", "Tywin Lannister");
        }};
        assertThat(rules.getRenamings(), is(expected));
        assertThat(rules.getWillNotBeRenamed(), is(emptyList()));
    }

    @Test
    public void prepareRename_should_rename_nothing_when_nothing_matches() {
        final WikiRenamer renamer = new WikiRenamer(null);
        final List<String> pages = Arrays.asList("Daenerys", "Viserys", "Rhaegar");
        final RenameRules rules = renamer.prepareRename(pages, x -> false, name -> name + " Lannister");
        assertThat(rules.getRenamings(), is(emptyMap()));
        assertThat(rules.getWillNotBeRenamed(), is(pages));
    }

    @Test
    public void prepareRename_should_rename_some_that_match() {
        final WikiRenamer renamer = new WikiRenamer(null);
        final List<String> pages = Arrays.asList("Daenerys", "Robert");
        final RenameRules rules = renamer.prepareRename(pages, x -> "Robert".equals(x), name -> name + " Baratheon");
        assertThat(rules.getRenamings(), is(singletonMap("Robert", "Robert Baratheon")));
        assertThat(rules.getWillNotBeRenamed(), is(singletonList("Daenerys")));
    }
}