package ru.avalon.javapp.devj120.tableviewerdemo;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataModel implements TableModel {
    private String[] columnNames;
    private Object[][] rowData;

    private Class[] columnClass;
    public DataModel(String[] columnNames, Object[][] rowData, Class[] columnClass) {
        setColumnNames(columnNames);
        setRowData(rowData);
        setColumnClass(columnClass);
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public Object[][] getRowData() {
        return rowData;
    }

    public void setRowData(Object[][] rowData) {
        this.rowData = rowData;
    }

    public Class[] getColumnClass() {
        return columnClass;
    }

    public void setColumnClass(Class[] columnClass) {
        this.columnClass = columnClass;
    }

    @Override
    public int getRowCount() {
        return rowData.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnClass[columnIndex].equals(Integer.class))
            return Integer.parseInt((String) rowData[rowIndex][columnIndex]);
        if (columnClass[columnIndex].equals(BigDecimal.class)) {
            if (rowData[rowIndex][columnIndex] == null) {
                return "";
            } else {
                String value = (String) rowData[rowIndex][columnIndex];
                value = value.replace(",", ".");
                return new BigDecimal(value);
            }
        }
        if (columnClass[columnIndex].equals(Boolean.class))
            return rowData[rowIndex][columnIndex].equals("true") ? Boolean.TRUE:Boolean.FALSE;
        if (columnClass[columnIndex].equals(LocalDate.class)) {
            String[] dataVal;
            String values = (String) rowData[rowIndex][columnIndex];
            dataVal = values.split("-");
            LocalDate inputData = LocalDate.of(Integer.parseInt(dataVal[0]), Integer.parseInt(dataVal[1]), Integer.parseInt(dataVal[2]));
            return inputData.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        return rowData[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
