package com.swm.sprint1.config;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;


public class MysqlCustomDialect extends MySQL5Dialect {
    public MysqlCustomDialect() {
        super();
        this.registerFunction("group_concat", new StandardSQLFunction("group_concat",StandardBasicTypes.STRING));
    }
}