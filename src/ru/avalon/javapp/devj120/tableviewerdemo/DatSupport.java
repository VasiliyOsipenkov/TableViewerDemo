package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatSupport {

    public static String[][] readDat(File file) throws IOException {
        List<String[]> res = new ArrayList<>();
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            // (1) идентификатор формата читаем)
            char[] datFormat = {'T', 'B', 'L', '1'};
            char[] checkFormat = new char[4];
            for (int i = 0; i < 4; i++) {
                checkFormat[i] = (char) in.readByte();
            }
            if (!Arrays.equals(datFormat, checkFormat)) {
                throw new FileFormatException("Unknown file format");
            }
            // (2) количество колонок
            int columnCount = in.readInt();

            // (3) описание колонок. Перед каждой 'S' какой-то неизвестный бит, странно
            String[] line = new String[columnCount];
            int index = 0;
            for (int i = 0; i < in.available() && index < columnCount; i++) {
                if (in.readByte() == 'S') {
                    line[index] = in.readUTF();
                    index++;
                }
            }
            /*for (int i = 0; i < columnCount; i++) {
                if (in.readByte() == 'S') {
                    System.out.println(in.readUTF());
                    System.out.println(i);
                }
                switch (in.readByte()) {//Тип значения в колонке
                    case 'I' : {
                        break;
                    }
                    case 'N' : {
                        break;
                    }
                    case 'S' : {
                        break;
                    }
                    case 'D' : {
                        break;
                    }
                    case 'B' : {
                        break;
                    }
                    }
                }*/

            // (4) данные таблицы
            // (4.1) количество строк
            int linesCount = in.readInt();

            // (4.2)

            /*for (Object[] row : data) {
                for (Object v : row) {
                    out.writeByte(v != null ? '*' : '-');
                    if(v != null)
                        out.writeUTF((String)v);
                }
            }*/
        }
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
