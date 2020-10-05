package rs.acreno.system.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://www.techgalery.com/2019/08/javafx-combo-box-with-search.html</a>
 */
public class AutoCompleteBox implements EventHandler<KeyEvent> {

    private static final Logger logger = Logger.getLogger(AutoCompleteBox.class);

    private final ComboBox<String> comboBox;
    final private ObservableList<String> data;
    private Integer sid;

    public AutoCompleteBox(final @NotNull ComboBox<String> comboBox) {
        this.comboBox = comboBox;
        this.data = comboBox.getItems();

        this.doAutoCompleteBox();
    }

    public AutoCompleteBox(final @NotNull ComboBox<String> comboBox, Integer sid) {
        this.comboBox = comboBox;
        this.data = comboBox.getItems();
        this.sid = sid;

        this.doAutoCompleteBox();
    }

    private void doAutoCompleteBox() {
        this.comboBox.setEditable(true);
        this.comboBox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {//mean onfocus
                this.comboBox.show();
            }
        });

        this.comboBox.getEditor().setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    return;
                }
            }
            this.comboBox.show();
        });

        this.comboBox.getSelectionModel().selectedIndexProperty().
                addListener((observable, oldValue, newValue) -> moveCaret(this.comboBox.getEditor().getText().length()));

        this.comboBox.setOnKeyPressed(t -> comboBox.hide());

        this.comboBox.setOnKeyReleased(AutoCompleteBox.this);

        if (this.sid != null)
            this.comboBox.getSelectionModel().select(this.sid);
    }

    @Override
    public void handle(@NotNull KeyEvent event) {
        if (event.getCode() == KeyCode.UP
                || event.getCode() == KeyCode.DOWN
                || event.getCode() == KeyCode.RIGHT
                || event.getCode() == KeyCode.LEFT
                || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END
                || event.getCode() == KeyCode.TAB
                || event.getCode() == KeyCode.SPACE) {
            return;
        }

        if (event.getCode() == KeyCode.BACK_SPACE) {
            String str = this.comboBox.getEditor().getText();
            if (str != null && str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            if (str != null) {
                this.comboBox.getEditor().setText(str);
                moveCaret(str.length());
            }
            this.comboBox.getSelectionModel().clearSelection();
        }

        if (event.getCode() == KeyCode.ENTER && comboBox.getSelectionModel().getSelectedIndex() > -1) {
            return;
        }

        setItems();
    }

    ObservableList<String> list;

    private void setItems() {
        list = FXCollections.observableArrayList();

        for (Object datum : this.data) {
            String s = this.comboBox.getEditor().getText().toLowerCase();
            if (datum.toString().toLowerCase().contains(s.toLowerCase())) {
                list.add(datum.toString());
            }
        }
        if (list.isEmpty()) {
            this.comboBox.hide();
        } else {
            this.comboBox.setItems(list);
            this.comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        this.comboBox.getEditor().positionCaret(textLength);
    }
}
