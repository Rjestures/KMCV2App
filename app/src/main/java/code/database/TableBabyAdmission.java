package code.database;



public class TableBabyAdmission {

    public static final String tableName = "babyAdmission";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.uuid + " VARCHAR," // App Unique Id
            + tableColumn.serverId + " VARCHAR," // Server Unique Id
            + tableColumn.babyId+ " VARCHAR,"
            + tableColumn.loungeId+ " VARCHAR,"
            + tableColumn.babyFileId+ " VARCHAR,"
            + tableColumn.attendentStaffId+ " VARCHAR,"
            + tableColumn.admissionDateTime+ " VARCHAR,"
            + tableColumn.babyBirthStatus+ " VARCHAR,"
            + tableColumn.babyAdmission+ " VARCHAR,"
            + tableColumn.weightAdmitted+ " VARCHAR,"
            + tableColumn.personSign+ " TEXT,"
            + tableColumn.referredFacilityAddress + " VARCHAR,"
            + tableColumn.nameReferredFacility+ " VARCHAR,"
            + tableColumn.address+ " VARCHAR,"
            + tableColumn.weightGainOrLoseAfterAdmission+ " VARCHAR,"
            + tableColumn.babyDischargeWeigth+ " VARCHAR,"
            + tableColumn.typeOfDischarge+ " VARCHAR,"
            + tableColumn.referredReason+ " VARCHAR,"
            + tableColumn.dischargeByDoctor+ " VARCHAR,"
            + tableColumn.dischargeByNurse+ " VARCHAR,"
            + tableColumn.transportation+ " VARCHAR,"
            + tableColumn.signOfFamilyMember+ " TEXT,"
            + tableColumn.dischargeChecklist+ " VARCHAR,"
            + tableColumn.dateOfFollowUpVisit+ " VARCHAR,"
            + tableColumn.doctorSign+ " TEXT,"
            + tableColumn.dateOfDicharge+ " VARCHAR,"
            + tableColumn.ashaName+ " VARCHAR,"
            + tableColumn.ashaSign+ " TEXT,"
            + tableColumn.babyDischargePdfName+ " VARCHAR,"
            + tableColumn.babyKMCPdfName+ " VARCHAR,"
            + tableColumn.babyFeedingPdfName+ " VARCHAR,"
            + tableColumn.babyWeightPdfName+ " VARCHAR,"
            + tableColumn.babyPDFFileName+ " VARCHAR,"
            + tableColumn.apgar1min+ " VARCHAR,"
            + tableColumn.apgar5min+ " VARCHAR,"
            + tableColumn.vitaminKGiven+ " VARCHAR,"
            + tableColumn.isDataSynced + " VARCHAR,"
            + tableColumn.syncTime + " VARCHAR,"
            + tableColumn.districtReferred + " VARCHAR,"
            + tableColumn.sehmatiPhoto + " TEXT,"
            + tableColumn.addDate + " VARCHAR,"
            + tableColumn.kmcPosition + " VARCHAR,"
            + tableColumn.kmcMonitoring + " VARCHAR,"
            + tableColumn.kmcNutrition + " VARCHAR,"
              + tableColumn.gestAge + " VARCHAR,"
            + tableColumn.kmcRespect + " VARCHAR,"
            + tableColumn.convulsions+ " VARCHAR,"
            + tableColumn.infantComingFrom+ " VARCHAR,"
            + tableColumn.kmcHygiene + " VARCHAR,"
            + tableColumn.whatisKmc + " VARCHAR,"
            + tableColumn.modifyDate + " VARCHAR,"
            + tableColumn.type + " VARCHAR,"
            + tableColumn.isAnyComplicationAtBirth + " JSON,"
            + tableColumn.json + " JSON,"
            + tableColumn.status + " VARCHAR" + ")");

    public enum tableColumn {
        uuid,
        serverId,
        babyId,
        loungeId,
        babyFileId,
        attendentStaffId,
        admissionDateTime,
        kmcPosition,
        kmcMonitoring,
        kmcNutrition,
        kmcRespect,
        kmcHygiene,
        whatisKmc,
        babyBirthStatus,
        babyAdmission,
        weightAdmitted,
        personSign,
        referredFacilityAddress,
        nameReferredFacility,
        address,
        weightGainOrLoseAfterAdmission,
        convulsions,
        babyDischargeWeigth,
        typeOfDischarge,
        referredReason,
        dischargeByDoctor,
        dischargeByNurse,
        transportation,
        signOfFamilyMember,
        infantComingFrom,
        dischargeChecklist,
        dateOfFollowUpVisit,
        doctorSign,
        dateOfDicharge,
        ashaName,
        ashaSign,
        babyDischargePdfName,
        babyKMCPdfName,
        babyFeedingPdfName,
        babyWeightPdfName,
        babyPDFFileName,
        apgar1min,
        apgar5min,
        vitaminKGiven,
        gestAge,
        isDataSynced,
        syncTime,
        sehmatiPhoto,
        addDate,
        districtReferred,
        isAnyComplicationAtBirth,
        modifyDate,
        type,
        json,
        status
    }
}
