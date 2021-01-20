package code.database;



public class TableBlock {

    public static final String tableName = "block";

    static final String createTable = ("CREATE TABLE  "+ tableName +" (" +
            tableColumn.districtId + " VARCHAR,"
            + tableColumn.blockId + " VARCHAR,"
            + tableColumn.blockName + " VARCHAR,"
            + tableColumn.urbanRural + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");


    public enum tableColumn {
        districtId,
        blockId,
        blockName,
        urbanRural,
        json,
        status
    }
}
