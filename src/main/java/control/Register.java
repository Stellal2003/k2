package control;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.DriverManagerConnectionPool;

public class Register {
    public boolean registerUser(String email, String password, String nome, String cognome, String indirizzo, String telefono, String numero, String intestatario, int CVV) {
        boolean isRegistered = false;

        try {
            Connection con = DriverManagerConnectionPool.getConnection();
            String sql = "INSERT INTO UserAccount (email, passwordUser, nome, cognome, indirizzo, telefono, numero, intestatario, CVV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, checkPsw(password)); // Utilizzo di SHA-256 per crittografare la password
            ps.setString(3, nome);
            ps.setString(4, cognome);
            ps.setString(5, indirizzo);
            ps.setString(6, telefono);
            ps.setString(7, numero);
            ps.setString(8, intestatario);
            ps.setInt(9, CVV);

            int result = ps.executeUpdate();
            isRegistered = (result > 0);

            DriverManagerConnectionPool.releaseConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isRegistered;
    }

    private String checkPsw(String psw) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] messageDigest = md.digest(psw.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        StringBuilder hashtext = new StringBuilder(number.toString(16));

        // Pad with leading zeros to ensure full 64 characters
        while (hashtext.length() < 64) {
            hashtext.insert(0, "0");
        }

        return hashtext.toString();
    }
}
