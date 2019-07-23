package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Controller {
    @FXML
    Tab tabLine;
    @FXML
    Tab tabSun;

    // отрезки
    @FXML
    TextField xBeginField;
    @FXML
    TextField yBeginField;
    @FXML
    TextField xEndField;
    @FXML
    TextField yEndField;

    // солнышко
    @FXML
    TextField xCenterField;
    @FXML
    TextField yCenterField;
    @FXML
    TextField lengthField;
    @FXML
    TextField stepAngleField;

    @FXML
    ChoiceBox algorithmChoice;
    @FXML
    ColorPicker colorChoice;

    @FXML
    Button drawChosenButton;
    @FXML
    Button drawBackgroundButton;

    @FXML
    Canvas canvas;
    @FXML
    Label cursorLabel;
    @FXML
    ColorPicker backgroundPicker;

    @FXML
    public void initialize() {
        ObservableList<String> algorithmsList = FXCollections.observableList(
                Arrays.asList(
                        DrawAlgorithms.ALG_DDA,
                        DrawAlgorithms.ALG_BDA,
                        DrawAlgorithms.ALG_BIA,
                        DrawAlgorithms.ALG_BSA,
                        DrawAlgorithms.ALG_LIB,
                        DrawAlgorithms.ALG_WOO
                ));
        algorithmChoice.setItems(algorithmsList);
        algorithmChoice.getSelectionModel().selectFirst();
        canvas.getGraphicsContext2D().setFill(backgroundPicker.getValue());
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        backgroundPicker.setOnAction(actionEvent -> {
            canvas.getGraphicsContext2D().setFill(backgroundPicker.getValue());
            canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        canvas.setOnMouseMoved(mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                cursorLabel.setText("Текущая позиция курсора:\n" + mouseEvent.getX() + "," + mouseEvent.getY());
            }
        });


        drawChosenButton.setOnMouseClicked(mouseEvent -> {
            draw(colorChoice);
        });

        drawBackgroundButton.setOnMouseClicked(mouseEvent -> {
            draw(backgroundPicker);
        });
    }

    private void draw(ColorPicker colorChoice) {
        try {
            String alg = (String) algorithmChoice.getSelectionModel().getSelectedItem();
            Color color = colorChoice.getValue();
            DrawAlgorithms drawAlgorithms = new DrawAlgorithms(canvas.getGraphicsContext2D(), backgroundPicker.getValue());
            if (tabLine.isSelected()) {
                int x0 = Integer.parseInt(xBeginField.getText());
                int xe = Integer.parseInt(xEndField.getText());
                int y0 = Integer.parseInt(yBeginField.getText());
                int ye = Integer.parseInt(yEndField.getText());
                drawAlgorithms.runAlgorithm(alg, x0, y0, xe, ye, color);
            } else if (tabSun.isSelected()) {
                int xc = Integer.parseInt(xCenterField.getText());
                int yc = Integer.parseInt(yCenterField.getText());
                double len = Double.parseDouble(lengthField.getText());
                double step = Double.parseDouble(stepAngleField.getText());
                if (len < 0) {
                    throw new Exception("err");
                }
                drawAlgorithms.runSun(alg, xc, yc, len, step, color);
            }
        } catch (Exception e) {
            showError("Введены некорректные данные!");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Произошла ошибка :(");
        alert.setHeaderText("ОШИБКА");
        alert.show();
    }
}
