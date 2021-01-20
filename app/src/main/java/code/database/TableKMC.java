package code.database;



public class TableKMC {

    public static final String tableName = "kmc";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.startDate + " VARCHAR,"
            + tableColumn.endTime + " VARCHAR,"
            + tableColumn.startTime + " VARCHAR,"
            + tableColumn.endDate + " VARCHAR,"
            + tableColumn.provider + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        babyId,
        nurseId,
        serverId,
        uuid,
        startDate,
        endTime,
        startTime,
        provider,
        endDate,
        isDataSynced,
        syncTime,
        addDate,
        modifyDate,
        status
    }
}
