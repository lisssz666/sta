package com.ruoyi.project.game.controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MapControlExample extends Application {

    private Pane root;

    @Override
    public void start(Stage primaryStage) {
        try {
            // 创建一个 JavaFX 窗口
            primaryStage.setTitle("Map Control Example");

            // 创建一个 JavaFX Pane 用于放置地图控件
            Pane root = new Pane();

            // 创建一个 WebView 来加载地图
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            // 在 WebView 中加载地图的 URL
            webEngine.load("https://www.baidu.com/");

            // 将 WebView 添加到 Pane 中
            root.getChildren().add(webView);

            // 创建一个 Scene 并将 Pane 放入其中
            Scene scene = new Scene(root, 800, 600);

            // 将 Scene 设置到 Stage
            primaryStage.setScene(scene);

            // 显示 JavaFX 窗口
            primaryStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


//mvn install:install-file -Dfile=/Users/lisenshuai/fsdownload/mydemo-0.0.1-SNAPSHOT.jar -DgroupId=com.mapgis.objects -DartifactId=geomap -Dversion=10.5.6.10 -Dpackaging=jar
