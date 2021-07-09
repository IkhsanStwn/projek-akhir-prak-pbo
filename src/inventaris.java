import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class inventaris {
    private JTextField tfid_brg;
    private JTextField tfnama_brg;
    private JTextField tfharga_brg;
    private JTextField tfstok_brg;
    private JButton hapusButton;
    private JButton ubahButton;
    private JButton simpanButton;
    private JTable tbBarang;
    private JPanel inventaris;
    private JButton resetButton;
    private JButton transaksiButton;
    private JButton inventarisButton;

    connectorDB connectorDB = new connectorDB();

    DefaultTableModel model;

    String id_barang;

    public inventaris() {
        JFrame frame = new JFrame("Toko Buku dan Alat Tulis");
        frame.setContentPane(inventaris);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //set tabel inventaris barang
        String [] judul = {"ID","Nama Barang","Stok","Harga"};
        model = new DefaultTableModel(judul,0);
        tbBarang.setModel(model);
        tampilTabel();

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (getId_barang().equals("")||getNama_brg().equals("")||getStok_brg().equals("")||getHarga_brg().equals("")){
                        JOptionPane.showMessageDialog(null,"Data Tidak Boleh Kosong!", "Peringatan",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        connectorDB.statement = connectorDB.koneksi.createStatement();
                        String query = ("INSERT INTO barang VALUES ('"+getId_barang()+"','"+getNama_brg()+"','"+getStok_brg()+"','"+getHarga_brg()+"')");

                        connectorDB.statement.executeUpdate(query);

                        JOptionPane.showMessageDialog(null,"Berhasil Disimpan!");

                        resetData();
                        tampilTabel();
                    }

                } catch (SQLException er){
                    JOptionPane.showMessageDialog(null, "Data Gagal Disimpan!" + er, "Hasil", JOptionPane.ERROR_MESSAGE );
                }
            }
        });
        ubahButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (getId_barang().equals("")||getNama_brg().equals("")||getStok_brg().equals("")||getHarga_brg().equals("")){
                        JOptionPane.showMessageDialog(null,"Data Tidak Boleh Kosong!", "Peringatan",JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        connectorDB.statement = connectorDB.koneksi.createStatement();
                        if (id_barang==null){
                            JOptionPane.showMessageDialog(null, "Tidak Dapat Mengubah Data! Silahkan Pilih(Klik) Data pada Tabel", "Peringatan!", JOptionPane.ERROR_MESSAGE );
                            resetData();
                        } else {
                            String query = ("UPDATE barang SET id_barang = '"+getId_barang()+"', nama_barang = '"+getNama_brg()+"', stok = '"+getStok_brg()+"', harga_barang = '"+getHarga_brg()+"' WHERE id_barang = '"+id_barang+"'");
                            connectorDB.statement.executeUpdate(query);

                            JOptionPane.showMessageDialog(null,"Berhasil Disimpan!");

                            resetData();
                            tampilTabel();
                        }

                    }

                } catch (SQLException er){
                    JOptionPane.showMessageDialog(null, "Data Gagal Disimpan!" + er, "Hasil", JOptionPane.ERROR_MESSAGE );
                }
            }
        });
        hapusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (id_barang==null){
                        JOptionPane.showMessageDialog(null, "Tidak Dapat Menghapus Data! Silahkan Pilih(Klik) Data pada Tabel", "Peringatan!", JOptionPane.ERROR_MESSAGE );
                        resetData();
                    }else if (getId_barang().equals("")){
                        JOptionPane.showMessageDialog(null, "ID Barang Kosong, Tidak Dapat Menghapus Data!", "Hasil", JOptionPane.ERROR_MESSAGE );
                    }else if (!getId_barang().equals(id_barang)){
                        JOptionPane.showMessageDialog(null, "ID Barang Jangan di Ubah, Tidak Dapat Menghapus Data!", "Hasil", JOptionPane.ERROR_MESSAGE );
                    }else {
                        connectorDB.statement = connectorDB.koneksi.createStatement();
                        String query = ("DELETE FROM barang WHERE id_barang = '"+getId_barang()+"'");
                        connectorDB.statement.executeUpdate(query);

                        JOptionPane.showMessageDialog(null, "Berhasil di Hapus!", "Peringatan", JOptionPane.INFORMATION_MESSAGE);

                        resetData();
                        tampilTabel();
                    }

                }catch (SQLException er){
                    JOptionPane.showMessageDialog(null, "Data Gagal Di Hapus", "Peringatan!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetData();
            }
        });
        transaksiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                transaksi transaksi = new transaksi();
            }
        });
        inventarisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetData();
            }
        });
        tbBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbBarang.getSelectedRow();
                if(i>-1){
                    tfid_brg.setText(model.getValueAt(i,0).toString());
                    tfnama_brg.setText(model.getValueAt(i,1).toString());
                    tfstok_brg.setText(model.getValueAt(i,2).toString());
                    tfharga_brg.setText(model.getValueAt(i,3).toString());
                }
                id_barang = getId_barang();
            }
        });
    }

    private void tampilTabel(){
        int row = tbBarang.getRowCount();

        for (int a=0; a<row; a++){
            model.removeRow(0);
        }

        try{
            connectorDB.statement = connectorDB.koneksi.createStatement();

            String query = ("SELECT * FROM barang");
            ResultSet resultSet = connectorDB.statement.executeQuery(query);
            while (resultSet.next()){
                String data[] ={resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4)};
                model.addRow(data);
            }
        }catch (SQLException er){
            JOptionPane.showMessageDialog(null, "Gagal Terhubung Databse, Periksa Query!");
        }
    }

    private void resetData(){
        id_barang = null;
        tfid_brg.setText(null);
        tfnama_brg.setText(null);
        tfstok_brg.setText(null);
        tfharga_brg.setText(null);
    }

    private String getId_barang(){
        return tfid_brg.getText();
    }

    private String getNama_brg(){
        return tfnama_brg.getText();
    }

    private String getStok_brg(){
        return tfstok_brg.getText();
    }

    private String getHarga_brg(){
        return tfharga_brg.getText();
    }


}
