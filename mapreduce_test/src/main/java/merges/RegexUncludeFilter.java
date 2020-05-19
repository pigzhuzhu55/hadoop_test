package merges;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class RegexUncludeFilter implements PathFilter {
    private final String regex;
    public RegexUncludeFilter(String regex){
        this.regex = regex;
    }

    public boolean accept(Path path) {
        boolean flag = path.toString().matches(regex);
        return !flag;
    }
}
