package code.database;



public class TableDistrict {

    public static final String tableName = "district";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.districtId + " VARCHAR,"
            + tableColumn.districtName + " VARCHAR,"
            + tableColumn.urbanRural + " VARCHAR,"
            + tableColumn.stateId + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");


    public enum tableColumn {
        districtId,
        districtName,
        urbanRural,
        stateId,
        json,
        status
    }
}
