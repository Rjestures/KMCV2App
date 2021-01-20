package code.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.kmcapp.android.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {
    DataBaseHelper(Context context) {
        super(context, context.getString(R.string.databaseName), null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableLounge.createTable);
        db.execSQL(TableDistrict.createTable);
        db.execSQL(TableBlock.createTable);
        db.execSQL(TableVillage.createTable);
        db.execSQL(TableStaff.createTable);
        db.execSQL(TableVideos.createTable);
        db.execSQL(TableMotherRegistration.createTable);
        db.execSQL(TableMotherAdmission.createTable);
        db.execSQL(TableMotherMonitoring.createTable);
        db.execSQL(TableBabyRegistration.createTable);
        db.execSQL(TableBabyAdmission.createTable);
        db.execSQL(TableBabyMonitoring.createTable);
        db.execSQL(TableWeight.createTable);
        db.execSQL(TableBreastFeeding.createTable);
        db.execSQL(TableKMC.createTable);
        db.execSQL(TableVaccination.createTable);
        db.execSQL(TableTreatment.createTable);
        db.execSQL(TableAsha.createTable);
        db.execSQL(TableFacility.createTable);
        db.execSQL(TableComments.createTable);
        db.execSQL(TableDoctorRound.createTable);
        db.execSQL(TableDutyChange.createTable);
        db.execSQL(TableNotification.createTable);
        db.execSQL(TableInvestigation.createTable);
        db.execSQL(TableNurseKnowledge.createTable);
        db.execSQL(TableMotherPastInformation.createTable);
        db.execSQL(TableLoungeAssessment.createTable);
        db.execSQL(TableStuck.createTable);
        db.execSQL(TableState.createTable);
        db.execSQL(TableCountry.createTable);
        db.execSQL(TableBirthReview.createTable);
        db.execSQL(TableLoungeServices.createTable);



        db.execSQL(TableUser.createTable);
        db.execSQL(TableRevenue.createTable);
        db.execSQL(TablePosters.createTable);
        db.execSQL(TableCounsellingPosters.createTable);

        //db.execSQL(TableNotification.createTable);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TableLounge.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableDistrict.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableBlock.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableVillage.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableVideos.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableMotherRegistration.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableMotherAdmission.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableMotherMonitoring.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableBabyRegistration.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableBabyAdmission.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableBabyMonitoring.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableWeight.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableKMC.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableStaff.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableVaccination.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableTreatment.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableAsha.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableFacility.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableComments.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableDoctorRound.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableDutyChange.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableNotification.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableInvestigation.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableNurseKnowledge.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableMotherPastInformation.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableLoungeAssessment.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableStuck.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableState.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableCountry.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableBirthReview.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableLoungeServices.tableName);

            db.execSQL("DROP TABLE IF EXISTS " + TableUser.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableRevenue.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableBreastFeeding.tableName);
            db.execSQL("DROP TABLE IF EXISTS " + TablePosters.tableName);



            //db.execSQL("DROP TABLE IF EXISTS " + TableNotification.tableName);

            onCreate(db);
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    // Copy to sdcard for debug use
    public static void copyDatabase(Context c) {
        String databasePath = c.getDatabasePath(c.getString(R.string.databaseName)).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
        Log.d("testing", " testing db path " + databasePath);
        Log.d("testing", " testing db exist " + f.exists());

        try {

            // External sdcard location
            File directory = new File(Environment.getExternalStorageDirectory(),c.getString(R.string.app_name));

            // Create the storage directory if it does not exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            myOutput = new FileOutputStream(directory.getAbsolutePath() + "/" + c.getString(R.string.databaseName)+".sqlite");
            myInput = new FileInputStream(databasePath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
        } catch (Exception e) {
        } finally {
            try {
                if (myOutput != null) {
                    myOutput.close();
                    myOutput = null;
                }
                if (myInput != null) {
                    myInput.close();
                    myInput = null;
                }
            } catch (Exception e) {
            }
        }
    }
}
