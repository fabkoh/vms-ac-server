package com.vmsac.vmsacserver.config;

import org.hibernate.dialect.H2Dialect;
import java.sql.Types;

/**
 * H2 test dialect set up that registers a column type for JDBC ARRAY (type 2003).
 * Required because some entities use @Type("list-array") (PostgreSQL arrays),
 * which H2Dialect does not handle out of the box.
 */
public class TestH2Dialect extends H2Dialect {
    public TestH2Dialect() {
        super();
        registerColumnType(Types.ARRAY, "array");
    }
}
