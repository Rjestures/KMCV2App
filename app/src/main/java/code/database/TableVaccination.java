package code.database;



public class TableVaccination {

    public static final String tableName = "vaccination";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.vaccName + " VARCHAR,"
            + tableColumn.quantity + " VARCHAR,"
            + tableColumn.date + " VARCHAR,"
            + tableColumn.time + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        babyId,
        serverId,
        uuid,
        vaccName,
        quantity,
        date,
        time,
        isDataSynced,
        nurseId,
        syncTime,
        addDate,
        modifyDate,
        status
    }
}
