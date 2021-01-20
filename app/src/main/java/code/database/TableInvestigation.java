package code.database;



public class TableInvestigation {

    public static final String tableName = "investigation";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.doctorId + " VARCHAR,"
            + tableColumn.doctorName + " VARCHAR,"
            + tableColumn.nurseComment + " VARCHAR,"
            + tableColumn.sampleComment + " VARCHAR,"
            + tableColumn.sampleImage + " TEXT,"
            + tableColumn.sampleTakenBy + " VARCHAR,"
            + tableColumn.sampleTakenOn + " VARCHAR,"
            + tableColumn.resultImage + " TEXT,"
            + tableColumn.result + " TEXT,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.investigationType + " VARCHAR,"
            + tableColumn.investigationName + " VARCHAR,"
            + tableColumn.doctorComment + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.invesDate + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        babyId,
        serverId,
        uuid,
        nurseComment,
        resultImage,
        result,
        nurseId,
        doctorId,
        doctorName,
        investigationType,
        investigationName,
        isDataSynced,
        sampleComment,
        sampleImage,
        sampleTakenOn,
        doctorComment,
        sampleTakenBy,
        syncTime,
        addDate,
        modifyDate,
        invesDate,
        json,
        status
    }
}
