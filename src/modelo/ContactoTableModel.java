/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ContactoTableModel extends AbstractTableModel {
    private final String[] columnas = {"ID", "Nombre", "Teléfono", "Email", "Categoría"};
    private List<Contacto> datos = new ArrayList<>();

    public void setDatos(List<Contacto> datos) {
        this.datos = datos;
        fireTableDataChanged();
    }

    public Contacto getContactoAt(int fila) {
        return datos.get(fila);
    }

    @Override
    public int getRowCount() { return datos.size(); }
    @Override
    public int getColumnCount() { return columnas.length; }
    @Override
    public String getColumnName(int column) { return columnas[column]; }
    @Override
    public boolean isCellEditable(int row, int col) { return false; }

    @Override
    public Object getValueAt(int row, int col) {
        Contacto c = datos.get(row);
        switch (col) {
            case 0: return c.getId();
            case 1: return c.getNombre();
            case 2: return c.getTelefono();
            case 3: return c.getEmail();
            case 4: return c.getCategoria();
            default: return null;
        }
    }
}