package dev.efnilite.witp.util.sql;

import dev.efnilite.fycore.sql.InvalidStatementException;

public abstract class Statement {

    protected SQLManager manager;
    protected String table;

    public Statement(SQLManager manager, String table) {
        this.manager = manager;
        this.table = table;
    }

    public abstract void query() throws InvalidStatementException;
}