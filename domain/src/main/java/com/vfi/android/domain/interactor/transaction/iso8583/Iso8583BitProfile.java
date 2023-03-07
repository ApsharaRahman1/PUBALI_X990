package com.vfi.android.domain.interactor.transaction.iso8583;
/**
 * Created by laikey on 2017/1/9.
 * 注意：本项目的请求包和返回包都使用此bitArr_req的定义！ by XC 2017-11-10
 * */
public class Iso8583BitProfile {
    public final static String bitArr_req = "[" +
            "{'name':'F0',  'type':'BCD',    'len':4},"   +
            "{'name':'F01', 'type':'BIN',    'len':8},"   +
            "{'name':'F02', 'type':'LBCD',   'len':19},"  +
            "{'name':'F03', 'type':'BCD',    'len':6},"   +
            "{'name':'F04', 'type':'BCD',    'len':12},"  +
            "{'name':'F05', 'type':'BCD',    'len':12},"  +
            "{'name':'F06', 'type':'BCD',    'len':12},"  +
            "{'name':'F07', 'type':'BCD',    'len':8},"   +
            "{'name':'F08', 'type':'BCD',    'len':8},"   +
            "{'name':'F09', 'type':'BCD',    'len':8},"   +
            "{'name':'F10', 'type':'BCD',    'len':8},"   +
            "{'name':'F11', 'type':'BCD',    'len':6},"   +
            "{'name':'F12', 'type':'BCD',    'len':6},"   +
            "{'name':'F13', 'type':'BCD',    'len':4},"   +
            "{'name':'F14', 'type':'BCD',    'len':4},"   +
            "{'name':'F15', 'type':'BCD',    'len':4},"   +
            "{'name':'F16', 'type':'BCD',    'len':4},"   +
            "{'name':'F17', 'type':'BCD',    'len':4},"   +
            "{'name':'F18', 'type':'BCD',    'len':4},"   +
            "{'name':'F19', 'type':'BCD',    'len':3},"   +
            "{'name':'F20', 'type':'BCD',    'len':3},"   +
            "{'name':'F21', 'type':'BCD',    'len':3},"   +
            "{'name':'F22', 'type':'BCD',    'len':3},"   +
            "{'name':'F23', 'type':'BCD',    'len':3},"   +
            "{'name':'F24', 'type':'BCD',    'len':3},"   +
            "{'name':'F25', 'type':'BCD',    'len':2},"   +
            "{'name':'F26', 'type':'BCD',    'len':2},"   +
            "{'name':'F27', 'type':'BCD',    'len':1},"   +
            "{'name':'F28', 'type':'BCD',    'len':8},"   +
            "{'name':'F29', 'type':'BCD',    'len':8},"   +
            "{'name':'F30', 'type':'BCD',    'len':8},"   +
            "{'name':'F31', 'type':'BCD',    'len':8},"   +
            "{'name':'F32', 'type':'LASC',   'len':8},"   +
            "{'name':'F33', 'type':'LBCD',   'len':11},"  +
            "{'name':'F34', 'type':'LBCD',   'len':28},"  +
            "{'name':'F35', 'type':'LBCD',   'len':37},"  +
            "{'name':'F36', 'type':'LLBCD',  'len':104}," +
            "{'name':'F37', 'type':'ASC',    'len':12},"  +
            "{'name':'F38', 'type':'ASC',    'len':6},"   +
            "{'name':'F39', 'type':'ASC',    'len':2},"   +
            "{'name':'F40', 'type':'ASC',    'len':3},"   +
            "{'name':'F41', 'type':'ASC',    'len':8},"   +
            "{'name':'F42', 'type':'ASC',    'len':15},"  +
            "{'name':'F43', 'type':'ASC',    'len':40},"  +
            "{'name':'F44', 'type':'LASC',   'len':99},"  +
            "{'name':'F45', 'type':'LASC',   'len':76},"  +
            "{'name':'F46', 'type':'LLASC',  'len':999}," +
            "{'name':'F47', 'type':'LLASC',  'len':999}," +
            "{'name':'F48', 'type':'LLASC',  'len':999}," +
            "{'name':'F49', 'type':'ASC',    'len':3},"   +
            "{'name':'F50', 'type':'ASC',    'len':3},"   +
            "{'name':'F51', 'type':'ASC',    'len':3},"   +
            "{'name':'F52', 'type':'BIN',    'len':8},"   +
            "{'name':'F53', 'type':'BCD',    'len':16},"  +
            "{'name':'F54', 'type':'LLASC',  'len':120}," +
            "{'name':'F55', 'type':'LLBIN',  'len':999}," +
            "{'name':'F56', 'type':'LLASC',  'len':999}," +
            "{'name':'F57', 'type':'LLBIN',  'len':999}," +
            "{'name':'F58', 'type':'LLASC',  'len':999}," +
            "{'name':'F59', 'type':'LLASC',  'len':999}," +
            "{'name':'F60', 'type':'LLASC',  'len':999}," +
            "{'name':'F61', 'type':'LLASC',  'len':999}," +
            "{'name':'F62', 'type':'LLASC',  'len':999}," +
            "{'name':'F63', 'type':'LLASC',  'len':999}," +
            "{'name':'F64', 'type':'BIN',    'len':8}"    + "]";
}
