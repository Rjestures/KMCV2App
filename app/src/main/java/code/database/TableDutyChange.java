package code.database;



public class TableDutyChange {

    public static final String tableName = "dutyChange";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.selfieCheckIn + " TEXT,"
            + tableColumn.selfieCheckOut + " TEXT,"
            + tableColumn.latitude + " VARCHAR,"
            + tableColumn.longitude + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.isDataSyncedCheckOut + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.syncTimeCheckOut + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        loungeId,
        nurseId,
        json,
        selfieCheckIn,
        selfieCheckOut,
        latitude,
        longitude,
        type, //1 - checkin, 2 check Out
        isDataSynced,
        isDataSyncedCheckOut,
        syncTime,
        syncTimeCheckOut,
        addDate,
        modifyDate,
        status
    }
}
