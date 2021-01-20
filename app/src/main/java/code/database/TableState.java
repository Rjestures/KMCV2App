package code.database;



public class TableState {

    public static final String tableName = "state";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.id + " VARCHAR,"
            + tableColumn.name + " VARCHAR,"
            + tableColumn.countryId + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        id,
        name,
        countryId,
        status
    }
}
