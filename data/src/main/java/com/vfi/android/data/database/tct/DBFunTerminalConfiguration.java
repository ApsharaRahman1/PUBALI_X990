package com.vfi.android.data.database.tct;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunTerminalConfiguration {
    private static final String TAG = TAGS.DataBase;

    private static final int DB_TERMINAL_PRIMARY_KEY = 1;

    public static void save(TerminalCfg terminalCfg) {
        DBModelTerminalConfiguration dbModelTerminalConfiguration = new DBModelTerminalConfiguration(DB_TERMINAL_PRIMARY_KEY, terminalCfg);
        dbModelTerminalConfiguration.save();
    }

    public static TerminalCfg get() {
        DBModelTerminalConfiguration dbModelTerminalConfiguration = new Select().from(DBModelTerminalConfiguration.class)
                .where(DBModelTerminalConfiguration_Table.id.eq(DB_TERMINAL_PRIMARY_KEY))
                .querySingle();

        if (dbModelTerminalConfiguration == null) {
            LogUtil.d(TAG, "Terminal configuration not found.");
            return null;
        }

        return dbModelTerminalConfiguration.getTerminalCfg();
    }

    public static void clear() {
        SQLite.delete().from(DBModelTerminalConfiguration.class).execute();
    }
}
