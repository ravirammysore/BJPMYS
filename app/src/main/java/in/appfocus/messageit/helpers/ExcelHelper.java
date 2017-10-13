package in.appfocus.messageit.helpers;

/**
 * Created by User on 29-09-2017.
 */

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ExcelHelper {
    

    private static RealmList<Contact> lstContactsToImportFromExcel;
    

    public static RealmList<Contact> importContactsListFromExcel(Context context, Realm realm, String groupId, Uri excelUri) {

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
            //original approach
            //File file = new File(excelFilePath);

            //other approach
            //File file = new File(excelUri.getPath());

            //second appr
            //File file = new File(new URI(excelUri.toString()));

            //third appr  - worked only in some cases
            //File file = new File(getPath(excelUri,context));

            //fourth appr (as recommended by android developer docs i believe)
            //works well in all cases!
            //suggested in stack-overflow:
            //https://stackoverflow.com/a/38568666

            //see the topic "Get an InputStream" from the link below
            // https://developer.android.com/guide/topics/providers/document-provider.html
            InputStream inputStream = context.getContentResolver().openInputStream(excelUri);

            //was in original approach ( C O M M E N T E D  as part of fourth approach!)
            //FileInputStream inputStream = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                Contact contact = getContactFromExcelRow(rowIter);
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

    public static boolean exportContactsInGroupToExcel(Context context, Realm realm, String groupId) {

        boolean success = false;

        String appFolderName = "appfocus";

        //New Workbook
        Workbook workbook = putContactsToExcelWorkbook(context,realm,groupId);

        //Cell style for header row
        //CellStyle cs = wb.createCellStyle();
        //cs.setFillForegroundColor(HSSFColor.LIME.index);
        //cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet

        File directoryForExport = new
                File(Environment.getExternalStorageDirectory()+File.separator+appFolderName);
        if(!directoryForExport.exists())
            directoryForExport.mkdir();

        Group group = realm.where(Group.class).equalTo("id",groupId).findFirst();
        String excelFileName = group.getName()+".xls";

        File excelFileToExport = new File(directoryForExport, excelFileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(excelFileToExport);
            workbook.write(outputStream);
            success = true;

        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        } finally {
            try {
                if (null != outputStream)
                    outputStream.close();
            }
            catch (Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return success;
    }

    //// TODO: 13-10-2017 working fine, need to create a menu for it and call the function 
    public static Boolean exportAllGroupsToExcelFiles(Context context,Realm realm){
        Boolean success = false;
        RealmResults<Group> lstGroups = realm.where(Group.class).findAll();

        try{
            for(Group group:lstGroups)
                exportContactsInGroupToExcel(context,realm,group.getId());
            success = true;
        }catch (Exception ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return success;
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
    

    private static Contact getContactFromExcelRow(Iterator<Row> rowIter){
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

    private static Workbook putContactsToExcelWorkbook(Context context,Realm realm,String groupId){

        Group group = realm.where(Group.class).equalTo("id",groupId).findFirst();
        RealmList<Contact> lstContactsToExport = group.getContacts();

        Workbook workbook = new HSSFWorkbook();

        Sheet sheet1 = null;
        sheet1 = workbook.createSheet("sheet1");

        // Generate header row and set column width
        Row row = sheet1.createRow(0);
        Cell c = null;
        c = row.createCell(0);
        c.setCellValue("Name");
        c = row.createCell(1);
        c.setCellValue("Mobile");
        sheet1.setColumnWidth(0, (12 * 500));
        sheet1.setColumnWidth(1, (12 * 500));

        //put other contacts into different rows, starting from second row (rowIndex=1)of excel sheet
        int rowIndex=1;
        for(Contact contact:lstContactsToExport){
            row = sheet1.createRow(rowIndex);
            c = row.createCell(0);
            c.setCellValue(contact.getName());
            c = row.createCell(1);
            c.setCellValue(contact.getMobileNo());
            rowIndex++;
        }

        return workbook;
    }
    
}
