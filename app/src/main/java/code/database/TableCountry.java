package code.database;



public class TableCountry {

    public static final String tableName = "country";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.id + " VARCHAR,"
            + tableColumn.name + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        id,
        name,
        status
    }
}
