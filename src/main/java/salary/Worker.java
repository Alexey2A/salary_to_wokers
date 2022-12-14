package salary;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.io.*;
import java.util.*;

public class Worker {
    private String name;
    private String surname;
    private String patronymic;
    private double salary;
    //    private double percentageOfTheSumOfAllSources;
//    private double salaryFromTheSource;
    private double totalSalary;
    private int numberOfDaysMissed;

    public String getName() {
        return name;
    }

    public Worker() {
    }

    public Worker(String name, String surname, double salary) {
        this.name = name;
        this.surname = surname;
        this.salary = salary;
    }
    public Worker(String name, String surname, String patronymic, double salary) {
        this.name = name;
        this.surname = surname;
        this.salary = salary;
        this.patronymic = patronymic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getNumberOfDaysMissed() {
        return numberOfDaysMissed;
    }

    public void setNumberOfDaysMissed(int numberOfDaysMissed) {
        this.numberOfDaysMissed = numberOfDaysMissed;
    }

    //    public double getPercentageOfTheSumOfAllSources() {
//        return percentageOfTheSumOfAllSources;
//    }
//
//    public void setPercentageOfTheSumOfAllSources(double percentageOfTheSumOfAllSources) {
//        this.percentageOfTheSumOfAllSources = percentageOfTheSumOfAllSources;
//    }
//
//    public double getSalaryFromTheSource() {
//        return salaryFromTheSource;
//    }
//
//    public void setSalaryFromTheSource(double salaryFromTheSource) {
//        this.salaryFromTheSource = salaryFromTheSource;
//    }
//
    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return name.equals(worker.name) && surname.equals(worker.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, salary);
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

    public void addAnWorker(Worker w) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("workers.txt");
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
        String s = objectMapper.writeValueAsString(w);
        printWriter.println(s);
        printWriter.close();

        Scanner scanner = new Scanner(new FileInputStream(file));
        Set<Worker> workerSet = new HashSet<>();
        while (scanner.hasNextLine()) {
            String s2 = scanner.nextLine();
            try {
                Worker worker = objectMapper.readValue(s2, Worker.class);
                workerSet.add(worker);
            } catch (JsonParseException | MismatchedInputException ignored) {
                System.out.println("JsonParseException or MismatchedInputException");
            }
        }

        PrintWriter printWriter2 = new PrintWriter(new FileOutputStream(file));
        for (Worker worker : workerSet) {
            String s3 = objectMapper.writeValueAsString(worker);
            printWriter2.println(s3);
        }
        printWriter2.close();
    }

    public static void deleteAnWorker(Worker w) throws FileNotFoundException
            , JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("workers.txt");
        Set<Worker> workerSet = new HashSet<>();
        Scanner scanner = new Scanner(new FileInputStream(file));
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            try {
                Worker worker = objectMapper.readValue(s, Worker.class);
                if (!w.equals(worker)) {
                    workerSet.add(worker);
                }
            } catch (Exception e) {
                System.out.println("Exception!!!");
            }
        }
        scanner.close();
        PrintWriter printWriter = new PrintWriter(file);
        for (Worker worker : workerSet) {
            printWriter.println(objectMapper.writeValueAsString(worker));
        }
        printWriter.close();
    }

    //TODO
    public static void updateWorkerAndSalary() throws FileNotFoundException {
        VBox workersContainer = new VBox();
        List<Worker> workerList = getAllWorkers();
        for (Worker w : workerList) {
            Label label1 = new Label(w.getSurname());
            Label label2 = new Label(w.getName());
            Label label3 = new Label(Double.toString(w.getSalary()));
            double totalSalaryOfWorker = SalaryOfWorker.totalSalaryOfWorker(w);
            Label label4 = new Label(Double.toString(SalaryOfWorker.rounding(totalSalaryOfWorker)));
            SalaryWindow.balanceForLabel += totalSalaryOfWorker;
            updateWorkerInFile(w, label4);

            TextField zpNow = new TextField();
            zpNow.setOnAction(actionEvent -> {
                double previousSalary = Double.parseDouble(label4.getText());
                double newSalary = Double.parseDouble(zpNow.getText());
                double balance = Double.parseDouble(SalaryWindow.balance.getText());
                double newBalance = balance + previousSalary - newSalary;
                newBalance = SalaryOfWorker.rounding(newBalance);
                label4.setText(zpNow.getText());
                SalaryWindow.balance.setText(Double.toString(newBalance));
                if (newBalance < 0) {
                    SalaryWindow.balance.setBackground(new Background
                            (new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                } else SalaryWindow.balance.setBackground(new Background(
                        new BackgroundFill(new Color(0,0,0,0),CornerRadii.EMPTY,Insets.EMPTY)));

                zpNow.clear();
                updateWorkerInFile(w, label4);
            });

            Button button = new Button("??????????????");
            button.setFont(new Font(10));
            button.setOnAction(actionEvent -> {
                try {
                    deleteAnWorker(w);
                } catch (FileNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                try {
                    updateWorkerAndSalary();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

            Insets insets = new Insets(3,2,2,3);
            label1.setMinWidth(130);
            HBox.setMargin(label1, insets);
            label2.setMinWidth(130);
            HBox.setMargin(label2, insets);
            label3.setMinWidth(60);
            HBox.setMargin(label3, insets);
            label4.setMinWidth(60);
            HBox.setMargin(label4, insets);
            zpNow.setMaxWidth(80);
            button.setMinWidth(50);
            HBox.setMargin(button, new Insets(0,0,0,30));
            HBox hBox = new HBox(15, label1, label2, label3, zpNow, label4, button);
            workersContainer.getChildren().add(hBox);

            Line line = new Line(0, 0, 635, 0);
            line.setStrokeWidth(5);
            line.setStroke(Color.WHITE);
            workersContainer.getChildren().add(line);
        }

        double d = Double.parseDouble(SalaryWindow.salaryTotalLabel.getText());
        SalaryWindow.balanceForLabel = d - SalaryWindow.balanceForLabel;
        SalaryWindow.balanceForLabel = SalaryOfWorker.rounding(SalaryWindow.balanceForLabel);
        SalaryWindow.balance.setText(Double.toString(SalaryWindow.balanceForLabel));

        SalaryWindow.workerField.setContent(workersContainer);
    }

    public static void updateWorkerInFile(Worker w, Label label) {
        Worker worker = new Worker();
        worker.setName(w.getName());
        worker.setSurname(w.getSurname());
        worker.setSalary(w.getSalary());
        worker.setTotalSalary(Double.parseDouble(label.getText()));
        try {
            deleteAnWorker(w);
        } catch (FileNotFoundException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            worker.addAnWorker(worker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
