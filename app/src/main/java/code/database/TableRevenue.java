package code.database;



public class TableRevenue {

    public static final String tableName = "revenue";

    static final String createTable = ("CREATE TABLE "+ tableName +" (" +
            tableColumn.districtCode + " VARCHAR,"
            + tableColumn.districtNameProperCase + " VARCHAR,"
            + tableColumn.priDistrictCode + " VARCHAR,"
            + tableColumn.districtCensusCode2011 + " VARCHAR,"
            + tableColumn.districtCensusCode2001 + " VARCHAR,"
            + tableColumn.subDistrictCode + " VARCHAR,"
            + tableColumn.subDistrictNameProperCase + " VARCHAR,"
            + tableColumn.subDistrictCensusCode2011 + " VARCHAR,"
            + tableColumn.subDistrictCensusCode2001 + " VARCHAR,"
            + tableColumn.revenueVillageCode + " VARCHAR,"
            + tableColumn.revenueVillageNameProperCase + " VARCHAR,"
            + tableColumn.revenueVillageCensusCode2011 + " VARCHAR,"
            + tableColumn.revenueVillageCensusCode2001 + " VARCHAR,"
            + tableColumn.gpPRICode + " VARCHAR,"
            + tableColumn.rrbanRural + " VARCHAR,"
            + tableColumn.gpNameProperCase + " VARCHAR,"
            + tableColumn.blockPRICode + " VARCHAR,"
            + tableColumn.blockPRINameProperCase + " VARCHAR,"
            + tableColumn.gsvid + " VARCHAR,"
            + tableColumn.status + " VARCHAR,"
            + tableColumn.add_date + " VARCHAR,"
            + tableColumn.modify_date + " VARCHAR"
            + ")");

    public enum tableColumn {

        districtCode,
        districtNameProperCase,
        priDistrictCode,
        districtCensusCode2011,
        districtCensusCode2001,
        subDistrictCode,
        subDistrictNameProperCase,
        subDistrictCensusCode2011,
        subDistrictCensusCode2001,
        revenueVillageCode,
        revenueVillageNameProperCase,
        revenueVillageCensusCode2011,
        revenueVillageCensusCode2001,
        gpPRICode,
        rrbanRural,
        gpNameProperCase,
        blockPRICode,
        blockPRINameProperCase,
        gsvid,
        status,
        add_date,
        modify_date

    }
}
