package AmadeyLogicGame;

import java.util.ResourceBundle;

public class Localizer {

    private ResourceBundle res;

    Localizer(String bundleName){
        res = ResourceBundle.getBundle(bundleName);
    }

    public String Localize(String strName){
        return res.getString("strName");
    }

}
