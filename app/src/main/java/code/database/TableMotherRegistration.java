package code.database;



public class TableMotherRegistration {

    public static final String tableName = "motherRegistration";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR," // App Unique Id
            + tableColumn.loungeId + " VARCHAR,"
            + tableColumn.motherId + " VARCHAR," // Server Unique Id
            + tableColumn.motherName + " VARCHAR,"
            + tableColumn.isMotherAdmitted + " VARCHAR," //Yes No
            + tableColumn.reasonForNotAdmitted + " VARCHAR," //If No
            + tableColumn.motherPicture + " TEXT,"
            + tableColumn.motherMCTSNumber + " VARCHAR,"
            + tableColumn.motherAadharNumber + " VARCHAR,"
            + tableColumn.motherDob + " VARCHAR,"
            + tableColumn.motherAge + " VARCHAR,"
            + tableColumn.motherEducation + " VARCHAR,"
            + tableColumn.motherCaste + " VARCHAR,"
            + tableColumn.motherReligion + " VARCHAR,"
            + tableColumn.motherMoblieNo + " VARCHAR,"
            + tableColumn.fatherName + " VARCHAR,"
            + tableColumn.fatherAadharNumber + " VARCHAR,"
            + tableColumn.fatherMoblieNo + " VARCHAR,"
            + tableColumn.rationCardType + " VARCHAR,"
            + tableColumn.guardianName + " VARCHAR,"
            + tableColumn.guardianNumber + " VARCHAR,"
            + tableColumn.guardianRelation + " VARCHAR,"
            + tableColumn.relationWithChildOther + " VARCHAR,"
            + tableColumn.motherWeight+ " VARCHAR,"
            + tableColumn.ageOfMarriage+ " VARCHAR,"
            + tableColumn.birthSpacing+ " VARCHAR,"
            + tableColumn.consanguinity+ " VARCHAR,"
            + tableColumn.presentCountry + " VARCHAR,"
            + tableColumn.presentState + " VARCHAR,"
            + tableColumn.presentResidenceType + " VARCHAR," // Urban , Rural
            + tableColumn.presentAddress + " VARCHAR,"
            + tableColumn.presentVillageName + " VARCHAR,"
            + tableColumn.presentBlockName + " VARCHAR,"
            + tableColumn.presentDistrictName + " VARCHAR,"
            + tableColumn.permanentResidenceType + " VARCHAR," // Urban , Rural
            + tableColumn.permanentCountry + " VARCHAR,"
            + tableColumn.permanentState + " VARCHAR,"
            + tableColumn.permanentAddress + " VARCHAR,"
            + tableColumn.permanentVillageName + " VARCHAR,"
            + tableColumn.permanentBlockName + " VARCHAR,"
            + tableColumn.permanentDistrictName + " VARCHAR,"
            + tableColumn.presentPinCode + " VARCHAR,"
            + tableColumn.permanentPinCode + " VARCHAR,"
            + tableColumn.sameaddress + " VARCHAR,"
            + tableColumn.ashaID + " VARCHAR,"
            + tableColumn.ashaName + " VARCHAR,"
            + tableColumn.ashaNumber + " VARCHAR,"
            + tableColumn.presentAddressNearBy + " VARCHAR,"
            + tableColumn.permanentAddressNearBy + " VARCHAR,"
            + tableColumn.staffId + " VARCHAR,"
            + tableColumn.type + " VARCHAR," //1-known ,  2- unknown , 3 - died
            + tableColumn.organisationName + " VARCHAR,"
            + tableColumn.organisationNumber + " VARCHAR,"
            + tableColumn.organisationAddress + " VARCHAR,"
            + tableColumn.para + " VARCHAR,"
            + tableColumn.gravida + " VARCHAR,"
            + tableColumn.abortion + " VARCHAR,"
            + tableColumn.live + " VARCHAR,"
            + tableColumn.multipleBirth + " VARCHAR," // Yes ,No
            + tableColumn.admittedSign + " TEXT,"
            + tableColumn.motherLmpDate + " VARCHAR,"
            + tableColumn.motherDeliveryPlace + " VARCHAR,"
            + tableColumn.motherDeliveryDistrict + " VARCHAR,"
            + tableColumn.estimatedDateOfDelivery + " VARCHAR,"
            + tableColumn.facilityID + " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.json + " JSON,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.typeOfBorn + " VARCHAR,"
            + tableColumn.typeOfOutBorn + " VARCHAR,"
            + tableColumn.infantComingFrom + " VARCHAR,"
            + tableColumn.infantComingFromOther + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        loungeId,
        motherId,
        motherName,
        isMotherAdmitted,
        reasonForNotAdmitted,
        motherPicture,
        motherMCTSNumber,
        motherAadharNumber,
        motherDob,
        motherAge,
        motherEducation,
        motherCaste,
        motherReligion,
        motherMoblieNo,
        fatherName,
        fatherAadharNumber,
        fatherMoblieNo,
        rationCardType,
        guardianName,
        guardianNumber,
        motherWeight,
        ageOfMarriage,
        birthSpacing,
        consanguinity,
        guardianRelation,
        relationWithChildOther,
        presentCountry,
        presentState,
        presentResidenceType,
        presentAddress,
        presentVillageName,
        presentBlockName,
        presentDistrictName,
        permanentResidenceType,
        permanentCountry,
        permanentState,
        permanentAddress,
        permanentVillageName,
        permanentBlockName,
        permanentDistrictName,
        presentPinCode,
        permanentPinCode,
        ashaID,
        ashaName,
        ashaNumber,
        presentAddressNearBy,
        permanentAddressNearBy,
        staffId,
        type, //1-known ,  2- unknown , 3 - died
        organisationName,
        organisationNumber,
        organisationAddress,
        para,
        gravida,
        abortion,
        live,
        multipleBirth,
        admittedSign,
        motherLmpDate,
        motherDeliveryPlace,
        motherDeliveryDistrict,
        sameaddress,
        facilityID,
        estimatedDateOfDelivery,
        isDataSynced, //0 Not Synced //1 Synced //2 Edit //3 incomplete
        syncTime,
        addDate,
        typeOfBorn,
        typeOfOutBorn,
        infantComingFrom,
        infantComingFromOther,
        json,
        modifyDate,
        status
    }
}
