package code.database;



public class TableMotherPastInformation {

    public static final String tableName = "motherPastInformation";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.babyAdmissionId + " VARCHAR,"
            + tableColumn.motherAdmissionId + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        loungeId,
        json,
        babyAdmissionId,
        motherAdmissionId,
        status
    }
}
