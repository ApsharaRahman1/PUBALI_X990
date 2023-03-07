package com.vfi.android.data.database.tct;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;

/**
 * Created by RuihaoS on 2019/8/13.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelTerminalConfiguration extends BaseModel {
    @Column
    @PrimaryKey
    public int id;

    @Column(typeConverter = DBTerminalCfgConverter.class)
    public TerminalCfg terminalCfg;

    public DBModelTerminalConfiguration() {

    }

    public DBModelTerminalConfiguration(int id, TerminalCfg terminalCfg) {
        this.id = id;
        this.terminalCfg = terminalCfg;
    }

    public TerminalCfg getTerminalCfg() {
        return terminalCfg;
    }

    public void setTerminalCfg(TerminalCfg terminalCfg) {
        this.terminalCfg = terminalCfg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
