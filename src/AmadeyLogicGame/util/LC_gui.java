package AmadeyLogicGame.util;

//implement Singleton
public class LC_gui {

    private static final String packageName = "gui";

    private static Localizer lc;

    private LC_gui(){}

    public static Localizer getInstance(){

        if(lc == null){
            lc = new Localizer(packageName);
        }

        return lc;

    }

}
