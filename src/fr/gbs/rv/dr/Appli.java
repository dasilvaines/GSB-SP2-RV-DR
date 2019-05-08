package fr.gbs.rv.dr;


import fr.gbs.rv.dr.entites.Praticien;
import fr.gbs.rv.dr.entites.RapportVisite;
import fr.gbs.rv.dr.entites.Visiteur;
import fr.gbs.rv.dr.modeles.ModeleGSB;
import fr.gbs.rv.dr.technique.Session;
import fr.gbs.rv.dr.utilitaires.ComparateurCoefConfiance;
import fr.gbs.rv.dr.utilitaires.ComparateurCoefNotoriete;
import fr.gbs.rv.dr.utilitaires.ComparateurDateVisite;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;



public class Appli extends Application {

    //declaration et initialisation des attributs  (panneaux)
    private PanneauAccueil vueAccueil = new PanneauAccueil();
    private PanneauPraticiens vuePraticiens = new PanneauPraticiens();
    private PanneauRapports vueRapports = new PanneauRapports();

    private StackPane stackPane;


    @Override
    public void start(Stage primaryStage) throws Exception{


        //Création de la scene
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: transparent");
        Scene scene = new Scene(root, 1000,800);


        //creation de l'affichage de la vue
        VBox root2 = new VBox();

        //Nouveau StackpPnne
        stackPane = new StackPane();

        //ADD la vue
        stackPane.getChildren().add(vuePraticiens);
        stackPane.getChildren().add(vueRapports);
        stackPane.getChildren().add(vueAccueil);

        //Add la pille de panneaux a l'affichage
        root2.getChildren().add(stackPane);

        //Creation de la barre de menu
        MenuBar barreMenu = new MenuBar();

        //Creation du menu
        Menu menuFichier = new Menu("Fichier");
        Menu menuRapports = new Menu("Rapports");
        menuRapports.setDisable(true);
        Menu menuPraticiens = new Menu("Praticiens");
        menuPraticiens.setDisable(true);

        //Creation de l'item dans le menu
        MenuItem itemSeConnecter = new MenuItem("Se connecter");
        MenuItem itemSeDeconnecter = new MenuItem("Se déconnecter");
        itemSeDeconnecter.setDisable(true);
        MenuItem itemQuitter = new MenuItem("Quitter");



        //Boite de dialogue de connexion
        Dialog authentification = new Dialog();
        authentification.setTitle("Authentification Délégué Regional");
        authentification.setHeaderText("Saisir vos données de connexion : ");

        //Champs Text
        TextField tfLogin = new TextField();
        PasswordField pfPassword = new PasswordField();

        //Label
        Label lbLogin = new Label("Matricule : ");
        Label lbPassword = new Label("Password : ")
                ;
        //Creation des buttons dans la boite de dialogue
        ButtonType btnConnection = new ButtonType("Connexion", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        authentification.getDialogPane().getButtonTypes().addAll(btnConnection, btnCancel);

        //Fenetre de connexion
        GridPane gridPaneDialogue = new GridPane();
        gridPaneDialogue.setHgap(10);
        gridPaneDialogue.setVgap(10);
        gridPaneDialogue.setPadding(new Insets(6));
        gridPaneDialogue.add(lbLogin,0 , 0);
        gridPaneDialogue.add(lbPassword, 0,1 );
        gridPaneDialogue.add(tfLogin, 1, 0);
        gridPaneDialogue.add(pfPassword, 1, 1);

        authentification.getDialogPane().setContent(gridPaneDialogue);

        final Node block = authentification.getDialogPane().lookupButton(btnConnection);
        block.setDisable(true);
        tfLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            block.setDisable(newValue.trim().isEmpty());
        });

        Alert alertBadAuthentification = new Alert(Alert.AlertType.ERROR);
        alertBadAuthentification.setTitle("Erreur !!");
        alertBadAuthentification.setHeaderText("Erreur de connexion...");
        alertBadAuthentification.setContentText("Votre matricule ou password est invalide.");
        ButtonType btnClose = new ButtonType("Annuler", ButtonBar.ButtonData.OK_DONE);

        alertBadAuthentification.getButtonTypes().setAll(btnClose);

        //Ecouteur d'evenement
        authentification.setResultConverter(btnDialogue ->{
            if (btnDialogue == btnConnection){
                return new Pair<>(tfLogin.getText(),pfPassword.getText());
            }
            return null;
        });



        //Creation de la boite de dialogue
        Alert alertQuitter = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuitter.setTitle("Quitter");
        alertQuitter.setHeaderText("Demande de confirmation");
        alertQuitter.setContentText("Voulez-vous quitter l'application ?");
        //creation des buttons
        ButtonType btnOui = new ButtonType("Oui" ,ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertQuitter.getButtonTypes().setAll(btnOui,btnNon);


        //Creation de boite de dialogue deconnexion
        Alert alertQuitterCon = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuitterCon.setTitle("Se deconnecter");
        alertQuitterCon.setHeaderText("Souhaitez-vous réellement vous déconnecter ?");
        ButtonType btnYes = new ButtonType("Oui" ,ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertQuitter.getButtonTypes().setAll(btnYes,btnNo);

        itemQuitter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<ButtonType> reponse = alertQuitter.showAndWait();
               if (reponse.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                   Platform.exit();
               }
               else{
                   alertQuitter.close();
               }
            }

        });

        itemSeConnecter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<Pair<String,String>> reponse = authentification.showAndWait();
                reponse.ifPresent(usernamePassword ->{
                    boolean res = ModeleGSB.seConnecter(reponse.get().getKey(),reponse.get().getValue());
                    if (res == true){
                        primaryStage.setTitle("GSB-RV-DR : " +
                                Session.getSession().getLeVisiteur().getNom().toUpperCase());
                        System.out.println(Session.getSession().getLeVisiteur().toString());
                        System.out.println(Session.getSession().getLeVisiteur().getNom());
                        System.out.println(Session.getSession().getLeVisiteur().getMatricule());
                        System.out.println(primaryStage.getTitle());
                        menuPraticiens.setDisable(false);
                        menuRapports.setDisable(false);
                        itemSeConnecter.setDisable(true);
                        tfLogin.setText(" ");
                        pfPassword.setText("");
                        itemSeDeconnecter.setDisable(false);
                        vuePraticiens.setCritereTri(PanneauPraticiens.CRITERE_COEF_CONFIANCE);
                    }

                else{

                    Optional<ButtonType> closeValid = alertBadAuthentification.showAndWait();

                    }
                });


                //Tester méthode getPraticiensHesitants
                List<Praticien> praticiens = ModeleGSB.getPraticiensHesitants();
                for(Praticien unPraticien: praticiens){
                    System.out.println(unPraticien.getNom());
                }


                // Tester Classe ComparateurCoefConfiance :sort(trier une liste d'elements)
                Collections.sort(praticiens , new ComparateurCoefConfiance());
                for (Praticien unPraticien:praticiens){
                    System.out.println(unPraticien.getDernierCoefConfiance());
                }

                //Tester Class ComparateurCoefNotoriete
                Collections.sort(praticiens, new ComparateurCoefNotoriete());
                for (Praticien unPraticien:praticiens){
                    System.out.println(unPraticien.getCoefNotoriete());
                }

                //Tester Class ComparateurDateVisite
                Collections.sort(praticiens, new ComparateurDateVisite());
                for (Praticien unPraticien:praticiens){
                    System.out.println(unPraticien.getDateDerniereVisite());
                }
            }
        });

        itemSeDeconnecter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //vueAccueil.toFront();
                vuePraticiens.setCritereTri(PanneauPraticiens.CRITERE_COEF_CONFIANCE);

                Optional<ButtonType> reponse = alertQuitterCon.showAndWait();
                if (reponse.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    primaryStage.setTitle("GSB-RV-DR ");
                    //Revenir a page d'accueil
                    vueAccueil.toFront();
                    Session.fermer();
                    menuPraticiens.setDisable(true);
                    menuRapports.setDisable(true);
                    itemSeConnecter.setDisable(false);
                    itemSeDeconnecter.setDisable(true);


                }
                else{
                    alertQuitterCon.close();
                }
            }
        });

        MenuItem itemConsulter = new MenuItem("Consulter");
        itemConsulter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               //Adicionner le panneau a l'item
                vueRapports.toFront();

                //Tester methode getVisiteurs() du ModeleGSB
                List<Visiteur> visiteurs = ModeleGSB.getVisiteurs();
                for (Visiteur unVisiteur:visiteurs){
                    System.out.println(unVisiteur.getMatricule());

                }

                List<RapportVisite> rapportVisites = ModeleGSB.getRapportsVisite("a17" ,03 , 2019);
                for (RapportVisite unRapVisite:rapportVisites){
                    System.out.println(unRapVisite.getLeVisiteur().getNom());
                }

                ModeleGSB.setRapportVisiteLu("a17",6);

            }
        });

        MenuItem itemHesitants = new MenuItem("Hésitants");
        //image
        Image openRap = new Image(getClass().getResourceAsStream("media/open.png"));
        ImageView openView = new ImageView(openRap);
        openView.setFitWidth(15);
        openView.setFitHeight(15);
        itemHesitants.setGraphic(openView);
        itemHesitants.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vuePraticiens.toFront();
                vuePraticiens.rafraichir();

            }
        });



        //creation d'un separator
        SeparatorMenuItem sepMenu = new SeparatorMenuItem();
        itemQuitter.setAccelerator(new KeyCodeCombination(KeyCode.X ,KeyCombination.CONTROL_DOWN));



        //Ajout des item dans le menu
        menuFichier.getItems().addAll(itemSeConnecter,itemSeDeconnecter,sepMenu,itemQuitter);
        menuRapports.getItems().add(itemConsulter);
        menuPraticiens.getItems().add(itemHesitants);

        //Ajout des menus dans la barre menus
        barreMenu.getMenus().addAll(menuFichier,menuRapports,menuPraticiens);
        root2.setFillWidth(true);
        root.setTop(barreMenu);
        root.setCenter(root2);

        primaryStage.setResizable(false);
        //modifier le titre de la fenetre
        primaryStage.setTitle("GSB-RV-DR");
        //Associer la scene a la fenetre
        primaryStage.setScene(scene);
        //afficher la fenetre
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
