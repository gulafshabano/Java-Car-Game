/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danielczykjakub_projekt;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import javax.swing.Timer;


/**
 *
 * @author JakubDanielczyk
 */
public class GamePanel extends javax.swing.JPanel {

    private final Car car;
    private final Road road;
    private Timer policeCarsSpawnTimer;
    private Timer policeCarsSpeedIncreaseTimer;
    private ArrayList<PoliceCar> policeCarsList = new ArrayList<>();
    private int policeCarSpawnDelay = 2000;
    private int policeCarSpeed = 100;
    private Random randomizer = new Random();
    private MediaPlayer player;
    private AudioClip soundPlayer;
    private Timer explosionTimer;
    private int explostionTimerCounter = 0;
    private Timer collisionCheckTimer;
    private int score = 0;
    private boolean intelligentPoliceCars = false;
    
    public GamePanel() {
        initComponents();
        setFocusable(true);
        JFXPanel fxPanel = new JFXPanel();
        add(fxPanel);
        car = new Car();
        road = new Road();
        loadGame();
        prepareTimers();
        preparePlayers();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            startGame();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            car.moveLeft();
        } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            car.moveRight();
        }
    }//GEN-LAST:event_formKeyPressed

    
    private void loadGame() {
        add(car);
        add(road);
        road.prepareStripes();
        road.prepareTrees();
        revalidate();
        repaint();
    }
    
    private void prepareTimers() {
        policeCarsSpawnTimer = new Timer(policeCarSpawnDelay, (e) -> {
            addNewPoliceCar();
            removeRedundantPoliceCars();
            revalidate();
            repaint();
        });
        policeCarsSpawnTimer.setInitialDelay(3000);
        
        policeCarsSpeedIncreaseTimer = new Timer(10000, (e) -> {
            if (policeCarSpeed > 26) {
                policeCarSpeed -= 10;
            } else {
                policeCarSpeed = 16;
                intelligentPolliceCars = true;
            }
            road.setTimersSpeed(policeCarSpeed);
        });
        
        explosionTimer = new Timer(50, (e) -> {
                road.nextExplosion();
                explostionTimerCounter++;
                if (explostionTimerCounter > 12) {
                    explosionTimer.stop();
                }
        });
        
        collisionCheckTimer = new Timer(policeCarSpeed, (e) -> {
            if (collisionOccured()) {
                road.explosionPosition = new Point(car.getX(), car.getY());
                explosionTimer.start();
                player.stop();
                soundPlayer.play();
                stopAllTimers();
                showAlert();
            }
        });
    }
    
    private void preparePlayers() {
        URL backGr = getClass().getResource("/resources/music.mp3");
        URL expSound = getClass().getResource("/resources/explosion.wav");
        player = new MediaPlayer(new Media(backGr.toString()));
        player.setOnEndOfMedia(new Runnable() {
            public void run() {
                player.seek(Duration.ZERO);
            }
        });
        
        soundPlayer = new AudioClip(expSound.toString());
    }
    
    private void startGame() {
        road.setTimersSpeed(policeCarSpeed);
        road.startTimers();
        policeCarsSpawnTimer.start();
        policeCarsSpeedIncreaseTimer.start();      
        player.play();
        collisionCheckTimer.start();
    }
    
    private void addNewPoliceCar() {
        PoliceCar policeCar;
        if (intelligentPoliceCars) {
            policeCar = new PoliceCar(getCarCurrentRoadNumber(), policeCarSpeed);
        } else {
            int roadNumber = randomizer.nextInt(3) + 1;
            policeCar = new PoliceCar(roadNumber, policeCarSpeed);
        }
        policeCarsList.add(policeCar);
        remove(road);
        add(policeCar);
        add(road);
        policeCar.startTimer();
    }
    
    private int getCarCurrentRoadNumber() {
        switch (car.getX()) {
            case 75:
                return 1;
            case 175:
                return 2;
            default:
                return 3;
        }
    }
    
    private void removeRedundantPoliceCars() {
        if (policeCarsList.get(0).getY() >= 500) {
            remove(policeCarsList.get(0));
            policeCarsList.remove(0);
            score++;
        }
    }
    
    private boolean collisionOccured() {
        for (PoliceCar policeCarr : policeCarsList) {
            if (Math.abs(policeCarr.getX() - car.getX()) < 50 && Math.abs(policeCarr.getY() - car.getY()) < 100) {
                return true;
            }
        }
        return false;
    }
    
    private void stopAllTimers() {
        policeCarsSpawnTimer.stop();
        policeCarsSpeedIncreaseTimer.stop();
        collisionCheckTimer.stop();
        road.stopAllTimers();
        for (PoliceCar policeCar : policeCarsList) {
            policeCar.stop();
        }
    }
    
    private void showAlert() {
        JOptionPane.showMessageDialog(null, "You lost! Your score was: " + score);
    }
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
