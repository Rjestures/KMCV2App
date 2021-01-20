package code.database;



public class TableTreatment {

    public static final String tableName = "treatment";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.babyId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.treatmentName + " VARCHAR,"
            + tableColumn.comment + " VARCHAR,"
            + tableColumn.quantity + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.doctorId + " VARCHAR,"
            + tableColumn.doctorName + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.image + " TEXT,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.givenDate + " VARCHAR,"
            + tableColumn.givenTime + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.unit + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        babyId,
        serverId,
        uuid,
        treatmentName,
        comment,
        quantity,
        isDataSynced,
        syncTime,
        nurseId,
        doctorId,
        doctorName,
        image,
        addDate,
        givenDate,
        givenTime,
        modifyDate,
        unit,
        type,
        status
    }
}
