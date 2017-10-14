import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class PdfDriver {

    private static final String[] titles = new String[]{"2017 NASCAR on FOX: NASCAR Camping World Truck Series - Pre-Race (Talladega Superspeedway) (LIVE) - FOX",
            "People's Court : 21030", "Divorce Court", "King of Queens", "Local News", "Maury", "Two Broke Girls", "College Football", "Paid Programming", "Paid Programming", "Jerry Springer"};
    private static final String[] colors = new String[]{"#16E046", "#FF9933", "#FFFF99", "#996633", "#FF99FF", "#FFFFFF", "#BFBFBF"};
    private static final String[] weekdays = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE MM/dd/YYYY");

    public static void main(String[] args) throws DocumentException, FileNotFoundException {
        Document document = new Document(PageSize.A3, 0, 0, 0, 0);
        PdfWriter.getInstance(document, new FileOutputStream(new Date().getTime() + ".pdf"));
        document.open();
        document.add(createTable(true));
        document.close();
    }

    private static PdfPTable createTable(Boolean withColor) throws FileNotFoundException, DocumentException {

        float fntSize = 6.7f;
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, fntSize);

        //populate a hashmap, where the key is a string representing each day of the week & the value is the current row idx
        HashMap<String, Integer> dayPartMap = new HashMap<String, Integer>();
        for (String weekday : weekdays) {
            dayPartMap.put(weekday, 0);
        }

        //set calendar object to monday at station's on-air time
        Calendar calendar = getStartOfWeek();

        PdfPTable table = new PdfPTable(9);
        for (int daypart = 0; daypart < 25; daypart++) {
            for (int weekdayPart = 0; weekdayPart < 9; weekdayPart++) {
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                //if first day part row, then set weekday names
                if (daypart == 0) {
                    cell.setColspan(1);
                    cell.setRowspan(1);
                    cell.setBorder(Rectangle.BOX);
                    cell.setBackgroundColor(fromHex("#D3D3D3"));

                    //if a program content column, then set the header column value (weekday name + date)
                    if (weekdayPart > 0 && weekdayPart < 8) {
                        cell.setPhrase(new Phrase(dateFormat.format(calendar.getTime()), font));
                    }
                    table.addCell(cell);

                    //increment day of week of not first or last column
                    if (weekdayPart > 0 && weekdayPart < 9) {
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                    }
                } else {
                    //otherwise, set program content
                    cell.setColspan(1);


                    //if first or last column of program content, print daypart time slots
                    if (weekdayPart == 0 || weekdayPart == 8) {
                        cell.setBorder(Rectangle.BOX);
                        cell.setBackgroundColor(fromHex("#D3D3D3"));
                        cell.setPhrase(new Phrase(timeFormat.format(calendar.getTime()), font));
                        cell.setRowspan(1);
                        cell.setPaddingTop(8);
                        cell.setPaddingBottom(8);
                        table.addCell(cell);

                    } else if (dayPartMap.get(weekdays[weekdayPart - 1]) <= (daypart - 1)) {
                        //otherwise, print program details

                        //give a semi-random span to simulate programs with lengths longer than 30 minutes
                        int span = 1;
                        if (daypart == 10) {
                            span = 3;
                        } else if (weekdayPart == 2 && daypart == 20) {
                            span = Math.min(8, 25 - daypart);
                        }

                        //update the dayPartMap, with the current row index it has reached. it will be skipped on the
                        //next iteration if the cell is already filled. iText PDF will automatically populate the next
                        //cell in the correct slot, so we only need to be sure it is skipped if necessary
                        dayPartMap.put(weekdays[weekdayPart - 1], dayPartMap.get(weekdays[weekdayPart - 1]) + span);
                        cell.setBorder(Rectangle.BOX);
                        cell.setRowspan(span);
                        cell.setPhrase(new Phrase(titles[new Random().nextInt(titles.length - 1)], font));
                        if (withColor) {
                            cell.setBackgroundColor(fromHex(colors[weekdayPart % (colors.length - 2)]));
                        }
                        table.addCell(cell);
                    }
                }
            }

            if (daypart > 0) {
                calendar.add(Calendar.DAY_OF_WEEK, -7);
                calendar.add(Calendar.MINUTE, 30);
            }
        }

        return table;
    }

    /**
     * given a station, return a calendar object set to Monday at the station's on-air time
     *
     * @return
     */
    private static Calendar getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR, 5);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
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