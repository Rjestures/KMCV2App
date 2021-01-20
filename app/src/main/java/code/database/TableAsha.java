package code.database;



public class TableAsha {

    public static final String tableName = "asha";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.ashaId + " VARCHAR,"
            + tableColumn.ashaName + " VARCHAR,"
            + tableColumn.blockName + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.mobName + " VARCHAR" + ")");

    public enum tableColumn {
        ashaId,
        ashaName,
        blockName,
        json,
        mobName
    }
}
