package musicplayer;

import com.opencsv.CSVWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * <p>
 * Music Player - Project
 * </p>
 * <p>
 * JMC requires an advanced music player that allows the ability to sort and
 * search the songs stored in a binary tree, the GUI should display the sorted
 * track list and highlight and play the searched track, it should save the
 * track list to a CSV using a 3rd party library
 * </p>
 *
 * @author 30021175 - Willian Bernatzki Woellner
 * @since 2020-11-08
 * @version 1.0.0
 */
public class MusicPlayer extends Application {

    /**
     * Global Variables
     */
    private BinaryTree binaryTree = new BinaryTree();
    private ListView<String> lstSongs = new ListView<>();
    private LinkedList<Song> songs = null; //Sorted Songs
    private Song currentSong = null;
    private int currentPosition = 0;
    private MediaPlayer mediaPlayer;
    private Slider slider;

    /**
     * Main Method
     *
     * @param args String
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start Method
     *
     * @param stage Stage
     */
    public void start(Stage stage) {

        //Create Stage
        stage.setTitle("Music Player");
        //Create GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        //Create FileChooser to add the songs
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:\\Developing\\Java 3\\MusicPlayer"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));

        //Create AddSongs Button
        Button btnAddSongs = new Button("Add Songs");
        btnAddSongs.setPrefWidth(100);
        btnAddSongs.setOnAction(e -> {
            List<File> lstFiles = fileChooser.showOpenMultipleDialog(stage);

            if (lstFiles != null) {
                for (int i = 0; i < lstFiles.size(); i++) {

                    //Create the Binary Tree
                    binaryTree.add(new Song(lstFiles.get(i).getName().substring(0,
                            lstFiles.get(i).getName().lastIndexOf(".")), //Remove File extension
                            lstFiles.get(i).toURI().toString()));
                }

                displaySongs();

                //Set Variables to Control the TrackList
                currentSong = songs.getFirst();
                currentPosition = 0;
                lstSongs.getSelectionModel().select(currentPosition);
                loadSong();
            }
        });

        VBox vboxAddSong = new VBox();
        vboxAddSong.setAlignment(Pos.TOP_CENTER);
        vboxAddSong.getChildren().add(btnAddSongs);
        grid.add(vboxAddSong, 0, 0, 4, 1);

        //Help Button
        Button btnHelp = new Button("?");
        btnHelp.setPrefWidth(10);
        btnHelp.setOnAction(e -> {
            String url = "help.html";
            File helpFile = new File(url); // file object of the help file 
            try {
                Desktop.getDesktop().browse(helpFile.toURI());
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error to open the help file: " + ex.getMessage());                
            }
        });
        grid.add(btnHelp, 0, 0);
        
        //Create GridPane to Search Section
        GridPane gridSearch = new GridPane();
        gridSearch.setHgap(10);
        gridSearch.setVgap(10);
        gridSearch.setPadding(new Insets(10, 10, 10, 10));

        TextField txtSearch = new TextField();
        txtSearch.setPrefWidth(500);
        gridSearch.add(txtSearch, 0, 0, 3, 1);

        //Search Button
        Button btnSearch = new Button("Search");
        btnSearch.setPrefWidth(100);
        btnSearch.setOnAction(e -> {
            if (songs != null) {
                if (!txtSearch.getText().isEmpty()) {
                    currentSong = binaryTree.search(txtSearch.getText());

                    if (currentSong != null) {
                        mediaPlayer.stop();
                        lstSongs.getSelectionModel().select(currentSong.getTitle());
                        currentPosition = songs.indexOf(currentSong);
                        txtSearch.setText("");
                        loadSong();
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, "Song not found.");
                    }
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Search field is required.");
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No songs added.");
            }
        });

        gridSearch.add(btnSearch, 3, 0);
        grid.add(gridSearch, 0, 1, 4, 1);

        //Save Button
        Button btnSave = new Button("Save");
        btnSave.setPrefWidth(100);
        btnSave.setOnAction(e -> {
            if (songs != null) {
                saveCSVFile();
                showAlert(Alert.AlertType.INFORMATION, "The Tracklist has been saved on CSV file.");
            } else {
                showAlert(Alert.AlertType.WARNING, "There are no songs to save.");
            }
        });

        VBox vboxSave = new VBox();
        vboxSave.setAlignment(Pos.TOP_CENTER);
        vboxSave.getChildren().add(btnSave);
        grid.add(vboxSave, 0, 2, 4, 1);

        //Settings to ListView Songs
        lstSongs.setPrefWidth(550);
        lstSongs.setPrefHeight(150);
        lstSongs.setMouseTransparent(true); //To Disabled the selecion 
        grid.add(lstSongs, 0, 3, 4, 1);

        //Play Button
        Button btnPlay = new Button("Play");
        btnPlay.setOnAction(e -> {
            if (mediaPlayer != null) {
                //Verify if the Current Song finished to reload the song
                if (slider.getValue() == 100) {
                    mediaPlayer.seek(Duration.ZERO);
                }
                mediaPlayer.play();
            }
        });
        btnPlay.setPrefWidth(60);
        grid.add(btnPlay, 0, 4);

        //Pause Button
        Button btnPause = new Button("Pause");
        btnPause.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });
        btnPause.setPrefWidth(60);
        grid.add(btnPause, 1, 4);

        //Stop Button
        Button btnStop = new Button("Stop");
        btnStop.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
        btnStop.setPrefWidth(60);
        grid.add(btnStop, 2, 4);

        //Create Slider to control the song time
        slider = new Slider();
        slider.setPrefWidth(300);
        // Inorder to jump to the certain part of song 
        slider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (mediaPlayer != null) {
                    if (slider.isPressed()) {
                        //As specified by user by pressing 
                        mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(slider.getValue() / 100));
                    }
                }
            }
        });

        grid.add(slider, 3, 4);

        //Create GridPane to Commands
        GridPane gridCommands = new GridPane();
        gridCommands.setAlignment(Pos.TOP_CENTER);
        gridCommands.setHgap(10);
        gridCommands.setVgap(10);
        gridCommands.setPadding(new Insets(10, 10, 10, 10));

        //First Button
        Button btnFirst = new Button("First");
        btnFirst.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                currentSong = songs.getFirst();
                currentPosition = 0;
                lstSongs.getSelectionModel().select(currentPosition);
                loadSong();
            }
        });
        btnFirst.setPrefWidth(100);
        gridCommands.add(btnFirst, 0, 0);

        //Previous Button
        Button btnPrevious = new Button("Previous");
        btnPrevious.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                if (currentPosition > 0) {
                    currentPosition--;
                    currentSong = songs.get(currentPosition);
                    lstSongs.getSelectionModel().select(currentPosition);
                    loadSong();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "There is no previous song to play.");
                }
            }
        });
        btnPrevious.setPrefWidth(100);
        gridCommands.add(btnPrevious, 1, 0);

        //Next Button
        Button btnNext = new Button("Next");
        btnNext.setOnAction(e -> {
            if (mediaPlayer != null) {
                
                if (currentPosition < songs.size() - 1) {
                    mediaPlayer.stop();
                    currentPosition++;
                    currentSong = songs.get(currentPosition);
                    lstSongs.getSelectionModel().select(currentPosition);
                    loadSong();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "There is no next song to play.");
                }
            }
        });
        btnNext.setPrefWidth(100);
        gridCommands.add(btnNext, 2, 0);

        //Last Button
        Button btnLast = new Button("Last");
        btnLast.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                currentSong = songs.getLast();
                currentPosition = songs.size() - 1;
                lstSongs.getSelectionModel().select(currentPosition);
                loadSong();
            }
        });
        btnLast.setPrefWidth(100);
        gridCommands.add(btnLast, 3, 0);

        grid.add(gridCommands, 0, 5, 4, 1);

        stage.setScene(new Scene(grid, 600, 400));
        stage.show();
    }

    /**
     * displaySongs Method - It is used to display all songs from the Binary
     * Tree on the ListView
     */
    private void displaySongs() {
        //Called Display Inorder Sort Songs
        songs = binaryTree.display();

        for (int i = 0; i < songs.size(); i++) {
            lstSongs.getItems().add(songs.get(i).getTitle());
        }

    }

    /**
     * loadSong Method - It is used to load a Song
     */
    private void loadSong() {

        Media media = new Media(currentSong.getURL());

        //Instantiating MediaPlayer class   
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                updateSlider();
            }
        });
        mediaPlayer.play();
    }

    /**
     * updateSlider Method - It is used to run the slider bar when the song is
     * playing
     */
    protected void updateSlider() {
        Platform.runLater(new Runnable() {
            public void run() {
                slider.setValue(mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
            }
        });
    }

    /**
     * saveCSVFile Method - It is used to save the tracklist on a CSV file using
     * the OpenCSV library
     */
    private void saveCSVFile() {
        try {
            //Create FileWriter object filewriter object as parameter 
            FileWriter fileWriter = new FileWriter("trackList.csv", false);
            // create CSVWriter object filewriter object as parameter 
            CSVWriter writer = new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

            //Header
            String[] header = {"Title", "URL", "MD5 URL"};
            writer.writeNext(header);

            //Rows
            for (int i = 0; i < songs.size(); i++) {
                String[] rows = {songs.get(i).getTitle(), songs.get(i).getURL(), getMd5(songs.get(i).getURL())};
                writer.writeNext(rows);
            }

            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error to save the file: " + e.getMessage());
        }
    }

    /**
     * getMd5 Method - It is used to encrypt the URL file using MD5 algorithm
     *
     * @param URL String
     * @return String
     */
    private static String getMd5(String URL) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(URL.getBytes(), 0, URL.length());

            return new BigInteger(1, m.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * showAlert Method - It is used to show the alerts
     *
     * @param alertType AlertType
     * @param message String
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Music Player");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
