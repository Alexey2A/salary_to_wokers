package salary;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Scanner;

public class SalaryWindow extends Application {
    Worker worker;
    SalarySource salarySource;
    ArrayList<Worker> workers;
    public static ArrayList<SalarySource> salarySourcesList;
    public static VBox root;
    public static ScrollPane sP;
    public static Label salaryTotalLabel;
    public static Pane sourceAndTotal;

    @Override
    public void start(Stage stage) throws Exception {

        workers = getAllWorkers();
        salarySourcesList = getAllSalarySources();

        AnchorPane anchorPane = new AnchorPane();
        ScrollPane scrollPane = new ScrollPane();
        VBox workersContainer = new VBox();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../gui_swing.fxml"));

//        FXMLLoader fxmlLoader = FXMLLoader.load(getClass().getResource("../gui_swing.fxml"));

        root = fxmlLoader.load();

        sP = (ScrollPane) root.lookup("#sourceField");
        VBox sourcesContainer = new VBox();

        Label balance = (Label) root.lookup("#balance");

        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof AnchorPane) {
                anchorPane = (AnchorPane) node;
            }
        }

        for (Node node : anchorPane.getChildrenUnmodifiable()) {
            if (node instanceof ScrollPane) {
                scrollPane = (ScrollPane) node;
                break;
            }
        }

        workers = getAllWorkers();

        double balanceForLabel = 0;
        for (Worker w : workers) {
            Label label1 = new Label(w.getSurname());
            Label label2 = new Label(w.getName());
            Label label3 = new Label(Double.toString(w.getSalary()));
            double totalSalaryOfWorker = SalaryOfWorker.totalSalaryOfWorker(w);
            Label label4 = new Label(Double.toString(totalSalaryOfWorker));
            balanceForLabel += totalSalaryOfWorker;
            TextField zpNow = new TextField();
            zpNow.setOnAction(dragEvent -> System.out.println("Hello"));
            Button button = new Button("Рассчитать");
            label1.setMinWidth(130);
            label2.setMinWidth(130);
            label3.setMinWidth(60);
            label4.setMinWidth(60);
            zpNow.setMaxWidth(80);
            button.setMinWidth(80);
            HBox hBox = new HBox(15, label1, label2, label3, zpNow, label4, button);
            workersContainer.getChildren().add(hBox);
        }

        scrollPane.setContent(workersContainer);

        double salaryTotal = 0;
        for (SalarySource salarySource : salarySourcesList) {
            salaryTotal += salarySource.getSalarySourceSum();
            HBox panel = new HBox();
            Label label = new Label(salarySource.getSalarySourceName() + " : "
                    + salarySource.getSalarySourceSum());
            Button button1 = new Button("Удалить");
            Button button2 = new Button("Изменить");

            panel.getChildren().add(label);
            panel.getChildren().add(button1);
            panel.getChildren().add(button2);
            sourcesContainer.getChildren().add(panel);
        }
        BigDecimal salaryTotalBigDecimal = new BigDecimal(salaryTotal);
        salaryTotalBigDecimal = salaryTotalBigDecimal.setScale(2, RoundingMode.HALF_EVEN);
        salaryTotal = salaryTotalBigDecimal.doubleValue();
        sP.setContent(sourcesContainer);

        salaryTotalLabel = (Label) root.lookup("#salaryTotal");
        salaryTotalLabel.setText(Double.toString(salaryTotal));


//        TODO
        double d = Double.parseDouble(salaryTotalLabel.getText());
        balanceForLabel = d - balanceForLabel;
        BigDecimal balanceForLabelBigDecimal = new BigDecimal(balanceForLabel);
        balanceForLabelBigDecimal = balanceForLabelBigDecimal.setScale(2, RoundingMode.HALF_EVEN);
        balanceForLabel = balanceForLabelBigDecimal.doubleValue();
        balance.setText(Double.toString(balanceForLabel));
        


        Scene scene = new Scene(root, 1000, 500);
        stage.setTitle("H W");
        stage.setScene(scene);
        stage.show();
    }

    public static ArrayList<Worker> getAllWorkers() throws FileNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Worker> workers = new ArrayList<>();
        Scanner scanner = new Scanner(new FileInputStream("workers.txt"));
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            try {
                Worker worker = objectMapper.readValue(s, Worker.class);
                workers.add(worker);
            } catch (Exception e) {
                System.out.println("Exception!!!");
            }
        }
        scanner.close();
        return workers;
    }

    public static ArrayList<SalarySource> getAllSalarySources() throws FileNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<SalarySource> sources = new ArrayList<>();
        Scanner scanner = new Scanner(new FileInputStream("salarySource.txt"));
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            try {
                SalarySource source = objectMapper.readValue(s, SalarySource.class);
                sources.add(source);
            } catch (Exception e) {
                System.out.println("Exception!!!");
            }
        }
        scanner.close();
        return sources;
    }

    public static void main(String[] args) {
        launch();
    }
}
