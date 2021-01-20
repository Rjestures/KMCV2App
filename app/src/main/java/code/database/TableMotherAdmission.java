package code.database;



public class TableMotherAdmission {

    public static final String tableName = "motherAdmission";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR," // App Unique Id
            + tableColumn.serverId + " VARCHAR," // Server Unique Id
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.motherId + " VARCHAR,"
            + tableColumn.hospitalRegistrationNumber+ " VARCHAR,"
            + tableColumn.admittedPersonSign+ " VARCHAR,"
            + tableColumn.referredFacilityName+ " VARCHAR,"
            + tableColumn.referredFacilityAddress+ " VARCHAR,"
            + tableColumn.typeOfDischarge+ " VARCHAR,"
            + tableColumn.referredReason+ " VARCHAR,"
            + tableColumn.dischargeByDoctor+ " VARCHAR,"
            + tableColumn.dischargeByNurse+ " VARCHAR,"
            + tableColumn.transportation+ " VARCHAR,"
            + tableColumn.signOfFamilyMember+ " VARCHAR,"
            + tableColumn.dischargeChecklist+ " VARCHAR,"
            + tableColumn.dateOfFollowUpVisit+ " VARCHAR,"
            + tableColumn.doctorSign+ " VARCHAR,"
            + tableColumn.dateOfDicharge+ " VARCHAR,"
            + tableColumn.ashaSign+ " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        motherId,
        loungeId,
        hospitalRegistrationNumber,
        admittedPersonSign,
        referredFacilityName,
        referredFacilityAddress,
        typeOfDischarge,
        referredReason,
        dischargeByDoctor,
        dischargeByNurse,
        transportation,
        signOfFamilyMember,
        dischargeChecklist,
        dateOfFollowUpVisit,
        doctorSign,
        dateOfDicharge,
        ashaSign,
        isDataSynced,
        syncTime,
        status,
        addDate,
        modifyDate
    }
}
