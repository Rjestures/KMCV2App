package code.database;

public class TableLoungeServices {

    public static final String tableName = "loungeServices";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.latitude + " VARCHAR,"
            + tableColumn.longitude + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.value + " VARCHAR,"
            + tableColumn.slot + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        serverId,
        uuid,
        nurseId,
        type,
        value,
        latitude,
        longitude,
        slot,
        addDate,
        modifyDate,
        isDataSynced,
        syncTime,
        json,
        status
    }
}
