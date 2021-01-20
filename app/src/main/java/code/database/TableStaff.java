package code.database;



public class TableStaff {

    public static final String tableName = "staff";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.staffId + " VARCHAR,"
            + tableColumn.staffName + " VARCHAR,"
            + tableColumn.staffType + " VARCHAR,"
            + tableColumn.staffSubType + " VARCHAR,"
            + tableColumn.staffAddress + " VARCHAR,"
            + tableColumn.emergencyContactNumber + " VARCHAR,"
            + tableColumn.jobType + " VARCHAR,"
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.facilityId + " VARCHAR,"
            + tableColumn.staffProfile + " VARCHAR,"
            + tableColumn.status + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.staffMobile + " VARCHAR" + ")");

    public enum tableColumn {
        staffId,
        staffName,
        staffType,
        staffSubType,
        staffAddress,
        emergencyContactNumber,
        jobType,
        loungeId,
        facilityId,
        staffProfile,
        status,
        addDate,
        modifyDate,
        staffMobile
    }
}
