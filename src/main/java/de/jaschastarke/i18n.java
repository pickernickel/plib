package de.jaschastarke;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.IncompleteArgumentException;

public class i18n {
    protected ResourceBundle bundle;
    public i18n(String bundleName, Locale locale) {
        useBundle(bundleName, locale);
    }
    public i18n(String bundleName) {
        useBundle(bundleName, null);
    }
    public i18n(Locale locale) {
        useBundle(null, locale);
    }
    public i18n() {
        useBundle(null, null);
    }
    
    protected void useBundle(String bundleName, Locale locale) {
        bundle = new MultipleResourceBundle(locale, new String[]{"de.jaschastarke.bukkit.messages", bundleName});
    }
    
    public ResourceBundle getResourceBundle() {
        return bundle;
    }
    
    public String trans(String msg, Object... objects) {
        msg = bundle.getString(msg);
        if (objects.length > 0)
            msg = MessageFormat.format(msg, objects);
        return msg;
    }
    
    public static Locale getLocaleFromString(String locale) {
        Matcher match = Pattern.compile("^([a-z]+)(?:[-_]([A-Za-z]+)(?:[-_](.*))?)?$").matcher(locale);
        if (!match.matches())
            throw new IncompleteArgumentException("Locale-String could not be interpreted: "+locale);
        if (match.group(3) != null)
            return new Locale(match.group(1), match.group(2), match.group(3));
        else if (match.group(2) != null)
            return new Locale(match.group(1), match.group(2));
        else
            return new Locale(match.group(1));
    }
}
