package com.vmsac.vmsacserver.config;

import org.hibernate.dialect.H2Dialect;

import java.sql.Types;

/**
 * Extends H2Dialect to register a mapping for JDBC type ARRAY (2003).
 * Required because vladmihalcea ListArrayType registers itself with Types.ARRAY,
 * and H2Dialect has no default mapping for that type code, causing Hibernate
 * to throw "No Dialect mapping for JDBC type: 2003" during ddl-auto=update.
 */
public class ExtendedH2Dialect extends H2Dialect {

    public ExtendedH2Dialect() {
        super();
        registerColumnType(Types.ARRAY, "ARRAY");
    }
}
