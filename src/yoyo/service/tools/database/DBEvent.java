// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.tools.database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.DriverManager;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import yoyo.core.event.AbsEvent;

public class DBEvent extends AbsEvent {

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private List<List<Object>> list;

    public DBEvent() {
        this.setDest("dbservice");
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("proxool.conn");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public List select(final String sql) {
        this.conn = this.getConnection();
        try {
            this.stmt = this.conn.createStatement();
            this.rs = this.stmt.executeQuery(sql);
            ResultSetMetaData rsmd = this.rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            this.list = new ArrayList<List<Object>>();
            while (this.rs.next()) {
                List<Object> obj = new ArrayList<Object>();
                for (int i = 1; i <= columnCount; ++i) {
                    int type = rsmd.getColumnType(i);
                    switch (type) {
                        case -5: {
                            obj.add(this.rs.getLong(i));
                            break;
                        }
                        case 6: {
                            obj.add(this.rs.getFloat(i));
                            break;
                        }
                        case 12: {
                            obj.add(this.rs.getString(i));
                            break;
                        }
                        case 1: {
                            obj.add(this.rs.getString(i));
                            break;
                        }
                        case 4: {
                            obj.add(this.rs.getInt(i));
                            break;
                        }
                        case 16: {
                            obj.add(this.rs.getBoolean(i));
                            break;
                        }
                        case -7: {
                            obj.add(this.rs.getByte(i));
                            break;
                        }
                    }
                }
                this.list.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.close();
        return this.list;
    }

    public int update(final String sql) {
        int state = 0;
        try {
            this.conn = this.getConnection();
            this.stmt = this.conn.createStatement();
            state = this.stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
        return state;
    }

    public boolean delete(final String sql) {
        boolean state = false;
        try {
            this.conn = this.getConnection();
            this.stmt = this.conn.createStatement();
            state = this.stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
        return state;
    }

    public boolean insert(final String sql) {
        boolean state = false;
        try {
            this.conn = this.getConnection();
            this.stmt = this.conn.createStatement();
            state = this.stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
        return state;
    }

    private void close() {
        this.closeResultSet();
        this.closeStatement();
        this.closeConnection();
    }

    private void closeConnection() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeStatement() {
        try {
            if (this.stmt != null) {
                this.stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeResultSet() {
        try {
            if (this.rs != null) {
                this.rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
