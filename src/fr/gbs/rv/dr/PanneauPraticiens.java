package fr.gbs.rv.dr;

import fr.gbs.rv.dr.entites.Praticien;
import fr.gbs.rv.dr.modeles.ModeleGSB;
import fr.gbs.rv.dr.utilitaires.ComparateurCoefConfiance;
import fr.gbs.rv.dr.utilitaires.ComparateurCoefNotoriete;
import fr.gbs.rv.dr.utilitaires.ComparateurDateVisite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;


public class PanneauPraticiens extends Pane {


  //  private Label titre = new Label("Praticiens");
    public final static int CRITERE_COEF_CONFIANCE = 1;
    public final static int CRITERE_COEF_NOTORIETE =2;
    public final static int CRITERE_DATE_VISITE = 3;
    private int critereTri = CRITERE_COEF_CONFIANCE;


    //creation des button radio
    RadioButton rdbCoef = new RadioButton("Confiance");
    RadioButton rdbNot = new RadioButton("Notoriété");
    RadioButton rdbDate = new RadioButton("Date Visite");
    private TableView<Praticien>tabPraticiens = new TableView<Praticien>();

    public PanneauPraticiens(){
       // this.getChildren().add(titre);
        //this.setStyle("-fx-background-color: white");


        VBox root = new VBox();

        root.setSpacing(8);
        root.setPadding(new Insets(10,10,10,10));

        GridPane dpBtnRadio = new GridPane();
        dpBtnRadio.setVgap(10);
        dpBtnRadio.setHgap(10);

       /* Image gsb = new Image(getClass().getResourceAsStream("media/gsb.png"));
        ImageView openView = new ImageView(gsb);
        //openView.autosize();
        openView.setStyle("-fx-background-image: media/gsb");
*/
        Label lbtitre = new Label("Sélectionner un critère de tri : ");
        lbtitre.setStyle("-fx-font-weight: bold");
        root.getChildren().add(lbtitre);

        ToggleGroup tgBtn = new ToggleGroup();
        rdbCoef.setToggleGroup(tgBtn);
        rdbNot.setToggleGroup(tgBtn);
        rdbDate.setToggleGroup(tgBtn);
        rdbCoef.setSelected(true);

        dpBtnRadio.add(rdbCoef,0,0);
        rdbCoef.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setCritereTri(CRITERE_COEF_CONFIANCE);
                rafraichir();
            }
        });
        dpBtnRadio.add(rdbNot,1,0);
        rdbNot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setCritereTri(CRITERE_COEF_NOTORIETE);
                rafraichir();
            }
        });
        dpBtnRadio.add(rdbDate,2,0);
        rdbDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setCritereTri(CRITERE_DATE_VISITE);
                rafraichir();
            }
        });


        //Creation des Column
        TableColumn<Praticien,Integer>colNumero = new TableColumn<Praticien, Integer>("Numéro");
        TableColumn<Praticien,String>colNom = new TableColumn<Praticien ,String>("Nom");
        TableColumn<Praticien, String>colVille = new TableColumn<Praticien, String>("Ville");

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));


        tabPraticiens.getColumns().addAll(colNumero,colNom,colVille);
        tabPraticiens.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        root.getChildren().addAll(dpBtnRadio,tabPraticiens);
        //Ajouter les btnradio a la vue
       // root.getChildren().add(dpBtnRadio);
        //Ajouter le VBox au Panneau
        this.getChildren().add(root);
        this.setStyle("-fx-background-color: white");


    }
    public void rafraichir(){
        if (critereTri == CRITERE_COEF_CONFIANCE) {
            List<Praticien> lesPraticiens = ModeleGSB.getPraticiensHesitants();
            ObservableList<Praticien> list = FXCollections.observableArrayList(lesPraticiens);
            list.sort(new ComparateurCoefConfiance());
            tabPraticiens.setItems(list);
        }
        else if (critereTri==CRITERE_COEF_NOTORIETE){
            List<Praticien> lesPraticiens = ModeleGSB.getPraticiensHesitants();
            ObservableList<Praticien> list = FXCollections.observableArrayList(lesPraticiens);
            list.sort(new ComparateurCoefNotoriete().reversed());
            tabPraticiens.setItems(list);
        }
        else {
            List<Praticien> lesPraticiens = ModeleGSB.getPraticiensHesitants();
            ObservableList<Praticien> list = FXCollections.observableArrayList(lesPraticiens);
            list.sort(new ComparateurDateVisite().reversed());
            tabPraticiens.setItems(list);
        }
    }

    public int getCritereTri() {
        return critereTri;
    }

    public void setCritereTri(int critereTri){
        this.critereTri = critereTri;
        if(critereTri == CRITERE_COEF_CONFIANCE){
            rdbCoef.setSelected(true);
        }
        else if(critereTri == CRITERE_COEF_NOTORIETE){
            rdbNot.setSelected(true);
        }
        else{
            rdbDate.setSelected(true);
        }
    }
}
