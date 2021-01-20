package code.database;



public class TableVideos {

    public static final String tableName = "videos";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.videoId + " VARCHAR,"
            + tableColumn.videoName + " VARCHAR,"
            + tableColumn.videoType + " VARCHAR,"
            + tableColumn.videoUrl + " VARCHAR,"
            + tableColumn.videoThumb + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.videoLocation + " VARCHAR" + ")");

    public enum tableColumn {
        videoId,
        videoName,
        videoType,
        videoUrl,
        videoThumb,
        addDate,
        videoLocation
    }
}
