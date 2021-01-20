package code.database;



public class TableDoctorRound {

    public static final String tableName = "doctorRound";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.doctorId + " VARCHAR,"
            + tableColumn.signature + " TEXT,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");


    public enum tableColumn {
        uuid,
        serverId,
        babyId,
        loungeId,
        doctorId,
        signature,
        isDataSynced,
        syncTime,
        addDate,
        modifyDate,
        json,
        status
    }
}
