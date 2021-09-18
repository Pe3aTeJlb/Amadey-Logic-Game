package AmadeyLogicGame.Util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconsManager {

    public static final Image AmadayLogicGame = new Image("AmadeyLogicGame/resources/images/logo.png");

    private static final String path = "AmadeyLogicGame/resources/images";

    private static final String logoPath = "AmadeyLogicGame/resources/images";

    public static ImageView getIcon(String name) {
        return getImageView(name);
    }

    public static ImageView getImageView(String name) {

        Image img = new Image(path + "/" + name);
        return new ImageView(img);

    }

    public static Image getImage(String name){

        Image img = new Image(path + "/" + name);
        return img;

    }

    public static Image getLogo(String name){

        Image img = new Image(logoPath + "/" + name);
        return img;

    }

}
