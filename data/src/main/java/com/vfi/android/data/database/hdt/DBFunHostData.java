package com.vfi.android.data.database.hdt;


import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by RuihaoS on 2019/8/13.
 */

public class DBFunHostData {
    private static final String TAG = TAGS.DataBase;

    public static void save(HostInfo hostInfo) {
        LogUtil.d(TAG, "DBFunHostData save executed");
        if(hostInfo == null)return;
        if (get(hostInfo.getHostIndex())!=null ) {
            object2DBModel(hostInfo).update();
        }else {
            object2DBModel(hostInfo).save();
        }
    }

    public static void clear() {
        SQLite.delete().from(DBModelHostData.class).execute();
    }

    public static HostInfo get(int hostIndex) {
        DBModelHostData dbModelHostData = new Select().from(DBModelHostData.class)
                .where(DBModelHostData_Table.hostIndex.eq(hostIndex))
                .querySingle();

        if (dbModelHostData == null) {
            LogUtil.d(TAG, "Host[" + hostIndex + "] not found");
            return null;
        }

        return DBModel2Object(dbModelHostData);
    }

    public static HostInfo getHostInfoByHostType(int hostType) {
        DBModelHostData dbModelHostData = new Select().from(DBModelHostData.class)
                .where(DBModelHostData_Table.hostType.eq(hostType))
                .querySingle();

        if (dbModelHostData == null) {
            LogUtil.d(TAG, "Host type[" + hostType + "] not found");
            return null;
        }

        return DBModel2Object(dbModelHostData);
    }

    public static List<HostInfo> getHostInfoByIndexs(String hostIndexs) {
        List<HostInfo> list= new ArrayList<>();
        String[] split = hostIndexs.split(",");
        for (String hostIndexStr :split){
            int hostIndex = StringUtil.parseInt(hostIndexStr, -1);
            if (hostIndex == -1) {
                continue;
            }

            HostInfo hostInfo = get(hostIndex);
            if (hostInfo != null) {
                list.add(hostInfo);
            }
        }

        LogUtil.d(TAG, "getHostInfoByIndexs hostIndexs[" + hostIndexs + "] list size=" + list.size());
        return list;
    }

    public static List<Integer> getHostTypeList() {
        List<Integer> hostTypeList = new ArrayList<>();
        List<DBModelHostData> hostDataList = new Select().from(DBModelHostData.class)
                .queryList();

        Iterator<DBModelHostData> iterator = hostDataList.iterator();
        while (iterator.hasNext()) {
            DBModelHostData hostData = iterator.next();
            hostTypeList.add(hostData.getHostType());
        }

        return hostTypeList;
    }

    public static List<HostInfo> getAllHostInfos() {
        List<HostInfo> hostTypeList = new ArrayList<>();
        List<DBModelHostData> hostDataList = new Select().from(DBModelHostData.class)
                .queryList();

        Iterator<DBModelHostData> iterator = hostDataList.iterator();
        while (iterator.hasNext()) {
            DBModelHostData hostData = iterator.next();
            hostTypeList.add(DBModel2Object(hostData));
        }

        return hostTypeList;
    }

    private static DBModelHostData object2DBModel(HostInfo hostInfo) {
        DBModelHostData dbModelHostData = new DBModelHostData();

        dbModelHostData.setHostIndex(hostInfo.getHostIndex());
        dbModelHostData.setHostType(hostInfo.getHostType());
        dbModelHostData.setHostName(hostInfo.getHostName());
        dbModelHostData.setTPDU(hostInfo.getTPDU());
        dbModelHostData.setNII(hostInfo.getNII());
        dbModelHostData.setPrimaryIp(hostInfo.getPrimaryIp());
        dbModelHostData.setPrimaryPort(hostInfo.getPrimaryPort());
        dbModelHostData.setSecondaryIp(hostInfo.getSecondaryIp());
        dbModelHostData.setSecondaryPort(hostInfo.getSecondaryPort());
        dbModelHostData.setMerchantIndexs(hostInfo.getMerchantIndexs());

        return dbModelHostData;
    }

    private static HostInfo DBModel2Object(DBModelHostData dbModelHostData) {
        HostInfo hostInfo = new HostInfo();

        hostInfo.setHostIndex(dbModelHostData.getHostIndex());
        hostInfo.setHostType(dbModelHostData.getHostType());
        hostInfo.setHostName(dbModelHostData.getHostName());
        hostInfo.setTPDU(dbModelHostData.getTPDU());
        hostInfo.setNII(dbModelHostData.getNII());
        hostInfo.setPrimaryIp(dbModelHostData.getPrimaryIp());
        hostInfo.setPrimaryPort(dbModelHostData.getPrimaryPort());
        hostInfo.setSecondaryIp(dbModelHostData.getSecondaryIp());
        hostInfo.setSecondaryPort(dbModelHostData.getSecondaryPort());
        hostInfo.setMerchantIndexs(dbModelHostData.getMerchantIndexs());

        return hostInfo;
    }

}
