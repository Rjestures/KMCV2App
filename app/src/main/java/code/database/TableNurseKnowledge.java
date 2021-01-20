package code.database;



public class TableNurseKnowledge {

    public static final String tableName = "nurseKnowledge";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.doctorId + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.confirmationStatus + " VARCHAR,"
            + tableColumn.actionTaken + " VARCHAR,"
            + tableColumn.message + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");


    public enum tableColumn {
        uuid,
        serverId,
        nurseId,
        loungeId,
        doctorId,
        type,
        confirmationStatus,
        actionTaken,
        message,
        isDataSynced,
        syncTime,
        addDate,
        modifyDate,
        json,
        status
    }
}
