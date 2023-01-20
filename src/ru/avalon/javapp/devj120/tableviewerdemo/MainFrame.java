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
            System.arraycopy(csv, 1, data, 0, data.length);
            ((DefaultTableModel)table.getModel()).setDataVector(data, colHdrs);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error reading CSV file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDat(File f) {
        try {

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error reading DAT file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDat(File f) {
        DefaultTableModel tm = (DefaultTableModel) table.getModel();
        String[] colHdrs = new String[tm.getColumnCount()];
        for (int i = 0; i < colHdrs.length; i++) {
            colHdrs[i] = tm.getColumnName(i);
        }
        Vector<Vector> rows = tm.getDataVector();
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < data.length; i++) {
            data[i] = rows.get(i).toArray();
        }
        try {
            DatSupport.writeDat(f, colHdrs, data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error saving dat-file", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        new MainFrame().setVisible(true);
    }
}
