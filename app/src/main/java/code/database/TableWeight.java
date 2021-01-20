package code.database;



public class TableWeight {

    public static final String tableName = "weight";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.babyAdmissionId + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.babyWeight + " VARCHAR,"
            + tableColumn.weightImage + " TEXT,"
            + tableColumn.isWeighingAvail + " VARCHAR,"
            + tableColumn.weighingReason + " VARCHAR,"
            + tableColumn.weightDate + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        babyId,
        loungeId,
        babyAdmissionId,
        nurseId,
        babyWeight,
        weightImage,
        isWeighingAvail,
        weighingReason,
        weightDate,
        addDate,
        isDataSynced,
        syncTime
    }
}
