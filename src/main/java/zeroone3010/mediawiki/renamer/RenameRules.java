package zeroone3010.mediawiki.renamer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RenameRules {
    private final List<String> pages;
    private final List<String> willNotBeRenamed;
    private final Map<String, String> renamings;

    public RenameRules(final List<String> pages,
                       final List<String> willNotBeRenamed,
                       final Map<String, String> renamings) {
        this.pages = Collections.unmodifiableList(pages);
        this.willNotBeRenamed = Collections.unmodifiableList(willNotBeRenamed);
        this.renamings = Collections.unmodifiableMap(renamings);
    }

    public List<String> getPages() {
        return pages;
    }

    public List<String> getWillNotBeRenamed() {
        return willNotBeRenamed;
    }

    public Map<String, String> getRenamings() {
        return renamings;
    }
}
