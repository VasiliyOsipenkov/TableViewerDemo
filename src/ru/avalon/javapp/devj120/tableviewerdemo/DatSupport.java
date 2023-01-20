package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatSupport {

    public static String[][] readDat(File file) {
        List<String[]> res = new ArrayList<>();
        /*try(BufferedReader br = new BufferedReader(new FileReader(file))) {
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
        }*/
        return res.toArray(new String[0][]);
    }
    public static void writeDat(File target, String[] colHdrs, Object[][] data) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target))) {
            // (1) идентификатор формата
            out.writeByte('T');
            out.writeByte('B');
            out.writeByte('L');
            out.writeByte('1');
            
            // (2) количество колонок
            out.writeInt(colHdrs.length);
            
            // (3) описание колонок
            for (String hdr : colHdrs) {
                out.writeChar('S'); // (3.1)
                out.writeUTF(hdr);  // (3.2)
            }
            
            // (4) данные таблицы
            // (4.1) количество строк
            out.writeInt(data.length);
            // (4.2)
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
