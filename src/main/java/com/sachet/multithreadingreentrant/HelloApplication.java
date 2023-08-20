package com.sachet.multithreadingreentrant;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HelloApplication extends Application {

    public static class PriceContainer {
        private Lock lock = new ReentrantLock();

        private double bitCoinPrice;
        private double etherPrice;
        private double liteCoinPrice;
        private double bitCoinCashPrice;
        private double ripplePrice;

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        public double getBitCoinPrice() {
            return bitCoinPrice;
        }

        public void setBitCoinPrice(double bitCoinPrice) {
            this.bitCoinPrice = bitCoinPrice;
        }

        public double getEtherPrice() {
            return etherPrice;
        }

        public void setEtherPrice(double etherPrice) {
            this.etherPrice = etherPrice;
        }

        public double getLiteCoinPrice() {
            return liteCoinPrice;
        }

        public void setLiteCoinPrice(double liteCoinPrice) {
            this.liteCoinPrice = liteCoinPrice;
        }

        public double getBitCoinCashPrice() {
            return bitCoinCashPrice;
        }

        public void setBitCoinCashPrice(double bitCoinCashPrice) {
            this.bitCoinCashPrice = bitCoinCashPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }
    }

    public static class PricesUpdater extends  Thread {
        private final PriceContainer priceContainer;
        private final Random random = new Random();

        public PricesUpdater(PriceContainer priceContainer) {
            this.priceContainer = priceContainer;
        }

        @Override
        public void run() {
            while (true) {
                priceContainer.getLock().lock();
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    priceContainer.setEtherPrice(random.nextInt(2000));
                    priceContainer.setRipplePrice(random.nextDouble());
                    priceContainer.setBitCoinPrice(random.nextInt(2000));
                    priceContainer.setLiteCoinPrice(random.nextInt(2000));
                    priceContainer.setBitCoinCashPrice(random.nextInt(2000));
                } finally {
                    priceContainer.getLock().unlock();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        GridPane gridPane = createGridPane();
        Map<String, Label> cryptoPriceLabels = createCryptoPriceLabels();

        double width = 800;
        double height = 700;

        addLabelsToGrid(gridPane, cryptoPriceLabels);
        Rectangle background = createBackgroundRectangle(width, height);

        StackPane root = new StackPane();
        Scene scene = new Scene(root, width, height);
        stage.setTitle("CryptoCurrency Prices!");
        root.getChildren().add(background);
        root.getChildren().add(gridPane);

        PriceContainer priceContainer = new PriceContainer();
        PricesUpdater pricesUpdater = new PricesUpdater(priceContainer);

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (priceContainer.getLock().tryLock()) {
                    try {
                        Label bitCoinLabel = cryptoPriceLabels.get("BTC");
                        bitCoinLabel.setText(String.valueOf(priceContainer.getBitCoinPrice()));

                        Label etherLabel = cryptoPriceLabels.get("ETH");
                        etherLabel.setText(String.valueOf(priceContainer.getEtherPrice()));

                        Label liteCoinLabel = cryptoPriceLabels.get("LTC");
                        liteCoinLabel.setText(String.valueOf(priceContainer.getLiteCoinPrice()));

                        Label bitCoinCashLabel = cryptoPriceLabels.get("BCH");
                        bitCoinCashLabel.setText(String.valueOf(priceContainer.getBitCoinCashPrice()));

                        Label rippleCoinLabel = cryptoPriceLabels.get("RPC");
                        rippleCoinLabel.setText(String.valueOf(priceContainer.getRipplePrice()));
                    }finally {
                        priceContainer.getLock().unlock();
                    }
                }
            }
        };

        pricesUpdater.start();
        animationTimer.start();

        stage.setScene(scene);
        stage.show();
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private Map<String, Label> createCryptoPriceLabels() {
        Label bitCoinPrice = new Label("0");
        bitCoinPrice.setId("BTC");

        Label etherPrice = new Label("0");
        etherPrice.setId("ETH");

        Label liteCoinPrice = new Label("0");
        liteCoinPrice.setId("LTC");

        Label bitCoinCashPrice = new Label("0");
        bitCoinCashPrice.setId("BCH");

        Label ripplePrice = new Label("0");
        ripplePrice.setId("RPC");

        Map<String, Label> map = new HashMap<>();
        map.put("BTC", bitCoinPrice);
        map.put("ETH", etherPrice);
        map.put("LTC", liteCoinPrice);
        map.put("BCH", bitCoinCashPrice);
        map.put("RPC", ripplePrice);
        return map;
    }

    private void addLabelsToGrid(GridPane grid, Map<String, Label> labels) {
        int row = 0;
        for (Map.Entry<String, Label> entry: labels.entrySet()) {
            String cryptoName = entry.getKey();
            Label nameLabel = new Label(cryptoName);
            nameLabel.setTextFill(Color.BLUE);
            nameLabel.setOnMousePressed(event -> nameLabel.setTextFill(Color.RED));
            nameLabel.setOnMouseReleased(event -> nameLabel.setTextFill(Color.RED));

            grid.add(nameLabel, 0, row);
            grid.add(entry.getValue(), 1, row);
            row++;
        }
    }

    private Rectangle createBackgroundRectangle(double width, double height) {
        Rectangle background = new Rectangle(width, height);
        FillTransition transition = new FillTransition(Duration.millis(1000), background, Color.LIGHTBLUE,
                Color.ALICEBLUE);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
        return background;
    }

    public static void main(String[] args) {
        launch();
    }
}