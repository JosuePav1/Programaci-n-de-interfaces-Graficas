/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import modelo.ContactoTableModel;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public JTextField txtNombre, txtTelefono, txtEmail, txtBuscar;
    public JComboBox<String> cbCategoria, cbFiltro;
    public JTable tablaContactos;
    public JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar, btnExportar;
    public JProgressBar barraProgreso;
    public JTabbedPane tabbedPane;
    public JTextArea areaEstadisticas;
    public JPopupMenu menuContextual; 
    public JMenuItem itemEliminar, itemEditar;

    private final ContactoTableModel tableModel = new ContactoTableModel();

    public VentanaPrincipal() {
        setTitle("Gestión de Contactos - Avanzado");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        
        // --- Pestaña 1: Contactos ---
        JPanel panelContactos = new JPanel(new BorderLayout(10, 10));
        panelContactos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Contacto"));
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelFormulario.add(txtTelefono);
        panelFormulario.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail);
        panelFormulario.add(new JLabel("Categoría:"));
        cbCategoria = new JComboBox<>(new String[]{"Familia", "Trabajo", "Amigos", "Otros"});
        panelFormulario.add(cbCategoria);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnExportar = new JButton("Exportar CSV");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnExportar);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.CENTER);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);
        panelContactos.add(panelNorte, BorderLayout.NORTH);

        // Tabla con ordenamiento
        tablaContactos = new JTable(tableModel);
        tablaContactos.setRowSorter(new TableRowSorter<>(tableModel)); 
        JScrollPane scrollTabla = new JScrollPane(tablaContactos);
        panelContactos.add(scrollTabla, BorderLayout.CENTER);

        // Barra de progreso
        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);
        panelContactos.add(barraProgreso, BorderLayout.SOUTH);

        // --- Pestaña 2: Estadísticas ---
        JPanel panelEstadisticas = new JPanel(new BorderLayout());
        areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panelEstadisticas.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);

        // Agregar pestañas al TabbedPane
        tabbedPane.addTab("Contactos", panelContactos);
        tabbedPane.addTab("Estadísticas", panelEstadisticas);
        add(tabbedPane, BorderLayout.CENTER);

        // Menú Contextual (Clic derecho en la tabla)
        menuContextual = new JPopupMenu();
        itemEditar = new JMenuItem("Editar Contacto");
        itemEliminar = new JMenuItem("Eliminar Contacto");
        menuContextual.add(itemEditar);
        menuContextual.add(itemEliminar);

        // Barra de búsqueda y filtro
        JPanel panelSur = new JPanel(new FlowLayout());
        panelSur.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        panelSur.add(txtBuscar);
        panelSur.add(new JLabel("Filtrar por:"));
        cbFiltro = new JComboBox<>(new String[]{"Todas", "Familia", "Trabajo", "Amigos", "Otros"});
        panelSur.add(cbFiltro);
        add(panelSur, BorderLayout.SOUTH);
    }

    public ContactoTableModel getTableModel() { return tableModel; }
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getTelefono() { return txtTelefono.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getCategoria() { return cbCategoria.getSelectedItem().toString(); }
    public String getTextoBuscar() { return txtBuscar.getText().trim(); }
    public String getFiltro() { return cbFiltro.getSelectedItem().toString(); }

    public void setCampos(String n, String t, String e, String c) {
        txtNombre.setText(n);
        txtTelefono.setText(t);
        txtEmail.setText(e);
        cbCategoria.setSelectedItem(c);
    }

    public void limpiarFormulario() {
        txtNombre.setText(""); txtTelefono.setText(""); txtEmail.setText("");
        cbCategoria.setSelectedIndex(0);
        tablaContactos.clearSelection();
    }

    public int getFilaSeleccionada() {
        return tablaContactos.getSelectedRow();
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmar(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}