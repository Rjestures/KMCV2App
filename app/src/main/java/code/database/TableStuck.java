package code.database;



public class TableStuck {

    public static final String tableName = "stuck";

    static final String createTable = ("CREATE TABLE  "+ tableName +" (" +
            tableColumn.loungeId + " VARCHAR,"
            + tableColumn.latitude + " VARCHAR,"
            + tableColumn.longitude + " VARCHAR,"
            + tableColumn.location + " VARCHAR,"
            + tableColumn.uuid + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        loungeId,
        latitude,
        longitude,
        location,
        uuid,
        addDate,
        json,
        status
    }
}
