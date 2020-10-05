package rs.acreno.system.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.jetbrains.annotations.NotNull;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;

import java.util.ArrayList;
import java.util.List;

public class DragAndDropTable {
    private static final List<PosaoArtikli> selections = new ArrayList<>();
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    public static void dragAndDropTbl(@NotNull TableView<PosaoArtikli> table) {
        //DRAG AND DROP TABLE ITEM IN "tblPosaoArtikli" for print
        table.setRowFactory(tv -> {
            TableRow<PosaoArtikli> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();

                    selections.clear();//important...

                    ObservableList<PosaoArtikli> items = table.getSelectionModel().getSelectedItems();

                    selections.addAll(items);


                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();

                if (db.hasContent(SERIALIZED_MIME_TYPE)) {

                    int dropIndex;
                    Object dI = null;

                    if (row.isEmpty()) {
                        dropIndex = table.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                        dI = table.getItems().get(dropIndex);
                    }
                    int delta = 0;
                    if (dI != null)
                        while (selections.contains(dI)) {
                            delta = 1;
                            --dropIndex;
                            if (dropIndex < 0) {
                                dI = null;
                                dropIndex = 0;
                                break;
                            }
                            dI = table.getItems().get(dropIndex);
                        }

                    for (PosaoArtikli sI : selections) {
                        table.getItems().remove(sI);
                    }

                    if (dI != null)
                        dropIndex = table.getItems().indexOf(dI) + delta;
                    else if (dropIndex != 0)
                        dropIndex = table.getItems().size();

                    table.getSelectionModel().clearSelection();

                    for (PosaoArtikli sI : selections) {
                        //draggedIndex = selections.get(i);
                        table.getItems().add(dropIndex, sI);
                        table.getSelectionModel().select(dropIndex);
                        dropIndex++;
                    }

                    event.setDropCompleted(true);
                    selections.clear();
                    event.consume();
                }
            });
            return row;
        });
    }
}
