package code.database;



public class TableUser {

    public static final String tableName = "user";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.motherId + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.babyAdmissionId + " VARCHAR,"
            + tableColumn.motherAdmissionId + " VARCHAR,"
            + tableColumn.step1 + " JSON,"
            + tableColumn.step2 + " JSON,"
            + tableColumn.step3 + " JSON,"
            + tableColumn.step4 + " JSON,"
            + tableColumn.step5 + " JSON,"
            + tableColumn.step6 + " JSON,"
            + tableColumn.step7 + " JSON,"
            + tableColumn.mRStatus + " VARCHAR,"
            + tableColumn.bRStatus + " VARCHAR,"
            + tableColumn.bAStatus + " VARCHAR,"
            + tableColumn.mAStatus + " VARCHAR,"
            + tableColumn.dIStatus + " JSON,"
            + tableColumn.cStatus + " VARCHAR,"
            + tableColumn.fromStep + " VARCHAR,"
            + tableColumn.isSibling + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        loungeId,
        motherId,
        babyId,
        babyAdmissionId,
        motherAdmissionId,
        step1,
        step4,
        step2,
        step3,
        step5,
        step6,
        step7,
        mRStatus,
        bRStatus,
        bAStatus,
        mAStatus,
        dIStatus,
        cStatus,
        fromStep,
        isSibling,
        addDate,
        modifyDate,
        status
    }
}
