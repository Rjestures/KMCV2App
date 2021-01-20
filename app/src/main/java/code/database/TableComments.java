package code.database;



public class TableComments {

    public static final String tableName = "comments";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.motherOrBabyId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.doctorId + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.comment + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        motherOrBabyId,
        serverId,
        uuid,
        doctorId,
        type,
        comment,
        isDataSynced,
        syncTime,
        addDate,
        modifyDate,
        json,
        status
    }
}
