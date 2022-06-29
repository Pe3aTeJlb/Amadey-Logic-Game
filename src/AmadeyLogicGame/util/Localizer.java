package AmadeyLogicGame.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.*;

public class Localizer {

    private String bundleName;

    public Localizer(String bundlename){
        bundleName = "AmadeyLogicGame/resources/localization/" +bundlename;
    }

    public void changeBundle(String bundlename){
        bundleName = "AmadeyLogicGame/resources/localization/" +bundlename;
    }

    private static final ObjectProperty<Locale> locale;

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> setLocale(newValue));
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        localeProperty().set(locale);
    }

    private static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(
                Locale.ENGLISH,
                new Locale("ru","RU")
        ));
    }

    public static StringBinding getLocaleTitle(){
        return Bindings.createStringBinding(() -> locale.getValue().getDisplayName(locale.getValue()), locale);
    }

    public static StringBinding getComplexTitleForLocale(Locale l){
        return Bindings.createStringBinding(() -> l.getDisplayName(l)
                +"/"+l.getDisplayName(Locale.getDefault()), locale);
    }


    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }


    public StringBinding castToBind(final String string){
        return Bindings.createStringBinding(() -> string);
    }

    public String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, getLocale());
        if(bundle.getString(key) != null) {
            return MessageFormat.format(bundle.getString(key), args);
        }else{ return null;}
    }


}
