package code.database;



public class TableLounge {

    public static final String tableName = "lounge";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.loungeName + " VARCHAR,"
            + tableColumn.contactNumber + " VARCHAR,"
            + tableColumn.numberOfBeds + " VARCHAR,"
            + tableColumn.recliningChairs + " VARCHAR,"
            + tableColumn.bedSideTable + " VARCHAR,"
            + tableColumn.acAvailability + " VARCHAR,"
            + tableColumn.weighingScale + " VARCHAR,"
            + tableColumn.roomThermometer + " VARCHAR,"
            + tableColumn.roomHeater + " VARCHAR,"
            + tableColumn.wallClock + " VARCHAR,"
            + tableColumn.ledTv + " VARCHAR,"
            + tableColumn.musicSystem + " VARCHAR,"
            + tableColumn.almirah + " VARCHAR,"
            + tableColumn.chargingPoint + " VARCHAR,"
            + tableColumn.nursingStation + " VARCHAR,"
            + tableColumn.locker + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.tabletWithLoungeApp + " VARCHAR,"
            + tableColumn.powerBackup + " VARCHAR,"
            + tableColumn.thermometer + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.facilityId + " VARCHAR,"
            + tableColumn.facilityName + " VARCHAR,"
            + tableColumn.facilityAddress + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        loungeName,
        contactNumber,
        numberOfBeds,
        recliningChairs,
        acAvailability,
        bedSideTable,
        roomHeater,
        weighingScale,
        roomThermometer,
        wallClock,
        ledTv,
        musicSystem,
        almirah,
        chargingPoint,
        type,
        nursingStation,
        locker,
        tabletWithLoungeApp,
        facilityId,
        facilityName,
        facilityAddress,
        powerBackup,
        thermometer,
        addDate,
        modifyDate,
        json,
        status
    }
}
