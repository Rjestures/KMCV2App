package code.database;



public class TableFacility {

    public static final String tableName = "facility";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.facilityId + " VARCHAR,"
            + tableColumn.facilityType + " VARCHAR,"
            + tableColumn.facilityTypeId + " VARCHAR,"
            + tableColumn.facilityName + " VARCHAR,"
            + tableColumn.districtName + " VARCHAR,"
            + tableColumn.priority + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.priCodeDistrict + " VARCHAR" + ")");

    public enum tableColumn {
        facilityId,
        facilityType,
        facilityTypeId,
        facilityName,
        districtName,
        priority,
        json,
        priCodeDistrict
    }
}
