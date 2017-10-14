import java.sql.*;

public class Driver {

    public static void main(String[] args) {
        String userName = "lfallon";
        String password = "Snoopy08";

        String url = "jdbc:sqlserver://25.2.229.177\\LANCE_SQLSERVER;databaseName=PALAS_STAGE";

        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, userName, password);
            PreparedStatement stmt = conn.prepareStatement("select top 10 * from ChannelSchedule");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("ProgramTitle"));
            }
            System.out.println("stop");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
