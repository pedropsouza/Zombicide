package org.engcomp.Zombicide;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class BtnMatrix extends VBox {
    protected Matrix<Button> btns;
    protected GridPane btnGrid;

    public BtnMatrix(Matrix<Object> mat) {
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                //"custom_control.fxml"));
        //fxmlLoader.setRoot(this);
        //fxmlLoader.setController(this);

        //try {
            //fxmlLoader.load();
        //} catch (IOException exception) {
            //throw new RuntimeException(exception);
        //}

        for (int y = 0; y < mat.getRows(); y++) {
            for (int x = 0; x < mat.getCols(); x++) {
                var btn = new Button(mat.get(x,y).toString());
                this.btnGrid.add(btn, x, y);
            }
        }
    }

    protected void doSomething() {
        System.out.println("The button was clicked!");
    }
}
