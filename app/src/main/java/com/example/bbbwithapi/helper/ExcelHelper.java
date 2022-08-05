package com.example.bbbwithapi.helper;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.example.bbbwithapi.model.UserAccount;
import com.example.bbbwithapi.model.Donation;
import com.example.bbbwithapi.model.Report;

public class ExcelHelper {
    public boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    public boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    public boolean isExternalStoragePublicDirectoryAvailable() {
        int sdk_id = Build.VERSION.SDK_INT;
        if (sdk_id >= 19) {
            String directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            String directory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            File folder1 = new File(directory1);
            File folder2 = new File(directory2);

            if (!folder1.exists() && !folder2.exists()) {
                return false;
            } else {
                return true;
            }
        }
        else {
            String directory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            File folder2 = new File(directory2);

            if (!folder2.exists()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean storeExcelInStorage(Context context, Workbook workbook, String fileName) {
        boolean isSuccess = true;
        String directory = "";

        String mycode = "XLS_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        fileName = mycode + "_" + fileName;

        int sdk_id = Build.VERSION.SDK_INT;
        if (sdk_id >= 19) {
            File mainFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
            if (mainFolder.exists()) {
                directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Yayasan/";
                File folder = new File(directory);
                if (!folder.exists()) {
                    isSuccess = folder.mkdir();
                }
            } else {
                directory = Environment.getExternalStorageDirectory() + "/Yayasan/";
                File folder = new File(directory);
                if (!folder.exists()) {
                    isSuccess = folder.mkdir();
                }
            }
        }
        else {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Yayasan/";
            File folder = new File(directory);
            if (!folder.exists()) {
                isSuccess = folder.mkdir();
            }
        }

        if (isSuccess) {
            File file = new File(directory, fileName);
            if (file.exists()){
                isSuccess = file.delete();
            }

            if (isSuccess) {
                FileOutputStream fileOutputStream = null;

                try {
                    fileOutputStream = new FileOutputStream(file);
                    workbook.write(fileOutputStream);

                    Toast.makeText(context, "Writing file: " + file, Toast.LENGTH_SHORT).show();
                    isSuccess = true;
                } catch (IOException e) {
                    Toast.makeText(context, "Error writing Exception: " + e, Toast.LENGTH_SHORT).show();
                    isSuccess = false;
                } catch (Exception e) {
                    Toast.makeText(context, "Failed to save file due to Exception: " + e, Toast.LENGTH_SHORT).show();
                    isSuccess = false;
                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(context, "Failed to close file because Exception: " + ex, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(context, "Failed to delete file: " + file, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Storage not available or read only: " + directory + ". Android: " + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
        }

        return isSuccess;
    }

    public void generateExcelUser(Context context, List<UserAccount> list_user) {
        if (!isExternalStoragePublicDirectoryAvailable()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
        }
        else {
            Workbook workbook = new HSSFWorkbook();

            // Create a new sheet in a Workbook and assign a name to it
            String EXCEL_SHEET_NAME = "user";
            final Sheet sheet = workbook.createSheet(EXCEL_SHEET_NAME);

            CreationHelper helper = workbook.getCreationHelper();

            Cell cellHeader = null;

            // Cell style for a cell
            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setFillForegroundColor(HSSFColor.AQUA.index);
            cellStyleHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyleHeader.setAlignment(CellStyle.ALIGN_CENTER);

            //Header
            Row rowHeader = sheet.createRow(0);

            cellHeader = rowHeader.createCell(0);
            cellHeader.setCellValue("User ID");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(1);
            cellHeader.setCellValue("User Name");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(2);
            cellHeader.setCellValue("Name");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(3);
            cellHeader.setCellValue("Email");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(4);
            cellHeader.setCellValue("Mobile Phone");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(5);
            cellHeader.setCellValue("Sex");
            cellHeader.setCellStyle(cellStyleHeader);

            //Body
            for (int i = 0; i < list_user.size(); i++) {
                Row rowBody = sheet.createRow(i + 1);

                Cell cellBody = null;

                cellBody = rowBody.createCell(0);
                cellBody.setCellValue(list_user.get(i).getUserID());

                cellBody = rowBody.createCell(1);
                cellBody.setCellValue(list_user.get(i).getUserName());

                cellBody = rowBody.createCell(2);
                cellBody.setCellValue(list_user.get(i).getName());

                cellBody = rowBody.createCell(3);
                cellBody.setCellValue(list_user.get(i).getEmail());

                cellBody = rowBody.createCell(4);
                cellBody.setCellValue(list_user.get(i).getMobilePhone());

                cellBody = rowBody.createCell(5);
                cellBody.setCellValue(list_user.get(i).getSex());
            }

            String fileName = "user_List.xls";
            boolean isWorkbookWrittenIntoStorage = storeExcelInStorage(context, workbook, fileName);
        }
    }

    public void generateExcelDonation(Context context, List<Donation> list_donation) {
        if (!isExternalStoragePublicDirectoryAvailable()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
        }
        else {
            Workbook workbook = new HSSFWorkbook();

            // Create a new sheet in a Workbook and assign a name to it
            String EXCEL_SHEET_NAME = "donation";
            final Sheet sheet = workbook.createSheet(EXCEL_SHEET_NAME);

            CreationHelper helper = workbook.getCreationHelper();

            Cell cellHeader = null;

            // Cell style for a cell
            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setFillForegroundColor(HSSFColor.AQUA.index);
            cellStyleHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyleHeader.setAlignment(CellStyle.ALIGN_CENTER);

            //Header
            Row rowHeader = sheet.createRow(0);

            cellHeader = rowHeader.createCell(0);
            cellHeader.setCellValue("Prayer");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(1);
            cellHeader.setCellValue("Nominal");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(2);
            cellHeader.setCellValue("Reference Number");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(3);
            cellHeader.setCellValue("Payment Status");
            cellHeader.setCellStyle(cellStyleHeader);

            //Body
            for (int i = 0; i < list_donation.size(); i++) {
                Row rowBody = sheet.createRow(i + 1);

                Cell cellBody = null;

                cellBody = rowBody.createCell(0);
                cellBody.setCellValue(list_donation.get(i).getPrayer());

                cellBody = rowBody.createCell(1);
                cellBody.setCellValue(list_donation.get(i).getNominal());

                cellBody = rowBody.createCell(2);
                cellBody.setCellValue(list_donation.get(i).getReferenceNumber());

                cellBody = rowBody.createCell(3);
                cellBody.setCellValue(list_donation.get(i).getStatusPayment());
            }

            String fileName = "donation_List.xls";
            boolean isWorkbookWrittenIntoStorage = storeExcelInStorage(context, workbook, fileName);
        }
    }

    public void generateExcelLaporan(Context context, List<Report> list_laporan) {
        if (!isExternalStoragePublicDirectoryAvailable()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
        }
        else {
            Workbook workbook = new HSSFWorkbook();

            // Create a new sheet in a Workbook and assign a name to it
            String EXCEL_SHEET_NAME = "laporan";
            final Sheet sheet = workbook.createSheet(EXCEL_SHEET_NAME);

            CreationHelper helper = workbook.getCreationHelper();

            Cell cellHeader = null;

            // Cell style for a cell
            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setFillForegroundColor(HSSFColor.AQUA.index);
            cellStyleHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyleHeader.setAlignment(CellStyle.ALIGN_CENTER);

            //Header
            Row rowHeader = sheet.createRow(0);

            cellHeader = rowHeader.createCell(0);
            cellHeader.setCellValue("Tahun Transaksi");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(1);
            cellHeader.setCellValue("Bulan Transaksi");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(2);
            cellHeader.setCellValue("Jumlah Transaksi");
            cellHeader.setCellStyle(cellStyleHeader);

            //Body
            for (int i = 0; i < list_laporan.size(); i++) {
                Row rowBody = sheet.createRow(i + 1);

                Cell cellBody = null;

                cellBody = rowBody.createCell(0);
                cellBody.setCellValue(list_laporan.get(i).getTahunTransaksi());

                cellBody = rowBody.createCell(1);
                cellBody.setCellValue(list_laporan.get(i).getBulanTransaksi());

                cellBody = rowBody.createCell(2);
                cellBody.setCellValue(list_laporan.get(i).getJumlahTransaksi());
            }

            String fileName = "laporan_List.xls";
            boolean isWorkbookWrittenIntoStorage = storeExcelInStorage(context, workbook, fileName);
        }
    }
}
