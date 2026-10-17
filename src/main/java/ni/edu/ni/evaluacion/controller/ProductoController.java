package ni.edu.ni.evaluacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.ni.evaluacion.dao.ProductoDAO;
import ni.edu.ni.evaluacion.model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroCategoria;
    @FXML private CheckBox chkExistencias;
    @FXML private Label lblContador;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId, colStock;
    @FXML private TableColumn<Producto, String> colCodigo, colNombre, colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;

    private ProductoDAO dao = new ProductoDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        ObservableList<String> filtros = FXCollections.observableArrayList("Todas", "Papelería", "Tecnología", "Accesorios");
        cmbFiltroCategoria.setItems(filtros);
        cmbFiltroCategoria.setValue("Todas");

        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        buscar();
    }

    @FXML
    private void buscar() {
        try {
            List<Producto> lista = dao.buscarProductos(
                    txtBuscar.getText(),
                    cmbFiltroCategoria.getValue(),
                    chkExistencias.isSelected()
            );
            tablaProductos.setItems(FXCollections.observableArrayList(lista));
            lblContador.setText("Registros visibles: " + lista.size());
        } catch (SQLException e) {
            mostrarError("Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue("Todas");
        chkExistencias.setSelected(false);
        cargarDatos();
    }

    private void mostrarError(String msj) {
        new Alert(Alert.AlertType.ERROR, msj).showAndWait();
    }
}