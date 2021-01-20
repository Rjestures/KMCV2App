package code.database;



public class TableMotherMonitoring {

    public static final String tableName = "motherMonitoring";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR," // App Unique Id
            + tableColumn.serverId + " VARCHAR," // Server Unique Id
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.motherId + " VARCHAR,"
            + tableColumn.motherAdmissionId+ " VARCHAR,"
            + tableColumn.motherTemperature+ " VARCHAR,"
            + tableColumn.motherSystolicBP+ " VARCHAR,"
            + tableColumn.motherDiastolicBP+ " VARCHAR,"
            + tableColumn.motherPulse+ " VARCHAR,"
            + tableColumn.motherUterineTone+ " VARCHAR,"
            + tableColumn.temperatureUnit+ " VARCHAR,"
            + tableColumn.episitomyCondition+ " VARCHAR,"
            + tableColumn.newSanitoryPadCheck+ " VARCHAR,"
            + tableColumn.motherUrinationAfterDelivery+ " VARCHAR,"
            + tableColumn.sanitoryPadStatus+ " VARCHAR,"
            + tableColumn.isSanitoryPadStink+ " VARCHAR,"
            + tableColumn.admittedSign+ " VARCHAR,"
            + tableColumn.padChanged+ " VARCHAR,"
            + tableColumn.padNotChangeReason+ " VARCHAR,"
            + tableColumn.staffId+ " VARCHAR,"
            + tableColumn.other+ " VARCHAR,"
            + tableColumn.padPicture+ " VARCHAR,"
            + tableColumn.assesmentNumber+ " VARCHAR,"
            + tableColumn.isDataComplete + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        loungeId,
        motherId,
        motherAdmissionId,
        motherTemperature,
        motherSystolicBP,
        motherDiastolicBP,
        motherPulse,
        motherUterineTone,
        temperatureUnit,
        episitomyCondition,
        newSanitoryPadCheck,
        motherUrinationAfterDelivery,
        sanitoryPadStatus,
        isSanitoryPadStink,
        admittedSign,
        padChanged,
        padNotChangeReason,
        staffId,
        other,
        padPicture,
        assesmentNumber,
        isDataComplete,
        isDataSynced,
        syncTime,
        addDate,
        modifyDate,
        json,
        status
    }
}
