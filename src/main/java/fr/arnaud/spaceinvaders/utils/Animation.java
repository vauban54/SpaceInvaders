package fr.arnaud.spaceinvaders.utils;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Animation {

    public static void animateLogoSpaceInvaders(ImageView imgLogo, double fromY, double toY,
                                                double delay, double fromAlpha, double toAlpha, double alphaDelay) {
        TranslateTransition animation = new TranslateTransition(Duration.millis(delay), imgLogo);
        animation.setFromY(fromY);
        animation.setToY(toY);
        animation.setInterpolator(Interpolator.EASE_OUT);
        animation.play();

        FadeTransition fade = new FadeTransition(Duration.millis(alphaDelay));
        fade.setNode(imgLogo);
        fade.setFromValue(fromAlpha);
        fade.setToValue(toAlpha);
        fade.play();
    }
}
