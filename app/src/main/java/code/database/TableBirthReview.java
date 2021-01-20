package code.database;



public class TableBirthReview {

    public static final String tableName = "birthReview";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR,"
            + tableColumn.serverId + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.nurseId + " VARCHAR,"
            + tableColumn.latitude + " VARCHAR,"
            + tableColumn.longitude + " VARCHAR,"
            + tableColumn.shift + " VARCHAR,"
            + tableColumn.totalLiveBirth + " VARCHAR,"
            + tableColumn.totalStableBabies + " VARCHAR,"
            + tableColumn.totalUnstableBabies + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        loungeId,
        nurseId,
        latitude,
        longitude,
        shift,
        totalLiveBirth,
        totalStableBabies,
        totalUnstableBabies,
        addDate,
        json,
        status
    }
}
