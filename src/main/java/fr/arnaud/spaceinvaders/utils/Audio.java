package fr.arnaud.spaceinvaders.utils;

import javafx.scene.media.AudioClip;

public class Audio {

    private final AudioClip audioClip;

    public Audio(String soundPath) {
        this.audioClip = new AudioClip(soundPath);
    }

    private void play() {
        this.audioClip.play();
    }

    public static void playSound(String soundPath) {
        Audio audio = new Audio(soundPath);
        audio.play();
    }
}
