package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatSupport {
    public static Class[] columnDatClass;

    public static String[][] readDat(File file) throws IOException {
        List<String[]> res = new ArrayList<>();
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {

            char[] datFormat = {'T', 'B', 'L', '1'};
            char[] checkFormat = new char[4];
            for (int i = 0; i < 4; i++) {
                checkFormat[i] = (char) in.readByte();
            }
            if (!Arrays.equals(datFormat, checkFormat)) {
                throw new FileFormatException("Unknown file format");
            }

            int columnCount = in.readInt();
            columnDatClass = new Class[columnCount];
            // (3) описание колонок. Перед каждой 'S' какой-то неизвестный бит, странно
            String[] columnNames = new String[columnCount];
            int index = 0;
            for (int i = 0; i < in.available() && index < columnCount; i++) {
                if (in.readByte() == 'S') {
                    columnNames[index] = in.readUTF();
                    index++;
                }
            }
            res.add(columnNames);
            // (4) данные таблицы
            // (4.1) количество строк
            int linesCount = in.readInt();

            // (4.2)

            for (int i = 0; i < linesCount; i++) {
                String[] line = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    char mark = (char) in.readByte();
                    switch (mark) {
                        case 'I' : {
                            columnDatClass[j] = Integer.class;
                            break;
                        }
                        case 'N' : {
                            columnDatClass[j] = BigDecimal.class;
                            break;
                        }
                        case 'S' : {
                            columnDatClass[j] = String.class;
                            break;
                        }
                        case 'D' : {
                            columnDatClass[j] = LocalDate.class;
                            break;
                        }
                        case 'B' : {
                            columnDatClass[j] = Boolean.class;
                            break;
                        }
                    }
                    char nullMark = (char) in.readByte();

                    if (nullMark == '*')
                        line[j] = in.readUTF();
                    if (nullMark == '-')
                        line[j] = null;
                }
                res.add(line);
            }
        }
        return res.toArray(new String[0][]);
    }
    public static void writeDat(File target, String[] colHdrs, Object[][] data, Class[] columnClass) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target))) {

            out.writeByte('T');
            out.writeByte('B');
            out.writeByte('L');
            out.writeByte('1');

            out.writeInt(colHdrs.length);

            for (String hdr : colHdrs) {
                out.writeChar('S'); // (3.1)
                out.writeUTF(hdr);  // (3.2)
            }

            out.writeInt(data.length);

            char[] colMark = new char[columnClass.length];
            for (int i = 0; i < columnClass.length; i++) {
                if (columnClass[i].equals(Integer.class))
                    colMark[i] = 'I';
                if (columnClass[i].equals(BigDecimal.class))
                    colMark[i] = 'N';
                if (columnClass[i].equals(String.class))
                    colMark[i] = 'S';
                if (columnClass[i].equals(LocalDate.class))
                    colMark[i] = 'D';
                if (columnClass[i].equals(Boolean.class))
                    colMark[i] = 'B';

            }

            for (int i = 0; i < data.length; i++) {
                Object[] row = data[i];
                for (int j = 0; j < row.length; j++) {
                    out.writeByte(colMark[j]);
                    out.writeByte(row[j] != null ? '*' : '-');
                    if (row[j] != null)
                        out.writeUTF((String) row[j]);
                }

            }
        }
    }
}
