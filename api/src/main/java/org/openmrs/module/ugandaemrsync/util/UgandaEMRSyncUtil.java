package org.openmrs.module.ugandaemrsync.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.CONNECTION_SUCCESS_200;
import static org.openmrs.module.ugandaemrsync.server.SyncConstant.CONNECTION_SUCCESS_201;

public class UgandaEMRSyncUtil {

	public static List<Object> getSuccessCodeList() {
        List<Object> success = new ArrayList<>();
        success.add(CONNECTION_SUCCESS_200);
        success.add(CONNECTION_SUCCESS_201);
        return success;
    }

    public static Date addDaysToDate(Date date, Integer numberOfDays) {
        Date today = new Date();
        System.out.println(today);      //Sat Jul 14 22:25:03 IST 2018
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, numberOfDays);
        return cal.getTime();
    }
}
