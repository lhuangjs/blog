package javafx.tableview;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class TableWithPaginationAndSorting<T> {
    private Page<T> page;
    private TableView<T> tableView;
    private Pagination tableViewWithPaginationPane;

    public Page<T> getPage() {
        return page;
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    public Pagination getTableViewWithPaginationPane() {
        return tableViewWithPaginationPane;
    }

    public TableWithPaginationAndSorting(Page<T> page, TableView<T> tableView) {
        this.page = page;
        this.tableView = tableView;
        tableViewWithPaginationPane = new Pagination();
        tableViewWithPaginationPane.pageCountProperty().bindBidirectional(page.totalPageProperty());
        updatePagination();
    }

    private void updatePagination() {
        tableViewWithPaginationPane.setPageFactory(pageIndex -> {
            tableView.setItems(FXCollections.observableList(page.getCurrentPageDataList(pageIndex)));
            return tableView;
        });
    }

    private HashMap<Label, String>  orderMap = new HashMap<>();
    public void addGlobalOrdering(TableColumn<T, ?> column,
                                  Comparator<? super T> ascComparator,
                                  Comparator<? super T> descComparator) {

        /** label setting **/
        // set  column name to label text
        Label label = new Label(column.getText());
        label.setMinWidth(column.getMinWidth());
        label.setMaxWidth(column.getMaxWidth());
        label.setPrefWidth(column.getPrefWidth());

        // by default, set the column has not been sort
        orderMap.put(label, "NO");

        /** column setting **/
        // set built-in column name to null
        column.setText(null);

        // turn off built-in order in TableView
        column.setSortable(false);

        // make label replace original table header
        column.setGraphic(label);

        ImageView ascImg = new ImageView("/image-app/asc.png");
        ImageView descImg = new ImageView("/image-app/desc.png");
        ImageView noImg = new ImageView("/image-app/no.png");

        // set event
        label.setOnMouseClicked(mouseEvent -> {

            // sort only one column every time, so make other columns unsorted
            orderMap.keySet().stream().forEach(lab -> lab.setGraphic(noImg));
            switch (orderMap.get(label)) {
                case "NO":
                    orderMap.replace(label, "ASC");
                    label.setGraphic(ascImg);
                    order(ascComparator);
                    updatePagination();
                    break;
                case "ASC":
                    orderMap.put(label, "DESC");
                    label.setGraphic(descImg);
                    order(descComparator);
                    updatePagination();
                    break;
                case "DESC":
                    orderMap.put(label, "ASC");
                    label.setGraphic(ascImg);
                    order(ascComparator);
                    updatePagination();
                    break;
            }
        });
    }

    /**
     * sort a list by given comparator
     * @param comparator
     */
    private void order(Comparator<? super T> comparator) {
        Collections.sort(page.getRowDataList(), comparator);
    }
}
