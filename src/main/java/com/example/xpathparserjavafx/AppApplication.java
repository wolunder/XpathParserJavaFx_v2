package com.example.xpathparserjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AppApplication.class.getResource("hello-view3_tab_progress.fxml"));
        //773 155
        //788 203
        Scene scene = new Scene(fxmlLoader.load(), 788,203);
        stage.setTitle("XP Converter");
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }
}


