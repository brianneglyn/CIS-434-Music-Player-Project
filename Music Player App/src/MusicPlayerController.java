package musicplayerapp;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MusicPlayerController {

    @FXML
    private Button btnPlay;

    @FXML
    private ListView<String> lvPlayList;

    @FXML
    private Button btnPause;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnSkip;

    @FXML
    private Button btnPrevious;

    @FXML
    private Slider sliderVolume;

    @FXML
    private Label lblSongInfo;

    @FXML
    private Button btnRenamePlaylist;

    @FXML
    private Button btnSavePlaylistName;

    @FXML
    private ListView<String> lvUserPlaylist;

    @FXML
    private Button btnRemoveFromPlaylist;

    @FXML
    private Button btnAddToPlaylist;

    @FXML
    private TextField txtPlaylistName;

    @FXML
    private ComboBox<String> cbPlaybackMode;
    
    @FXML
    private Label lblLibrary;

    @FXML
    private Label lblPlaylist;
    
    
    @FXML
    private Label lblMusicPlayer;
    
    @FXML
    private Label lblTimeElapsed;

    int count = 0;

    MediaPlayer musicPlayer = null;

    private int currentSongIndex = 0;

    private String userPlaylistName = "My Playlist"; // Default playlist name

    private String playbackMode = "Order"; // Default mode

    private List<Integer> shuffledIndexes = new ArrayList<>();

    private int shuffleIndex = 0;

    // Add paths to your music files here
    private final String[] songs = {
        "c:\\music.mp3",
        "c:\\Users\\brike\\Downloads\\music2.mp3",
        "c:\\Users\\brike\\Downloads\\music3.mp3",
        "c:\\Users\\brike\\Downloads\\music4.mp3"
    };

    private final String[] songNames = {
        " Fragments of Time ",
        " Awakening Now ",
        " Bygone Era ",
        " Meadow Waltz "
    };

    private final String[] artists = {
        " Daft Punk ",
        " Keys of Moon ",
        " Peri Tune ",
        " Keys of Moon "

    };

    private String formatDuration(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @FXML
    public void initialize() {
        // Populate playback modes
        cbPlaybackMode.getItems().addAll("Order", "Shuffle", "Loop");
        cbPlaybackMode.setValue("Order"); // Default playback mode

        cbPlaybackMode.valueProperty().addListener((observable, oldValue, newValue) -> {
            playbackMode = newValue;
            if (playbackMode.equals("Shuffle")) {
                initializeShuffle();
            }
        });
        // Populate playlist
        for (int i = 0; i < songs.length; i++) {
            lvPlayList.getItems().add(songNames[i]); // Add song names to the ListView
        }
        lvPlayList.getSelectionModel().select(0); // Default selection (first song)

        // Initialize volume slider
        sliderVolume.setMin(0);
        sliderVolume.setMax(1);
        sliderVolume.setValue(0.5); // Default volume

        sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (musicPlayer != null) {
                    musicPlayer.setVolume(newValue.doubleValue());
                }
            }

        });
        
    }
    
    private void initializeShuffle() {
    shuffledIndexes.clear();
    for (int i = 0; i < songs.length; i++) {
        shuffledIndexes.add(i);
    }
    Collections.shuffle(shuffledIndexes);
    shuffleIndex = 0;
}





  private void handleEndOfSong() {
    if (playbackMode.equals("Loop")) {
        playSong(currentSongIndex); // Replay the current song
    } else {
        skipClicked(null); // Move to the next song (Order or Shuffle)
    }
}
   private void playSong(int index) {
    if (index < 0 || index >= songs.length) {
        lblSongInfo.setText("Invalid song index selected.");
        return;
    }
    if (musicPlayer != null) {
        musicPlayer.stop();
        musicPlayer.dispose();
    }
    try {
        Media media = new Media(new File(songs[index]).toURI().toString());
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setVolume(sliderVolume.getValue());
        musicPlayer.play();
        lvPlayList.getSelectionModel().select(index);

        musicPlayer.setOnEndOfMedia(() -> handleEndOfSong()); // Handle end of song

        musicPlayer.setOnReady(() -> {
            double durationInSeconds = media.getDuration().toSeconds();
            String formattedDuration = formatDuration(durationInSeconds);
            lblSongInfo.setText("Now Playing: " + songNames[index] + " by " + artists[index] + " [" + formattedDuration + "]");
        });

        currentSongIndex = index;

        musicPlayer.setOnError(() -> {
            lblSongInfo.setText("Error during playback: " + musicPlayer.getError().getMessage());
        });

        // Update elapsed time
        musicPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> {
            String formattedElapsedTime = formatDuration(newTime.toSeconds());
            lblTimeElapsed.setText(formattedElapsedTime);
        });

    } catch (Exception e) {
        lblSongInfo.setText("Unexpected error playing song.");
        e.printStackTrace();
    }
}
   
   
   
   @FXML
void playClicked(ActionEvent event) {
    if (musicPlayer != null) {
        MediaPlayer.Status status = musicPlayer.getStatus();
        if (status == MediaPlayer.Status.PAUSED) {
            // Resume playback from the paused position
            musicPlayer.play();
        } else {
            // Start playback from the beginning
            playSong(currentSongIndex);
        }
    } else {
        // If no song is currently loaded, start playing the current song
        playSong(currentSongIndex);
    }
}

    @FXML
    void pauseClicked(ActionEvent event) {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }

    @FXML
    void stopClicked(ActionEvent event) {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
            lblSongInfo.setText("Now Playing: None");
        }
    }

   @FXML
void skipClicked(ActionEvent event) {
    if (playbackMode.equals("Order")) {
        currentSongIndex = (currentSongIndex + 1) % songs.length;
    } else if (playbackMode.equals("Shuffle")) {
        if (shuffleIndex >= shuffledIndexes.size()) {
            initializeShuffle(); // Reinitialize shuffle if all songs are played
        }
        currentSongIndex = shuffledIndexes.get(shuffleIndex++);
    } else if (playbackMode.equals("Loop")) {
        // In Loop mode, replay the current song
    }
    playSong(currentSongIndex);
}

   
    @FXML
void previousClicked(ActionEvent event) {
    if (playbackMode.equals("Order")) {
        currentSongIndex = (currentSongIndex - 1 + songs.length) % songs.length;
    } else if (playbackMode.equals("Shuffle")) {
        if (shuffleIndex > 0) {
            shuffleIndex--;
        }
        currentSongIndex = shuffledIndexes.get(shuffleIndex);
    } else if (playbackMode.equals("Loop")) {
        // In Loop mode, replay the current song
    }
    playSong(currentSongIndex);
}

@FXML
void playSelectedSong() {
    int selectedIndex = lvPlayList.getSelectionModel().getSelectedIndex();
    if (selectedIndex != -1) { // Ensure a valid selection
        currentSongIndex = selectedIndex; // Update current index
        playSong(currentSongIndex); // Play the selected song
    }
}

// Add a selected song from the library to the user playlist
    @FXML
    void addToPlaylist(ActionEvent event) {
        int selectedIndex = lvPlayList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedSong = songNames[selectedIndex];
            if (!lvUserPlaylist.getItems().contains(selectedSong)) {
                lvUserPlaylist.getItems().add(selectedSong);
            } else {
                lblSongInfo.setText("Song is already in the playlist.");
            }
        }
    }

// Remove a selected song from the user playlist
    @FXML
    void removeFromPlaylist(ActionEvent event) {
        int selectedIndex = lvUserPlaylist.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            lvUserPlaylist.getItems().remove(selectedIndex);
        }
    }

// Play a selected song from the user playlist
    @FXML
    void playFromUserPlaylist() {
        int selectedIndex = lvUserPlaylist.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedSong = lvUserPlaylist.getItems().get(selectedIndex);
            int songIndex = -1;
            for (int i = 0; i < songNames.length; i++) {
                if (songNames[i].equals(selectedSong)) {
                    songIndex = i;
                    break;
                }
            }
            if (songIndex != -1) {
                currentSongIndex = songIndex;
                playSong(currentSongIndex);
            }
        }
    }

// Save the playlist name
    @FXML
    void savePlaylistName(ActionEvent event) {
        String newName = txtPlaylistName.getText().trim();
        if (!newName.isEmpty()) {
            userPlaylistName = newName;
            lblSongInfo.setText("Playlist saved as: " + userPlaylistName);
        } else {
            lblSongInfo.setText("Enter a valid playlist name.");
        }
    }

// Rename the playlist
    @FXML
    void renamePlaylist(ActionEvent event) {
        String newName = txtPlaylistName.getText().trim();
        if (!newName.isEmpty()) {
            userPlaylistName = newName;
            lblSongInfo.setText("Playlist renamed to: " + userPlaylistName);
        } else {
            lblSongInfo.setText("Enter a valid playlist name.");
        }
    }
}
