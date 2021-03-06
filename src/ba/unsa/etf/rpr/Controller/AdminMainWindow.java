package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.DAL.*;
import ba.unsa.etf.rpr.Model.ActionLog;
import ba.unsa.etf.rpr.Model.Inspector;
import ba.unsa.etf.rpr.Utility.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class AdminMainWindow {
    public ListView inspectorList;
    public Label labInfo;
    public Label labUniqueID;
    public Inspector currentInspector;
    public int currentInspectorID = -1;
    public Button profileBtn;
    public Button reportsBtn;
    public Button modifyBtn;
    public Button deleteBtn;
    public Button assignTaskBtn;
    public Button showDutiesBtn;
    public Label labStatusBar;
    public Label labInspectorType;
    public Label labEmail;
    public Label labPhoneNumber;
    public Button exportBtn;
    private InspectorDAO inspectorDAO;
    private AdministratorDAO administratorDAO;
    private UserDAO userDAO;
    private LoginLogDAO loginLogDAO;
    private ActionLogDAO actionLogDAO;
    private Status status;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() throws SQLException {
        inspectorDAO = InspectorDAO.getInstance();
        administratorDAO = AdministratorDAO.getInstance();
        userDAO = UserDAO.getInstance();
        actionLogDAO = ActionLogDAO.getInstance();
        loginLogDAO = LoginLogDAO.getInstance();
        status = Status.getInstance();

        disableButtons();

        inspectorList.setItems(inspectorDAO.allInspectors());
        inspectorList.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem)->{
            Inspector newInspector = (Inspector) newItem;
            if(newInspector!=null){
                currentInspectorID = newInspector.getId();
                updateShownProfileInfo();
                enableButtons();
            }else{
                disableButtons();
            }
        });
    }

    private void updateShownProfileInfo() {
        labInfo.setText(inspectorDAO.getFirstNameForID(currentInspectorID) + " " + inspectorDAO.getSurenameForID(currentInspectorID));
        labUniqueID.setText("UNIQUE ID: " + inspectorDAO.getUniqueIDForID(currentInspectorID));
        labInspectorType.setText(inspectorDAO.getInspectorTypeForID(currentInspectorID));
        labEmail.setText(inspectorDAO.getLoginEmailForID(currentInspectorID));
        labPhoneNumber.setText(inspectorDAO.getPhoneNumberForID(currentInspectorID));
    }

    public void openCreateAccount(ActionEvent actionEvent) throws IOException {
        Stage myStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/createAccount.fxml"));
        myStage.setTitle("Create an account");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setResizable(false);
        myStage.showAndWait();
        refreshInspectorsList();
        updateStatus();
        inspectorList.getSelectionModel().select(inspectorList.getItems().size()-1);
    }

    public void profileBtn(ActionEvent actionEvent) throws IOException, SQLException {
        DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        status.setStatus("Inspector profile - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + " [" + inspectorDAO.getUniqueIDForID(currentInspectorID) + "] opened. (" + LocalDateTime.now().format(logFormatter) + ")");
        String action = "Administrator[" + userDAO.getLoggedUserUniqueID()+"] opened account - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + "[" + inspectorDAO.getUniqueIDForID(currentInspectorID)+"]";
        actionLogDAO.addLog(new ActionLog(1, LocalDateTime.now().format(formatter), action, userDAO.getLoggedUserUniqueID()));
        updateStatus();

        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inspectorProfile.fxml"));
        Parent root = loader.load();

        ProfileController profileController = loader.getController();
        profileController.labUniqueID.setText(inspectorDAO.getUniqueIDForID(currentInspectorID));
        profileController.labFirstName.setText(inspectorDAO.getFirstNameForID(currentInspectorID));
        profileController.labLastName.setText(inspectorDAO.getSurenameForID(currentInspectorID));
        profileController.labBirthdate.setText(inspectorDAO.getBirthdateForID(currentInspectorID));
        profileController.labJMBG.setText(inspectorDAO.getJMBGForID(currentInspectorID));
        if(inspectorDAO.getGenderForID(currentInspectorID)==1) profileController.labGender.setText("M");
        else profileController.labGender.setText("F");
        profileController.labIDNumber.setText(inspectorDAO.getIDNumberForID(currentInspectorID));
        profileController.labResidence.setText(inspectorDAO.getResidenceForID(currentInspectorID));
        profileController.labPhoneNumber.setText(inspectorDAO.getPhoneNumberForID(currentInspectorID));
        profileController.labEmail.setText(inspectorDAO.getEmailForID(currentInspectorID));
        profileController.labLoginEmail.setText(inspectorDAO.getLoginEmailForID(currentInspectorID));
        profileController.labPassword.setText(inspectorDAO.getPasswordForID(currentInspectorID));
        profileController.labInspectionArea.setText(inspectorDAO.getInspectionAreaForID(currentInspectorID));
        profileController.labInspectorType.setText(inspectorDAO.getInspectorTypeForID(currentInspectorID));
        if(inspectorDAO.getDriversLicenseForID(currentInspectorID)==1) profileController.labDriversLicense.setText("owns a license");
        else profileController.labDriversLicense.setText("doesn't own a license");
        myStage.setTitle("Inspector profile");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setResizable(false);
        myStage.showAndWait();

        updateStatus();
    }

    public void deleteBtn(ActionEvent actionEvent) throws SQLException {
        status.setStatus("Inspector profile - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + " [" + inspectorDAO.getUniqueIDForID(currentInspectorID) + "] deleted. (" + LocalDateTime.now().format(logFormatter) + ")");
        String action = "Administrator[" + userDAO.getLoggedUserUniqueID()+"] deleted account - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + "[" + inspectorDAO.getUniqueIDForID(currentInspectorID)+"]";
        actionLogDAO.addLog(new ActionLog(1, LocalDateTime.now().format(formatter), action, userDAO.getLoggedUserUniqueID()));

        inspectorDAO.deleteInspector(currentInspectorID);
        refreshInspectorsList();
        currentInspectorID = -1;
        labInfo.setText("");
        labUniqueID.setText("Choose an inspector");
        labInspectorType.setText("");
        labPhoneNumber.setText("");
        labEmail.setText("");
        updateStatus();
    }

    public void modifyBtn(ActionEvent actionEvent) throws IOException {
        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modifyProfile.fxml"));
        Parent root = loader.load();

        ModifyProfileController mod = loader.getController();
        mod.inspectorId = currentInspectorID;
        mod.fldName.setText(inspectorDAO.getFirstNameForID(currentInspectorID));
        mod.fldSurename.setText(inspectorDAO.getSurenameForID(currentInspectorID));
        mod.fldIDNumber.setText(inspectorDAO.getIDNumberForID(currentInspectorID));
        mod.fldResidency.setText(inspectorDAO.getResidenceForID(currentInspectorID));
        mod.fldPhoneNumber.setText(inspectorDAO.getPhoneNumberForID(currentInspectorID));
        mod.fldEmail.setText(inspectorDAO.getEmailForID(currentInspectorID));
        mod.fldLoginEmail.setText(inspectorDAO.getLoginEmailForID(currentInspectorID));
        mod.fldPassword.setText(inspectorDAO.getPasswordForID(currentInspectorID));
        mod.comboInspectionArea.setValue(inspectorDAO.getInspectionAreaForID(currentInspectorID));
        if(inspectorDAO.isMajorInspector(currentInspectorID)) mod.rbMajorInspector.setSelected(true);
        else mod.rbFederalInspector.setSelected(true);
        if(inspectorDAO.getDriversLicenseForID(currentInspectorID)==1) mod.cbDriversLicense.setSelected(true);
        else mod.cbDriversLicense.setSelected(false);

        myStage.setTitle("Modify the inspector");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setResizable(false);
        myStage.showAndWait();
        refreshInspectorsList();

        inspectorList.getSelectionModel().select(currentInspectorID);
        updateStatus();
        updateShownProfileInfo();
    }

    public void refreshInspectorsList(){
        inspectorList.setItems(inspectorDAO.allInspectors());
    }

    public void logoutBtn(ActionEvent actionEvent) throws IOException {
        loginLogDAO.logout(userDAO.getLoggedUserUniqueID());
        userDAO.deleteLoggedUser();
        Stage myStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setResizable(false);
        myStage.show();
        Stage stage = (Stage) labUniqueID.getScene().getWindow();
        stage.close();
    }

    public void reportsBtn(ActionEvent actionEvent) throws IOException, SQLException {
        status.setStatus("Reports for inspector profile - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + " [" + inspectorDAO.getUniqueIDForID(currentInspectorID) + "] opened. (" + LocalDateTime.now().format(logFormatter) + ")");
        String action = "Administrator[" + userDAO.getLoggedUserUniqueID()+"] open reports for inspector - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + "[" + inspectorDAO.getUniqueIDForID(currentInspectorID)+"]";
        actionLogDAO.addLog(new ActionLog(1, LocalDateTime.now().format(formatter), action, userDAO.getLoggedUserUniqueID()));
        updateStatus();
        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
        Parent root = loader.load();
        ReportsController reportsController = loader.getController();
        reportsController.setInspectorId(currentInspectorID);
        reportsController.refreshReports();
        myStage.setTitle("Reports");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setResizable(false);
        myStage.showAndWait();
        updateStatus();
    }

    public void exitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) inspectorList.getScene().getWindow();
        stage.close();
    }

    public void changeAdminLoginData(ActionEvent actionEvent) throws IOException {
        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modifyAdministrator.fxml"));
        Parent root = loader.load();

        ModifyAdministratorController cont = loader.getController();
        String adminUniqueID = userDAO.getLoggedUserUniqueID();
        cont.fldEmail.setText(administratorDAO.getEmailForUniqueID(adminUniqueID));
        cont.fldPassword.setText(administratorDAO.getPasswordForUniqueID(adminUniqueID));
        cont.fldUniqueID.setText(adminUniqueID);

        myStage.setTitle("Modify the administrator");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.showAndWait();
        updateStatus();
    }

    public void createAdminAccountBtn(ActionEvent actionEvent) throws IOException {
        Stage myStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/createAdmin.fxml"));
        myStage.setTitle("Create an admin account");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setMinWidth(210);
        myStage.setMinHeight(260);
        myStage.setMaxWidth(300);
        myStage.setMaxHeight(260);
        myStage.showAndWait();
        updateStatus();
    }

    public void logsBtn(ActionEvent actionEvent) throws IOException, SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        status.setStatus("Logs opened. (" + LocalDateTime.now().format(formatter) + ")");
        updateStatus();
        actionLogDAO.addLog(new ActionLog(1, LocalDateTime.now().format(formatter), "Administrator [" + userDAO.getLoggedUserUniqueID()+ "] opened logs", userDAO.getLoggedUserUniqueID()));
        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logs.fxml"));
        Parent root = loader.load();
        LogsController cont = loader.getController();
        myStage.setTitle("Logs");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setMinWidth(498);
        myStage.setMinHeight(608);
        myStage.showAndWait();
        updateStatus();
    }

    public void assignTaskBtn(ActionEvent actionEvent) throws IOException {
        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/assignTask.fxml"));
        Parent root = loader.load();
        AssignTaskController cont = loader.getController();
        cont.setInspectorID(currentInspectorID);
        myStage.setTitle("Assign a task");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setMinWidth(260);
        myStage.setMinHeight(202);
        myStage.setMaxWidth(400);
        myStage.showAndWait();
        updateStatus();
    }

    public void updateStatus(){
        labStatusBar.setText(status.getStatus());
    }

    public void showDutiesBtn(ActionEvent actionEvent) throws IOException, SQLException {
        status.setStatus("Duties assigned to inspector " + inspectorDAO.getNameSurenameForID(currentInspectorID) + " [" + inspectorDAO.getUniqueIDForID(currentInspectorID) + "] have been shown. (" + LocalDateTime.now().format(logFormatter) + ")");
        actionLogDAO.addLog(new ActionLog(1, LocalDateTime.now().format(formatter), "Administrator [" + userDAO.getLoggedUserUniqueID()+ "] opened duties of inspector - " + inspectorDAO.getNameSurenameForID(currentInspectorID) + " [" + inspectorDAO.getUniqueIDForID(currentInspectorID) + "]", userDAO.getLoggedUserUniqueID()));
        Stage myStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/showDuties.fxml"));
        Parent root = loader.load();
        ShowDutiesController cont = loader.getController();
        cont.inspectorId = currentInspectorID;
        myStage.setTitle("Duties assigned");
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.setMinWidth(210);
        myStage.setMinHeight(187);
        myStage.setMaxWidth(210);
        updateStatus();
        myStage.showAndWait();
        updateStatus();
    }

    private void disableButtons() {
        profileBtn.setDisable(true);
        modifyBtn.setDisable(true);
        reportsBtn.setDisable(true);
        deleteBtn.setDisable(true);
        assignTaskBtn.setDisable(true);
        showDutiesBtn.setDisable(true);
        exportBtn.setDisable(true);
    }

    private void enableButtons(){
        profileBtn.setDisable(false);
        modifyBtn.setDisable(false);
        reportsBtn.setDisable(false);
        deleteBtn.setDisable(false);
        assignTaskBtn.setDisable(false);
        showDutiesBtn.setDisable(false);
        exportBtn.setDisable(false);
    }

    public void exportBtn(ActionEvent actionEvent) throws IOException {
        if(currentInspectorID == -1) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text document", "*.txt"));
        File file = chooser.showSaveDialog(labPhoneNumber.getScene().getWindow());
        if(file!=null){
            try {
                PrintWriter writer;
                writer = new PrintWriter(file);
                String exportData = labInspectorType.getText() + " " + inspectorDAO.getNameSurenameForID(currentInspectorID) + "[" + labUniqueID.getText() + "]\n\n";
                exportData += "Inspection area: " + inspectorDAO.getInspectionAreaForID(currentInspectorID) + "\n";
                exportData += "Birthdate: " + inspectorDAO.getBirthdateForID(currentInspectorID) + "\n";
                exportData += "JMBG: " + inspectorDAO.getJMBGForID(currentInspectorID) + "\n";
                if(inspectorDAO.getGenderForID(currentInspectorID)==1) exportData+= "Gender: Male\n";
                else exportData += "Gender: Female\n";
                exportData += "ID number: " + inspectorDAO.getIDNumberForID(currentInspectorID) + "\n";
                exportData += "E-mail: " + labEmail.getText() + "\n";
                exportData += "Phone number: " + labPhoneNumber.getText() + "\n";
                exportData += "Residency: " + inspectorDAO.getResidenceForID(currentInspectorID) + "\n";
                exportData += "Login e-mail: " + labEmail.getText() + "\n";
                if(inspectorDAO.getDriversLicenseForID(currentInspectorID)==1) exportData+= "Has a valid driver's license\n";
                else exportData += "Doesn't have a valid driver's license\n";
                status.setStatus("Inspector profile - " + inspectorDAO.getFirstNameForID(currentInspectorID) + " " + inspectorDAO.getSurenameForID(currentInspectorID) +  " [" + inspectorDAO.getUniqueIDForID(currentInspectorID) + "] exported. (" + LocalDateTime.now().format(logFormatter) + ")");
                actionLogDAO.addLog(new ActionLog(1, LocalDateTime.now().format(formatter), "Administrator [" + userDAO.getLoggedUserUniqueID()+ "] exported profile - " + inspectorDAO.getFirstNameForID(currentInspectorID) + " " + inspectorDAO.getSurenameForID(currentInspectorID) +  " [" + labUniqueID.getText() + "] ", userDAO.getLoggedUserUniqueID()));
                writer.println(exportData);
                writer.close();
            } catch (IOException | SQLException ex) {
                System.out.println("Error exporting file!");
            }
        }
        updateStatus();
    }

    public void aboutBtn(ActionEvent actionEvent) throws IOException {
        Stage myStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/about.fxml"));
        myStage.setTitle("About");
        myStage.setResizable(false);
        myStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        myStage.show();
    }
}
