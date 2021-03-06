package in.appfocus.messageit.helpers;

/**
 * Created by User on 29-09-2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
import io.realm.RealmList;

public class ExcelHelper {
    
    //Create a path where we will save our excel files
    private static File EXCEL_EXPORT_PATH = Environment.getExternalStorageDirectory();
    
    private static RealmList<Contact> lstContactsToImportFromExcel;

    public static RealmList<Contact> extractContactsInExcel(Context context, Realm realm, String groupId, String excelFilePath) {

                                    // I M P O R T A N T

        /*JAR DOWNLOADED FROM:
        https://www.apache.org/dyn/closer.lua/poi/release/bin/poi-bin-3.17-20170915.tar.gz

        USAGE EXAMPLE FROM:
        source code inspired from http://www.mysamplecode.com/2011/10/android-read-write-excel-file-using.html*/

        lstContactsToImportFromExcel = new RealmList<>();

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return lstContactsToImportFromExcel;
        }

        try{

            // this line was in downloaded code
            //File file = new File(EXCEL_PATH, filename);

            //din work
            //File file = new File(uri.getPath());

            //din work
            //File file =new File(new URI(uri.toString()));

            //works perfect!
            File file = new File(excelFilePath);

            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                Contact contact = extractContactFromRow(rowIter);
                if(contact!=null)
                    if(!Utilities.isContactPresentInGroup(context,realm,contact,groupId))
                        lstContactsToImportFromExcel.add(contact);
            }
        }
        catch (OfficeXmlFileException ex){
            Toast.makeText(context, "Check input file and format!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return lstContactsToImportFromExcel;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static Contact extractContactFromRow(Iterator<Row> rowIter){
        Contact contact = null;

        HSSFRow myRow = (HSSFRow) rowIter.next();

        //don't process row zero(header)
        if(myRow.getRowNum()==0) return null;

        Iterator<Cell> cellIter = myRow.cellIterator();

        contact = new Contact();
        while(cellIter.hasNext()){

            HSSFCell myCell = (HSSFCell) cellIter.next();

            //name column
            if(myCell.getColumnIndex()==0)
                if(!Utilities.isStringNullOrEmpty(myCell.toString()))
                    contact.setName(myCell.toString());

            //phone number column
            if(myCell.getColumnIndex()==1){
                String phNumber;
                phNumber = extractPhoneNumber(myCell.toString());
                contact.setMobileNo(phNumber);
            }

            //similarly process other columns
        }

        return contact;
    }

    private static String extractPhoneNumber(String phNumber){
        String result = "invalid";

        try {
            if(!Utilities.isStringNullOrEmpty(phNumber)){
                //if the number is represented in exponential form,
                // remove decimal and retain only the sequence before E
                phNumber = phNumber.replace(".","");
                if(phNumber.contains("E")){
                    int indexOfE = phNumber.indexOf("E");
                    phNumber = phNumber.substring(0,indexOfE);
                }
                phNumber = phNumber.replaceAll("\\s", "");
                phNumber = phNumber.replace("-", "");

                result = phNumber;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //// TODO: 06-10-2017 export feature 
    public static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        //CellStyle cs = wb.createCellStyle();
        //cs.setFillForegroundColor(HSSFColor.LIME.index);
        //cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Item Number");
        //c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Quantity");
        //c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Price");
        //c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        
        //File file = new File(context.getExternalFilesDir(null), fileName);
        File file = new File(EXCEL_EXPORT_PATH, fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
                Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
            } 
            catch (Exception ex) {
            }
        }

        return success;
    }

}
