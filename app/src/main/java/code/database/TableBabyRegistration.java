package code.database;



public class TableBabyRegistration {

    public static final String tableName = "babyRegistration";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR," // App Unique Id
            + tableColumn.motherId + " VARCHAR,"
            + tableColumn.babyId+ " VARCHAR," // Server Unique Id
            + tableColumn.babyMCTSNumber+ " VARCHAR,"
            + tableColumn.deliveryDate+ " VARCHAR,"
            + tableColumn.deliveryTime+ " VARCHAR,"
            + tableColumn.babyGender+ " VARCHAR,"
            + tableColumn.deliveryType+ " VARCHAR,"
            + tableColumn.babyWeight+ " VARCHAR,"
            + tableColumn.birthWeightAvail+ " VARCHAR,"
            + tableColumn.reason+ " VARCHAR,"
            + tableColumn.babyCryAfterBirth+ " VARCHAR,"
            + tableColumn.babyNeedBreathingHelp+ " VARCHAR,"
            + tableColumn.registrationDateTime+ " VARCHAR,"
            + tableColumn.babyPhoto+ " TEXT,"
            + tableColumn.typeOfBorn+ " VARCHAR,"
            + tableColumn.typeOfOutBorn+ " VARCHAR,"
            + tableColumn.wasApgarScoreRecorded+ " VARCHAR,"
            + tableColumn.apgarScore+ " VARCHAR,"
            + tableColumn.vitaminKGiven+ " VARCHAR,"
            + tableColumn.firstTimeFeed+ " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        babyId,
        motherId,
        babyMCTSNumber,
        deliveryDate,
        deliveryTime,
        babyGender,
        deliveryType,
        babyWeight,
        birthWeightAvail,
        reason, babyCryAfterBirth,
        babyNeedBreathingHelp,
        registrationDateTime,
        babyPhoto,
        typeOfBorn,
        typeOfOutBorn,
        wasApgarScoreRecorded,
        apgarScore,
        vitaminKGiven,
        firstTimeFeed,
        isDataSynced, //0 Not Synced //1 Synced //2 Edit //3 incomplete
        syncTime,
        addDate,
        modifyDate,
        json,
        status
    }
}
