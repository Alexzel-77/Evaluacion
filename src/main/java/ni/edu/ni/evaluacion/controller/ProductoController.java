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
import java.util.Optional;

public class ProductoController {

    @FXML private TextField txtBuscar, txtCodigo, txtNombre, txtPrecio, txtStock;
    @FXML private ComboBox<String> cmbFiltroCategoria, cmbCategoria;
    @FXML private CheckBox chkExistencias;
    @FXML private Label lblContador;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId, colStock;
    @FXML private TableColumn<Producto, String> colCodigo, colNombre, colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;

    private ProductoDAO dao = new ProductoDAO();
    private Producto productoSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        ObservableList<String> categorias = FXCollections.observableArrayList("Papelería", "Tecnología", "Accesorios");
        cmbCategoria.setItems(categorias);

        ObservableList<String> filtros = FXCollections.observableArrayList("Todas", "Papelería", "Tecnología", "Accesorios");
        cmbFiltroCategoria.setItems(filtros);
        cmbFiltroCategoria.setValue("Todas");

        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        buscar(); // Reutilizamos el método buscar para cargar inicial
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
    private void guardar() {
        try {
            // Normalización exigida por el laboratorio
            String cod = txtCodigo.getText().trim().toUpperCase();
            String nom = txtNombre.getText().trim();
            String cat = cmbCategoria.getValue();
            BigDecimal prec = new BigDecimal(txtPrecio.getText().trim());
            int stk = Integer.parseInt(txtStock.getText().trim());

            Producto p = new Producto(0, cod, nom, cat, prec, stk);
            dao.insertar(p);

            mostrarMensaje("Éxito", "Producto guardado correctamente.");
            limpiarFormulario();
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Fallo en la base de datos (Ej. Código repetido o chequeo fallido):\n" + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarError("Por favor ingrese números válidos en Precio y Stock.");
        }
    }

    @FXML
    private void eliminar() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto primero.");
            return;
        }

        // Confirmación exigida por el PDF
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setContentText("¿Está seguro de eliminar " + productoSeleccionado.getCodigo() + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int filas = dao.eliminar(productoSeleccionado.getIdProducto());
                if(filas == 0) mostrarError("El producto ya no existe en la BD.");
                else mostrarMensaje("Éxito", "Eliminado correctamente.");

                limpiarFormulario();
                cargarDatos();
            } catch (SQLException e) {
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void seleccionarRegistro() {
        productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            txtCodigo.setText(productoSeleccionado.getCodigo());
            txtNombre.setText(productoSeleccionado.getNombre());
            cmbCategoria.setValue(productoSeleccionado.getCategoria());
            txtPrecio.setText(productoSeleccionado.getPrecio().toString());
            txtStock.setText(String.valueOf(productoSeleccionado.getStock()));
        }
    }

    @FXML private void limpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue("Todas");
        chkExistencias.setSelected(false);
        cargarDatos();
    }

    @FXML private void limpiarFormulario() {
        productoSeleccionado = null;
        txtCodigo.clear(); txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear(); txtStock.clear();
    }

    private void mostrarMensaje(String titulo, String msj) {
        new Alert(Alert.AlertType.INFORMATION, msj).showAndWait();
    }

    private void mostrarError(String msj) {
        new Alert(Alert.AlertType.ERROR, msj).showAndWait();
    }
}