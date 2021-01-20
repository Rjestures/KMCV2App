package code.database;



public class TableBabyMonitoring {

    public static final String tableName = "babyMonitoring";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR," // App Unique Id
            + tableColumn.serverId + " VARCHAR," // Server Unique Id
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.babyId+ " VARCHAR,"
            + tableColumn.babyAdmissionId+ " VARCHAR,"
            + tableColumn.babyMeasuredWeight+ " VARCHAR,"
            + tableColumn.isHeadCircumferenceAvail+ " VARCHAR,"
            + tableColumn.measuringTapeNotAvailReason+ " VARCHAR,"
            + tableColumn.babyHeadCircumference+ " VARCHAR,"
            + tableColumn.temperatureUnit+ " VARCHAR,"
            + tableColumn.babyRespiratoryRate+ " VARCHAR,"
            + tableColumn.babyTemperature+ " VARCHAR,"
            + tableColumn.isPulseOximatoryDeviceAvailable+ " VARCHAR,"
            + tableColumn.type+ " VARCHAR,"
            + tableColumn.babySpO2+ " VARCHAR,"
            + tableColumn.babyPulseRate+ " VARCHAR,"
            + tableColumn.crtKnowledge+ " VARCHAR,"
            + tableColumn.isCftGreaterThree+ " VARCHAR,"
            + tableColumn.urinePassedIn24Hrs+ " VARCHAR,"
            + tableColumn.stoolPassedIn24Hrs+ " VARCHAR,"
            + tableColumn.generalCondition+ " VARCHAR,"
            + tableColumn.tone+ " VARCHAR,"
            + tableColumn.sucking+ " VARCHAR,"
            + tableColumn.apneaOrGasping+ " VARCHAR,"
            + tableColumn.grunting+ " VARCHAR,"
            + tableColumn.chestIndrawing+ " VARCHAR,"
            + tableColumn.color+ " VARCHAR,"
            + tableColumn.isBleeding+ " VARCHAR,"
            + tableColumn.bulgingAnteriorFontanel+ " VARCHAR,"
            + tableColumn.umbilicus+ " VARCHAR,"
            + tableColumn.skinPustules+ " VARCHAR,"
            + tableColumn.staffId+ " VARCHAR,"
            + tableColumn.staffSign+ " VARCHAR,"
            + tableColumn.isThermometerAvailable+ " VARCHAR,"
            + tableColumn.isDataComplete+ " VARCHAR,"
            + tableColumn.isDataSynced+ " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.formattedDate + " VARCHAR,"
            + tableColumn.assessmentNumber + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.isInterestInFeeding + " VARCHAR,"
            + tableColumn.isLengthAvail + " VARCHAR,"
            + tableColumn.lengthValue + " VARCHAR,"
            + tableColumn.lengthReason + " VARCHAR,"
            + tableColumn.lengthReasonOther + " VARCHAR,"
            + tableColumn.babyHeadCircumferenceReason + " VARCHAR,"
            + tableColumn.babyHeadCircumferenceOtherReason + " VARCHAR,"
            + tableColumn.lactation + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        loungeId,
        babyId,
        babyAdmissionId,
        babyMeasuredWeight,
        isHeadCircumferenceAvail,
        isLengthAvail,
        lengthValue,
        lengthReason,
        lengthReasonOther,
        measuringTapeNotAvailReason,
        babyHeadCircumference,
        babyHeadCircumferenceReason,
        babyHeadCircumferenceOtherReason,
        babyRespiratoryRate,
        babyTemperature,
        temperatureUnit,
        isPulseOximatoryDeviceAvailable,
        crtKnowledge,
        isCftGreaterThree,
        type,
        urinePassedIn24Hrs,
        stoolPassedIn24Hrs,
        generalCondition,
        tone,
        sucking,
        apneaOrGasping,
        grunting,
        chestIndrawing,
        color,
        isBleeding,
        bulgingAnteriorFontanel,
        umbilicus,
        skinPustules,
        babySpO2,
        babyPulseRate,
        assessmentNumber,
        staffId,
        staffSign,
        isThermometerAvailable,
        isDataComplete,
        isDataSynced,
        syncTime,
        formattedDate,
        isInterestInFeeding,
        lactation,
        addDate,
        modifyDate,
        json,
        status
    }
}