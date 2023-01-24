package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.*;
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
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target))) {

            for (Object[] row : data) {
                for (Object v : row) {
                    out.writeByte(v != null ? '*' : '-');
                    if(v != null)
                        out.writeUTF((String)v);
                }
            }
        }
    }
}
