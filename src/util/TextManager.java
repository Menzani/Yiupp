package it.menzani.yiupp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextManager {

    private static final Pattern UNIX_SEPARATOR = Pattern.compile("\n", Pattern.LITERAL);
    private static final Pattern CLASSIC_MAC_SEPARATOR = Pattern.compile("\r", Pattern.LITERAL);

    private TextManager() {

    }

    public static String fillTemplate(String templateText, TemplateVariable... variables) {
        for (TemplateVariable variable : variables) {
            templateText = variable.fill(templateText);
        }
        return templateText;
    }

    public static String collapseHtml(CharSequence htmlText) {
        return CLASSIC_MAC_SEPARATOR.matcher(UNIX_SEPARATOR.matcher(htmlText).replaceAll(Matcher.quoteReplacement("")))
                .replaceAll(Matcher.quoteReplacement(""));
    }

}
