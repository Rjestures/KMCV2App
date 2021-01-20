package code.database;



public class TableLoungeAssessment {

    public static final String tableName = "loungeAssessment";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.motherPermission + " VARCHAR,"
            + tableColumn.loungePicture + " VARCHAR,"
            + tableColumn.latitude + " VARCHAR,"
            + tableColumn.longitude + " VARCHAR,"
            + tableColumn.loungeTemperature + " VARCHAR,"
            + tableColumn.loungeThermometerCondition + " VARCHAR,"
            + tableColumn.loungeSafety + " VARCHAR,"
            + tableColumn.toiletCondition + " VARCHAR,"
            + tableColumn.washroomCondition + " VARCHAR,"
            + tableColumn.commonAreaCondition + " VARCHAR,"
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
        motherPermission,
        loungePicture,
        latitude,
        longitude,
        loungeTemperature,
        loungeThermometerCondition,
        loungeSafety,
        toiletCondition,
        washroomCondition,
        commonAreaCondition,
        addDate,
        modifyDate,
        isDataSynced,
        syncTime,
        json,
        status
    }
}
