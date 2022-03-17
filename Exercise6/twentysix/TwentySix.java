import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

/***
 *  Style 25 - Persistent-tables
 *
 * Constraints:
 *
 * The input data of the problem is modeled as entities with relations between them
 * The data is placed in tables, with columns potentially cross-referencing data in other tables
 * Existence of a relational query engine
 * The problem is solved by issuing queries over the tabular data
 *
 *  References
 *      https://github.com/xerial/sqlite-jdbc
 *      https://www.sqlitetutorial.net/sqlite-java/update/
 */

public class TwentySix {

    public static Connection createDB(String db_name)  {

        String url = "jdbc:sqlite:./" + db_name;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database created: " + db_name);
            }
            return conn;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Create DB: Connection failed");
        return null;
    }

    public static void resetWordsTable(Connection conn) {
        if (conn == null) {
            return;
        }
        System.out.println("Reseting Table: words_list");
        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            String[] sql = new String[]{
                    "DROP TABLE if exists words_list",
                    "CREATE TABLE words_list (id INTEGER PRIMARY KEY AUTOINCREMENT, word)"
                };

            for (String s : sql) {
                stmt.execute(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadFileToDB(Connection conn, String filePath) {
        List<String> stop_words;
        List<String> words = null;

        System.out.print("Loading files... ");
        try {
            stop_words = asList(new String(Files.readAllBytes(Paths.get("../../stop_words.txt"))).split(","));
            words = asList(new String(Files.readAllBytes(Paths.get(filePath)))
                    .split("[^a-zA-Z]+"))
                    .stream()
                    .filter(word -> !(stop_words.contains(word.toLowerCase())) && word.length() >= 2)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Complete");

        if(conn != null) {
            try {

                PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO words_list (word)" +
                        "VALUES (?)"
                );
                System.out.print("Adding words... ");
                for (String w: words) {
                    if (words != null) {

                        try {
                            w = w.toLowerCase();
                            pstmt.setString(1, w);
                            pstmt.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Word is null");
                    }
                }
                System.out.println("Complete");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("No connection");
        }

    }


    public static void main(String[] args) {
        Connection conn = null;

        if(args.length != 1){
          System.out.println("Invalid Input");
          return;
        }

        System.out.print("DB Initializing... ");
        if(!Files.exists(Paths.get("./tf.db"))) {
            conn = createDB("tf.db");
            resetWordsTable(conn);
            loadFileToDB(conn, args[0]);
            System.out.print("New DB Created... ");
        } else {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:./tf.db");
                System.out.print("Existing DB Found... ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Success");


        if(conn != null) {
            try {
                // Query and print top 25 words
                String sql =
                        "SELECT word, COUNT(*) as word FROM words_list " +
                                "GROUP BY word ORDER BY word DESC";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                System.out.print("---------- Word counts (top 25) -----------\n");
                int row = 1;
                while(rs.next() && row <=25) {  //each row
                    System.out.println(rs.getString(1) + " - " + rs.getString(2));
                    row++;
                }

                // Query for number of unique words with "z"
                System.out.print("---------- Number of unique words with z -----------\n");
                String sql2 =
                        "SELECT COUNT(DISTINCT word) as C " +
                        "FROM words_list " +
                        "WHERE word " +
                        "LIKE '%z%'";
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql2);
                System.out.println("Total number of unique words with Z: " + rs2.getString(1));

                // List out unique words with z.
//                String sql3 =
//                        "SELECT word, COUNT(*) as c " +
//                        "FROM words_list " +
//                        "WHERE word like '%z%'" +
//                        "GROUP BY word ORDER BY c DESC";
//                Statement stmt3 = conn.createStatement();
//                ResultSet rs3 = stmt3.executeQuery(sql3);
//                row = 1;
//                while(rs3.next() && row <=rs2.getInt(1)) {
//                    System.out.println(rs3.getString(1));
//                    row++;
//                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }
}