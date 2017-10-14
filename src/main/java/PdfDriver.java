import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PdfDriver {

    private static final String[] colors = new String[]{"#16E046", "#BFBFBF", "#FF9933", "#FFFF99", "#996633", "#FF99FF", "#FFFFFF"};
    private static final String[] weekdays = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public static void main(String[] args) throws DocumentException, FileNotFoundException {
        createTable();

        /*

        Document document = new Document(PageSize.A3, 0, 0, 60, 0);
        PdfWriter.getInstance(document, new FileOutputStream(new Date().getTime() + ".pdf"));
        document.open();
        PdfPTable table = new PdfPTable(3);
// the cell object
        PdfPCell cell;
// we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Cell with colspan 3"));
        cell.setColspan(3);
        table.addCell(cell);
// now we add a cell with rowspan 2
        cell = new PdfPCell(new Phrase("Cell with rowspan 3"));
        cell.setRowspan(3);
        table.addCell(cell);
// we add the four remaining cells with addCell()
        table.addCell("row 1; cell 1");
        PdfPCell cellRowSpan2 = new PdfPCell(new Phrase("cell with rowspan 2"));
        cellRowSpan2.setRowspan(2);

        table.addCell(cellRowSpan2);
        table.addCell("row 2; cell 1");
        table.addCell("row 3; cell 1");
        table.addCell("row 3; cell 2");
        document.add(table);
        document.close();

        */
    }

    private static void createTable() throws FileNotFoundException, DocumentException {

        Document document = new Document(PageSize.A3, 0, 0, 60, 0);
        PdfWriter.getInstance(document, new FileOutputStream(new Date().getTime() + ".pdf"));
        document.open();

        HashMap<String, Integer> dayPartMap = new HashMap<String, Integer>();
        for (String weekday : weekdays) {
            dayPartMap.put(weekday, 0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 5);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        PdfPTable table = new PdfPTable(9);
        for (int daypart = 0; daypart < 25; daypart++) {
            for (int weekdayPart = 0; weekdayPart < 9; weekdayPart++) {
                PdfPCell cell = new PdfPCell();

                //if first day part row, then set weekday names
                if (daypart == 0) {
                    cell.setColspan(1);
                    cell.setRowspan(1);
                    cell.setBorder(Rectangle.BOX);
                    cell.setBackgroundColor(fromHex(colors[1]));
                    if (weekdayPart > 0 && weekdayPart < 8) {
                        cell.setPhrase(new Phrase(weekdays[weekdayPart - 1]));
                    }
                    table.addCell(cell);
                } else {
                    //otherwise, set program content
                    cell.setColspan(1);


                    //if first or last column of program content, print daypart time slots
                    if (weekdayPart == 0 || weekdayPart == 8) {
                        cell.setBorder(Rectangle.BOX);
                        cell.setBackgroundColor(fromHex(colors[1]));
                        String time = formatZeroes(calendar.get(Calendar.HOUR)) + ":" + formatZeroes(calendar.get(Calendar.MINUTE));
                        cell.setPhrase(new Phrase(time));
                        cell.setRowspan(1);
                        table.addCell(cell);

                    } else if (dayPartMap.get(weekdays[weekdayPart - 1]) <= (daypart - 1)) {
                        //otherwise, print program details

                        //give a semi-random span to simulate programs with lengths longer than 30 minutes
                        int span = daypart == 10 ? 3 : 1;
                        dayPartMap.put(weekdays[weekdayPart - 1], dayPartMap.get(weekdays[weekdayPart - 1]) + span);

                        cell.setBorder(Rectangle.BOX);
                        cell.setRowspan(span);
                        cell.setPhrase(new Phrase("title"));
                        cell.setBackgroundColor(fromHex(colors[weekdayPart % (colors.length - 1)]));
                        table.addCell(cell);
                    }
                }
            }

            if (daypart > 0) {
                calendar.add(Calendar.MINUTE, 30);
            }
        }
        document.add(table);
        document.close();
    }

    /**
     * given a number, add a leading zero if less than ten
     *
     * @param val
     * @return
     */
    private static String formatZeroes(int val) {
        return val < 10 ? "0" + val : String.valueOf(val);
    }

    /**
     * convert a hex string to an itext BaseColor
     *
     * @param hex
     * @return
     */
    private static BaseColor fromHex(String hex) {
        return new BaseColor(Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16));
    }

}