package Server;

/*
This class has been programmed to make it 
easier for people to connect to database
and manipulate queries with java
it's free to use,edit and publish
Just don't forget to mention the source
Barakat Abwe-Palestin/Nablus
*/
/*needed libraries for IO/SQL/LOGGING*/
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/*you might need to add commons-lang3-3.5 library 
in case of net-beans inner jdbc:mysql library fails
you can find it with the conf file in the same repo
*/
public class Database {

    private Statement statement;/*statement to use for query*/
    private String host, database, user, pass;/*connection arguments*/
    private java.sql.Connection conn = null;/*connection variable*/
    private ResultSet result;/*result variable*/

    public Database() {
        try {
            this.readConfig();/*to read the configuration from the config file*/
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            try {
                this.connect();/*to start the connection*/
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.createStatement();/*to create the statement and make it ready to be used*/
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createStatement() throws SQLException {
        this.statement = this.conn.createStatement();/*statement is created*/
    }

    private void readConfig() throws FileNotFoundException {
        File file = new File("./config/database.conf");/*defining the config file*/
        Scanner scanner = new Scanner(file);/*starting scanner*/
        String conf = "";
        while (scanner.hasNextLine()) {
            conf += scanner.nextLine() + "\n";/*reading the configs*/
        }
        String[] con = conf.split("\n");/*split the configs to lines*/
        this.host = con[0];/*adding hostname*/
        this.database = con[1];/*adding database name*/
        this.user = con[2];/*adding username*/
        this.pass = con[3];/*adding password*/
    }

    public final void connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");/*defining database type  and driver*/
        this.conn = DriverManager.getConnection(("jdbc:mysql://" + this.host + "/"
                + this.database), this.user, this.pass);/*starting the connection*/
    }

    public java.sql.ResultSet select(String query) throws SQLException {/*execute select query*/
        this.result = this.statement.executeQuery(query);
        return result;
    }

    public void insert(String query) throws SQLException {/*execute insert query*/
        this.statement.execute(query);
    }

    public void delete(String query) throws SQLException {/*execute delete query*/
        this.statement.executeUpdate(query);
    }

    public void update(String query) throws SQLException {/*execute update query*/
        this.statement.executeUpdate(query);
    }

    public int numOfAffectedRows() throws SQLException {/*get number of affected rows after insert,delete,update*/
        return this.statement.getUpdateCount();
    }

    public void closeConnection() throws SQLException {/*to close the connection*/
        this.conn.close();
    }
     public int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }
    int getColumnCount(ResultSet result) {
        try {
            return result.getMetaData().getColumnCount();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}

