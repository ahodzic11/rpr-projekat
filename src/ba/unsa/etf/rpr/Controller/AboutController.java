package ba.unsa.etf.rpr.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AboutController {
    public Label labHeadline;

    public void openProjectLink(ActionEvent actionEvent) {
        if(Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ahodzic11/rpr-projekat"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void openAuthorsGit(ActionEvent actionEvent) {
        if(Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ahodzic11"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void okBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) labHeadline.getScene().getWindow();
        stage.close();
    }
}
