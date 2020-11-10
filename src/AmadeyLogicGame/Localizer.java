package AmadeyLogicGame;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer {

    public ResourceBundle res;

    Localizer(String bundleName, Locale l){
        res = ResourceBundle.getBundle(bundleName, l);
    }

    public String Localize(String strName){
        return res.getString(strName);
    }

}
