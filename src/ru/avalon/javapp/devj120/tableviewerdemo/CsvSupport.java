package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvSupport {
    private static final char SEP = ',';
    
    public static String[][] readCsv(File file) throws IOException {
        List<String[]> res = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            int colCnt = -1;
            while( ( s = br.readLine() ) != null ) {
                String[] row = parseLine(s);
                if(colCnt == -1)
                    colCnt = row.length;
                else if(colCnt != row.length)
                    throw new FileFormatException("Rows contain different number of values.");
                res.add(row);
            }
        }
        return res.toArray(new String[0][]);
    }

    private static String[] parseLine(String s) {
        List<String> res = new ArrayList<>();
        int p = 0;
        while(p < s.length()) {
            int st = p;
            String v;
            if(s.charAt(p) == '"') {
                p++;
                p = s.indexOf('"', p);
                while(p < (s.length() - 1) && s.charAt(p + 1) == '"') {
                    p += 2;
                    p = s.indexOf('"', p);
                }
                v = s.substring(st + 1, p).replace("\"\"", "\"");
                p += 2;
            } else {
                p = s.indexOf(SEP, p);
                if(p == -1)
                    p = s.length();
                v = s.substring(st, p);
                if(v.length() == 0)
                    v = null;
                p++;
            }
            res.add(v);
        }
        return res.toArray(new String[0]);
    }

    public static void writeCsv(File target, String[] colHdrs, Object[][] data) throws IOException {
        try(FileWriter out = new FileWriter(target)) {
            for (int i = 0; i < colHdrs.length - 1; i++) {
                out.write(cellHandler(colHdrs[i]));
                out.write(",");
            }
            out.write(cellHandler(colHdrs[colHdrs.length - 1]));
            out.write("\n");

            for (int i = 0; i < data.length; i++) {
                Object[] row = data[i];
                for (int j = 0; j < row.length - 1; j++) {
                    out.write(cellHandler((String) row[j]));
                    out.write(",");
                }
                out.write(cellHandler((String) row[row.length - 1]));
                if (i < data.length - 1)
                out.write("\n");
            }
        }
    }
    private static String cellHandler(String s) {
        if (s == null)
            return "";
        if (s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            s = "\"" + s + "\"";
        }
        if (s.matches("^([0-9]*\\,[0-9]+)$"))
            s = "\"" + s + "\"";
        return s;
    }

    public static Class[] columnClassDetect(String[][] rowData) {
        Class[] columnClass = new Class[rowData[0].length];
        for (int i = 0; i < columnClass.length; i++)
            columnClass[i] = String.class;

        for (int i = 0; i < rowData.length; i++) {
            String[] row = rowData[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j] == null)
                    j++;
                if (row[j].matches("^[0-9]+$") && !columnClass[j].equals(BigDecimal.class) && !columnClass[j].equals(String.class) && i > 0 ||
                        row[j].matches("^[0-9]+$") && i == 0) {
                    columnClass[j] = Integer.class;
                }
                if (row[j].matches("^([0-9]*\\,[0-9]+)$") && i == 0 ||
                        row[j].matches("^([0-9]*\\,[0-9]+)$") && i > 0 && !columnClass[j].equals(String.class)) {
                    columnClass[j] = BigDecimal.class;
                }
                if (row[j].matches("^(false|true)$") && i == 0 ||
                        row[j].matches("^(false|true)$") && i > 0 && !columnClass[j].equals(String.class)) {
                    columnClass[j] = Boolean.class;
                }
                if (row[j].matches("^\\d{4}-\\d{2}-\\d{2}$") && i == 0 ||
                        row[j].matches("^\\d{4}-\\d{2}-\\d{2}$") && i > 0 && !columnClass[j].equals(String.class)) {
                    columnClass[j] = LocalDate.class;
                }
            }
        }
        return columnClass;
    }
}
