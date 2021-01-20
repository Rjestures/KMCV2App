package code.database;



public class TableNotification {

    public static final String tableName = "notification";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.motherId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.message + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.shiftingType + " VARCHAR,"
            + tableColumn.value + " VARCHAR,"
            + tableColumn.syncedDate + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.date + " VARCHAR,"
            + tableColumn.time + " VARCHAR,"
            + tableColumn.assessmentNumber + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");


    public enum tableColumn {
        loungeId,
        babyId,
        motherId,
        serverId,
        uuid,
        message,
        type,
        shiftingType,
        value,
        addDate,
        modifyDate,
        syncedDate,
        date,
        time,
        assessmentNumber,
        status // 1 pending // 2 done //3 undone
    }
}
