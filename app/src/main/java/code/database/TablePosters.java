package code.database;


public class TablePosters {

    public static final String tableName = "posters";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
             tableColumn.posterId + " VARCHAR,"
            + tableColumn.posterName + " VARCHAR,"
            + tableColumn.posterType + " VARCHAR,"
            + tableColumn.posterUrl + " VARCHAR,"
            + tableColumn.posterUrl_base64 + " VARCHAR,"
            + tableColumn.status + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR" + ")");

    public enum tableColumn {
        posterId,
        posterName,
        posterType,
        posterUrl_base64,
        posterUrl,
        status,
        addDate,

    }
}

