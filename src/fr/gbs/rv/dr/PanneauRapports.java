package fr.gbs.rv.dr;

import fr.gbs.rv.dr.entites.Praticien;
import fr.gbs.rv.dr.entites.RapportVisite;
import fr.gbs.rv.dr.technique.Mois;
import fr.gbs.rv.dr.entites.Visiteur;
import fr.gbs.rv.dr.modeles.ModeleGSB;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanneauRapports extends Pane {

//------------------Creation du comboBox--------------------------------------
    private ComboBox<Visiteur> comboBoxVisiteur = new ComboBox<Visiteur>();
    private ComboBox<Mois> comboBoxMois = new ComboBox<Mois>();
    private ComboBox<Integer> comboBoxAnnee = new ComboBox<>();
    private TableView<RapportVisite> tbrapv = new TableView<>();


    public PanneauRapports(){

        VBox root = new VBox();
        root.setSpacing(5);
        root.setPadding(new Insets(10,20, 10,10));

//-------------------recupérer les Visiteurs-----------------------------------
        comboBoxVisiteur.getItems().setAll(ModeleGSB.getVisiteurs());
        comboBoxVisiteur.setPromptText("Visiteur");

//-------------------recupérer les Mois----------------------------------------
        comboBoxMois.getItems().setAll(Mois.values());
        comboBoxMois.setPromptText("Mois");

//-------------------recupérer les Annes---------------------------------------
        int anneeCourante = LocalDate.now().getYear();
        System.out.println(anneeCourante);
        int anneeAv = anneeCourante - 20;

        while(anneeCourante >  anneeAv){
            comboBoxAnnee.getItems().add(anneeCourante);
            anneeCourante = anneeCourante - 1;
        }

        comboBoxAnnee.setPromptText("Année");

        Button btnValide = new Button("Validation");
        btnValide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rafraichir();
            }

        });

//-----------------Colonnes du tableau -----------------------------------------
        TableColumn<RapportVisite, Integer> colNumero = new TableColumn<RapportVisite ,Integer>("Numero");
        TableColumn<RapportVisite , String> colPraticien = new TableColumn<RapportVisite, String>("Praticien");
        TableColumn<RapportVisite ,String> colVille = new TableColumn<RapportVisite, String>("Vile");
        TableColumn<RapportVisite , LocalDate> colDateV = new TableColumn<RapportVisite, LocalDate>("Visite");
        TableColumn<RapportVisite ,LocalDate> colDateRed = new TableColumn<RapportVisite, LocalDate>("Rédaction");

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        colPraticien.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RapportVisite, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RapportVisite, String> param) {
                String nom = param.getValue().getLePraticien().getNom();
                return new SimpleStringProperty(nom);
            }
        });

        colVille.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RapportVisite, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RapportVisite, String> param) {
                String ville = param.getValue().getLePraticien().getVille();
                return  new SimpleStringProperty(ville);
            }
        });

        colDateV.setCellValueFactory(new PropertyValueFactory<>("dateVisite"));
        colDateV.setCellFactory(
                colonne ->{
                    return new TableCell<RapportVisite, LocalDate>(){
                        @Override
                        protected void updateItem(LocalDate item , boolean empty){
                            super.updateItem(item, empty);

                            if (empty){
                                setText("");
                            }
                            else {
                                DateTimeFormatter formateur = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                                setText(item.format(formateur));
                            }
                        }
                    };
                }
        );


        colDateRed.setCellValueFactory(new PropertyValueFactory<>("dateRedaction"));

        //Changement de couleur des lignes du tableau
        tbrapv.setRowFactory(
                ligne -> {
                    return new TableRow<RapportVisite>() {
                        @Override
                        protected void updateItem(RapportVisite item, boolean empty) {
                            super.updateItem(item, empty);

                            if (item != null) {
                                if (item.isLu()) {
                                    setStyle("-fx-background-color:  gold");
                                } else {
                                    setStyle("-fx-background-color: cyan");
                                }
                            }

                        }

                    };
                }
        );

        tbrapv.setOnMouseClicked(
                (MouseEvent event) -> {
                    if ( event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                        int indiceRapport = tbrapv.getSelectionModel().getSelectedIndex();
                        RapportVisite lu = tbrapv.getSelectionModel().getSelectedItem();
                        String matricule = tbrapv.getSelectionModel().getSelectedItem().getLeVisiteur().getMatricule();
                        int numero = tbrapv.getSelectionModel().getSelectedItem().getNumero();
                        ModeleGSB.setRapportVisiteLu( matricule, numero );
                        rafraichir();

                    }
                }
        );


        tbrapv.getColumns().addAll(colNumero,colPraticien,colVille ,colDateV,colDateRed);


        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(comboBoxVisiteur,0,0);
        form.add(comboBoxMois,1,0);
        form.add(comboBoxAnnee , 2,0);
        form.add(btnValide ,3, 0);



        root.getChildren().add(form);
        root.getChildren().add(tbrapv);

        this.getChildren().add(root);
        this.setStyle("-fx-background-color: white");
    }

    public void rafraichir(){
        Visiteur v = comboBoxVisiteur.getSelectionModel().getSelectedItem();
        String m = comboBoxMois.getSelectionModel().getSelectedItem().toString();
        Mois mois = Mois.valueOf(m);
        int numMois = mois.ordinal();
        int annee2 = comboBoxAnnee.getSelectionModel().getSelectedItem();

        //System.out.println(v + " | " + m + " | " + annee2);

        List<RapportVisite> lesPraticiens = ModeleGSB.getRapportsVisite(v.getMatricule() , numMois + 1 , annee2);
        ObservableList<RapportVisite> list = FXCollections.observableArrayList(lesPraticiens);
        tbrapv.setItems(list);


    }

}
