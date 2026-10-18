/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Contacto;
import modelo.ContactoDAO;
import vista.VentanaPrincipal;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class ContactoController {

    private final VentanaPrincipal vista;
    private final ContactoDAO dao;

    public ContactoController(VentanaPrincipal vista, ContactoDAO dao) {
        this.vista = vista;
        this.dao = dao;
        configurarEventos();
        configurarAtajosTeclado();
        cargarConProgreso();
    }

    private void configurarEventos() {
        // Botones
        vista.btnAgregar.addActionListener(e -> agregar());
        vista.btnEditar.addActionListener(e -> editar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnLimpiar.addActionListener(e -> vista.limpiarFormulario());
        vista.btnExportar.addActionListener(e -> exportarCSV());

        // Búsqueda en vivo
        vista.txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { cargarTabla(); }
        });

        // Filtro por categoría
        vista.cbFiltro.addActionListener(e -> cargarTabla());

        // Evento de selección en la tabla (Mouse)
        vista.tablaContactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int fila = vista.tablaContactos.rowAtPoint(e.getPoint());
                    if (fila != -1) {
                        vista.tablaContactos.setRowSelectionInterval(fila, fila);
                        vista.menuContextual.show(vista.tablaContactos, e.getX(), e.getY());
                    }
                } else {
                    seleccionarFila();
                }
            }
        });

        // Eventos del menú contextual (Clic derecho)
        vista.itemEditar.addActionListener(e -> editar());
        vista.itemEliminar.addActionListener(e -> eliminar());

        // Al cambiar de pestaña, actualizar estadísticas
        vista.tabbedPane.addChangeListener(e -> {
            if (vista.tabbedPane.getSelectedIndex() == 1) {
                actualizarEstadisticas();
            }
        });
    }

    // Atajos de teclado (Ctrl+N, Ctrl+G, Suprimir)
    private void configurarAtajosTeclado() {
        JRootPane root = vista.getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // Ctrl + N -> Limpiar formulario
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "nuevo");
        am.put("nuevo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { vista.limpiarFormulario(); }
        });

        // Ctrl + G -> Guardar (Agregar)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "guardar");
        am.put("guardar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { agregar(); }
        });

        // Tecla Suprimir -> Eliminar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        am.put("eliminar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { eliminar(); }
        });
    }

    // Simula la carga de contactos con la barra de progreso
    private void cargarConProgreso() {
        vista.barraProgreso.setMaximum(100);
        vista.barraProgreso.setValue(0);
        vista.barraProgreso.setString("Cargando...");
        
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(40); // Simula tiempo de carga
                    publish(i);
                }
                return null;
            }
            @Override
            protected void process(List<Integer> chunks) {
                vista.barraProgreso.setValue(chunks.get(chunks.size() - 1));
            }
            @Override
            protected void done() {
                vista.barraProgreso.setString("Carga completada");
                cargarTabla();
            }
        };
        worker.execute();
    }

    private void cargarTabla() {
        List<Contacto> lista;
        String texto = vista.getTextoBuscar();
        if (!texto.isEmpty()) {
            lista = dao.buscar(texto);
        } else {
            lista = dao.filtrarPorCategoria(vista.getFiltro());
        }
        vista.getTableModel().setDatos(lista);
    }

    private void agregar() {
        if (vista.getNombre().isEmpty()) {
            vista.mostrarError("El nombre es obligatorio.");
            return;
        }
        dao.agregar(new Contacto(0, vista.getNombre(), vista.getTelefono(), vista.getEmail(), vista.getCategoria()));
        vista.limpiarFormulario();
        cargarTabla();
    }

    private void editar() {
        int fila = vista.getFilaSeleccionada();
        if (fila == -1) {
            vista.mostrarError("Selecciona un contacto para editar.");
            return;
        }
        Contacto c = vista.getTableModel().getContactoAt(fila);
        if (vista.getNombre().isEmpty()) {
            vista.mostrarError("El nombre es obligatorio.");
            return;
        }
        c.setNombre(vista.getNombre());
        c.setTelefono(vista.getTelefono());
        c.setEmail(vista.getEmail());
        c.setCategoria(vista.getCategoria());
        dao.actualizar(c);
        vista.limpiarFormulario();
        cargarTabla();
    }

    private void eliminar() {
        int fila = vista.getFilaSeleccionada();
        if (fila == -1) {
            vista.mostrarError("Selecciona un contacto para eliminar.");
            return;
        }
        Contacto c = vista.getTableModel().getContactoAt(fila);
        if (vista.confirmar("¿Eliminar el contacto " + c.getNombre() + "?")) {
            dao.eliminar(c.getId());
            vista.limpiarFormulario();
            cargarTabla();
        }
    }

    private void seleccionarFila() {
        int fila = vista.getFilaSeleccionada();
        if (fila == -1) return;
        Contacto c = vista.getTableModel().getContactoAt(fila);
        vista.setCampos(c.getNombre(), c.getTelefono(), c.getEmail(), c.getCategoria());
    }

    private void exportarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");
        if (fileChooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".csv")) {
                archivo = new File(archivo.getAbsolutePath() + ".csv");
            }
            try {
                dao.exportarCSV(archivo.getAbsolutePath());
                JOptionPane.showMessageDialog(vista, "Exportado con éxito a:\n" + archivo.getAbsolutePath());
            } catch (Exception ex) {
                vista.mostrarError("Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void actualizarEstadisticas() {
        List<Contacto> todos = dao.listar();
        int familia = 0, trabajo = 0, amigos = 0, otros = 0;
        for (Contacto c : todos) {
            switch (c.getCategoria()) {
                case "Familia": familia++; break;
                case "Trabajo": trabajo++; break;
                case "Amigos": amigos++; break;
                default: otros++;
            }
        }
        
        String texto = "=== ESTADÍSTICAS DE CONTACTOS ===\n\n" +
                       "Total de contactos: " + todos.size() + "\n\n" +
                       "Familia: " + familia + "\n" +
                       "Trabajo: " + trabajo + "\n" +
                       "Amigos: " + amigos + "\n" +
                       "Otros: " + otros;
        vista.areaEstadisticas.setText(texto);
    }
}