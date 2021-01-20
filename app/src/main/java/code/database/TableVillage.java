package code.database;



public class TableVillage {

    public static final String tableName = "village";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.villageId + " VARCHAR,"
            + tableColumn.blockId + " VARCHAR,"
            + tableColumn.urbanRural + " VARCHAR,"
            + tableColumn.villageName + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        villageId,
        blockId,
        urbanRural,
        villageName,
        json,
        status
    }
}
