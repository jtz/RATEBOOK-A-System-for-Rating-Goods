package goods.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDB {
	
	// path of database file
	public static final String url = "jdbc:sqlite:C:/lib/sqlite/ratebook.db";

	public static void main(String[] args) {
		createTable();
	}
	
    public static void createTable() {
        try (Connection conn = DriverManager.getConnection(url);
        		Statement stmt = conn.createStatement()){            
        	
        	// create Goods table
            String sql = "CREATE TABLE IF NOT EXISTS goods (\n"
                    + "	barcode text PRIMARY KEY,\n"
                    + "	name text NOT NULL,\n"
                    + "	brand text NOT NULL,\n"
                    + "	rateCount integer,\n"
                    + "	rating real,\n"
                    + "	priceRangeLow real,\n"
                    + "	priceRangeHigh real,\n"
                    + "	goodsCategory text,\n"
                    + "	ageRangeLow integer,\n"
                    + "	ageRangeHigh integer\n"
                    + ");";
        	
        	stmt.execute(sql);
        	System.out.println("Successfully create table Goods.");
        	
        	// create Review table
        	// barcode + client is primary key
            sql = "CREATE TABLE IF NOT EXISTS review (\n"
                    + "	barcode text NOT NULL,\n"
                    + "	client text NOT NULL,\n"
                    + "	createTime text NOT NULL,\n"
                    + "	review text,\n"
                    + " PRIMARY KEY (barcode, client)\n"
                    + ");";
        	
        	stmt.execute(sql);
        	System.out.println("Successfully create table Review.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
