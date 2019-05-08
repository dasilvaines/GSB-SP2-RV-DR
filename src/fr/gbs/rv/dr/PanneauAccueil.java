package fr.gbs.rv.dr;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class PanneauAccueil extends Pane {
    //private Label titre = new Label("Bienvenue" );

    private Image gsb = new Image(getClass().getResourceAsStream("media/gsb.png"));
    private ImageView openView = new ImageView(gsb);



    public PanneauAccueil(){

        openView.autosize();


        this.getChildren().add(openView);
        this.setStyle("-fx-background-color: white");
    }
}

