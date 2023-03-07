package com.vfi.android.data.database.tct;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

/**
 * Created by yunlongg1 on 12/15/17.
 */

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class DBTerminalCfgConverter extends TypeConverter<String, TerminalCfg> {
    @Override
    public String getDBValue(TerminalCfg model) {
        Gson gson = new GsonBuilder().create();
        String convert = gson.toJson(model);
        LogUtil.d(TAGS.DataBase, "convert[" + convert + "]");

        return convert;
    }

    @Override
    public TerminalCfg getModelValue(String data) {
        Gson gson = new Gson();
        TerminalCfg terminalCfg = gson.fromJson(data, TerminalCfg.class);

        return terminalCfg;
    }
}
