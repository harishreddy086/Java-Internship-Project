import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HotelReservationGUI extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root";
    private static final String PASS = "password"; // Replace with your password

    private Connection conn;

    public HotelReservationGUI() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }

        setTitle("Hotel Reservation System");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));

        JButton reserveBtn = new JButton("Reserve Room");
        JButton viewBtn = new JButton("View Reservations");
        JButton getRoomBtn = new JButton("Get Room Number");
        JButton updateBtn = new JButton("Update Reservation");
        JButton deleteBtn = new JButton("Delete Reservation");

        reserveBtn.addActionListener(e -> reserveRoomUI());
        viewBtn.addActionListener(e -> viewReservationsUI());
        getRoomBtn.addActionListener(e -> getRoomNumberUI());
        updateBtn.addActionListener(e -> updateReservationUI());
        deleteBtn.addActionListener(e -> deleteReservationUI());

        add(reserveBtn);
        add(viewBtn);
        add(getRoomBtn);
        add(updateBtn);
        add(deleteBtn);

        setVisible(true);
    }

    private void reserveRoomUI() {
        JFrame frame = new JFrame("Reserve Room");
        frame.setSize(300, 250);
        frame.setLayout(new GridLayout(4, 2));

        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField contactField = new JTextField();
        JButton submitBtn = new JButton("Submit");

        submitBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int room = Integer.parseInt(roomField.getText());
                String contact = contactField.getText();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setInt(2, room);
                ps.setString(3, contact);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Reservation Successful!");
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.add(new JLabel("Guest Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Room Number:"));
        frame.add(roomField);
        frame.add(new JLabel("Contact Number:"));
        frame.add(contactField);
        frame.add(new JLabel(""));
        frame.add(submitBtn);
        frame.setVisible(true);
    }

    private void viewReservationsUI() {
        JFrame frame = new JFrame("View Reservations");
        frame.setSize(600, 300);
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.setColumnIdentifiers(new String[]{"ID", "Guest", "Room", "Contact", "Date"});

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reservations");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("reservation_date")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }

        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    private void getRoomNumberUI() {
        JFrame frame = new JFrame("Get Room Number");
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(3, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JButton fetchBtn = new JButton("Fetch");

        fetchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                PreparedStatement ps = conn.prepareStatement("SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?");
                ps.setInt(1, id);
                ps.setString(2, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int room = rs.getInt("room_number");
                    JOptionPane.showMessageDialog(frame, "Room Number: " + room);
                } else {
                    JOptionPane.showMessageDialog(frame, "Reservation not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.add(new JLabel("Reservation ID:"));
        frame.add(idField);
        frame.add(new JLabel("Guest Name:"));
        frame.add(nameField);
        frame.add(new JLabel(""));
        frame.add(fetchBtn);
        frame.setVisible(true);
    }

    private void updateReservationUI() {
        JFrame frame = new JFrame("Update Reservation");
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(5, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField contactField = new JTextField();
        JButton updateBtn = new JButton("Update");

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                int room = Integer.parseInt(roomField.getText());
                String contact = contactField.getText();

                PreparedStatement ps = conn.prepareStatement("UPDATE reservations SET guest_name=?, room_number=?, contact_number=? WHERE reservation_id=?");
                ps.setString(1, name);
                ps.setInt(2, room);
                ps.setString(3, contact);
                ps.setInt(4, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Reservation Updated!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Reservation not found.");
                }
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.add(new JLabel("Reservation ID:"));
        frame.add(idField);
        frame.add(new JLabel("New Guest Name:"));
        frame.add(nameField);
        frame.add(new JLabel("New Room Number:"));
        frame.add(roomField);
        frame.add(new JLabel("New Contact Number:"));
        frame.add(contactField);
        frame.add(new JLabel(""));
        frame.add(updateBtn);
        frame.setVisible(true);
    }

    private void deleteReservationUI() {
        JFrame frame = new JFrame("Delete Reservation");
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(2, 2));

        JTextField idField = new JTextField();
        JButton deleteBtn = new JButton("Delete");

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                PreparedStatement ps = conn.prepareStatement("DELETE FROM reservations WHERE reservation_id = ?");
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Reservation Deleted.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Reservation not found.");
                }
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.add(new JLabel("Reservation ID:"));
        frame.add(idField);
        frame.add(new JLabel(""));
        frame.add(deleteBtn);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelReservationGUI::new);
    }
}
