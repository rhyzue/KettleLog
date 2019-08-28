import javafx.util.*;
import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.control.skin.ButtonSkin;

public class FadeButtonSkin extends ButtonSkin {

    public FadeButtonSkin(Button control) {
        super(control);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
        fadeIn.setNode(control);
        fadeIn.setToValue(1);
        control.setOnMouseEntered(e -> fadeIn.playFromStart());

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        fadeOut.setNode(control);
        fadeOut.setToValue(0.5);
        control.setOnMouseExited(e -> fadeOut.playFromStart());

        control.setOpacity(0.5);
    }

}