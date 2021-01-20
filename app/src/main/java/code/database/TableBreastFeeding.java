package code.database;



public class TableBreastFeeding {

    public static final String tableName = "feeding";

    static final String createTable = ("CREATE TABLE  "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.feedTime + " JSON,"
            + tableColumn.method + " JSON,"
            + tableColumn.duration + " VARCHAR,"
            + tableColumn.quantity + " VARCHAR,"
            + tableColumn.fluid + " VARCHAR,"
            + tableColumn.date + " VARCHAR,"
            + tableColumn.specify + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        uuid,
        serverId,
        babyId,
        nurseId,
        feedTime,
        method,
        duration, // Its time for how long mother has feeded baby
        quantity,
        isDataSynced,
        syncTime,
        fluid,
        date,
        specify,
        addDate,
        modifyDate,
        json,
        status
    }
}
