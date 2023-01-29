package ru.avalon.javapp.devj120.tableviewerdemo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class MainFrame extends JFrame {
    private final JTable table;
    private final JFileChooser chooser;
    
    public MainFrame() {
        super("Table viewer");
        
        setBounds(900, 600, 600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chooser = new JFileChooser();
        
        JToolBar tb = new JToolBar();
        Container cp = getContentPane();
        
        JButton btn = new JButton(new ImageIcon("fopen.png"));
        btn.setToolTipText("Open file...");
        btn.addActionListener(e -> {
            int res = chooser.showOpenDialog(this);
            if(res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if(f.getName().endsWith(".csv"))
                    openCsv(f);
                if (f.getName().endsWith(".dat"))
                    openDat(f);
            }
        });
        tb.add(btn);
        
        btn = new JButton(new ImageIcon("fsave.png"));
        btn.setToolTipText("Save file...");
        btn.addActionListener(e -> {
            int res = chooser.showSaveDialog(this);
            if(res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if(f.exists()) {
                    if(JOptionPane.showConfirmDialog(this, "Are you sure you want to overwrite the file?", 
                            "File overwriting confirmation", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
                        return;
                }
                if(f.getName().endsWith(".csv"))
                    saveCsv(f);
                if(f.getName().endsWith(".dat"))
                    saveDat(f);
            }
        });
        tb.add(btn);
        
        cp.add(tb, BorderLayout.NORTH);
        
        table = new JTable();
        JPanel p = new JPanel(new BorderLayout());
        p.add(table.getTableHeader(), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        cp.add(p, BorderLayout.CENTER);
        
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV-file", "csv"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Dat-file", "dat"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
    }

    private void openCsv(File f) {
        try {
            String[][] csv = CsvSupport.readCsv(f);
            String[] colHdrs = csv[0];
            String[][] data = new String[csv.length - 1][];
            System.arraycopy(csv, 1, data, 0, data.length);//заполнение строк
            table.setModel(new DataModel(colHdrs, data, CsvSupport.columnClassDetect(data)));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error reading CSV file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDat(File f) {
        try {
            DatSupport datSupport = new DatSupport();
            String[][] dat = datSupport.readDat(f);
            String[] colHdrs = dat[0];
            String[][] data = new String[dat.length - 1][];
            System.arraycopy(dat, 1, data, 0, data.length);
            table.setModel(new DataModel(colHdrs, data, datSupport.columnDatClass));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error reading DAT file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCsv(File f) {
        DataModel dm = (DataModel) table.getModel();
        String[] colHdrs = dm.getColumnNames();
        Object[][] data = dm.getRowData();
        try {
            CsvSupport.writeCsv(f, colHdrs, data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error saving dat-file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDat(File f) {
        DataModel dm = (DataModel) table.getModel();
        String[] colHdrs = dm.getColumnNames();
        Object[][] data = dm.getRowData();
        try {
            DatSupport.writeDat(f, colHdrs, data, dm.getColumnClass());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error saving dat-file", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        new MainFrame().setVisible(true);
    }
}
