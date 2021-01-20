package code.database;

public class TableCounsellingPosters {

    public static final String tableName = "counsellingByPosters";

    static final String createTable = ("CREATE TABLE  "+ tableName +" (" +
            TableCounsellingPosters.tableColumn.posterId + " VARCHAR,"
            + TableCounsellingPosters.tableColumn.posterTitle + " VARCHAR,"
            + TableCounsellingPosters.tableColumn.ConsumeTime + " VARCHAR,"
            + TableCounsellingPosters.tableColumn.type + " VARCHAR,"
            + TableCounsellingPosters.tableColumn.babyId + " VARCHAR,"
            + TableCounsellingPosters.tableColumn.date + " VARCHAR" + ")");

    public enum tableColumn {
        posterId,
        posterTitle,
        ConsumeTime,
        type,
        babyId,
        date,
    }

}
