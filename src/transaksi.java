import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class transaksi {
    private JTextField tfid_transaksi;
    private JButton baruBtn;
    private JComboBox cbid_barang;
    private JTextField tfnama_barang;
    private JTextField tfharga_barang;
    private JTextField tfjumlah_barang;
    private JButton tambahbtn;
    private JButton hapusButton;
    private JTable tbdetail_transaksi;
    private JTextField tfTotal;
    private JTextField tfBayar;
    private JTextField tfKembalian;
    private JButton simpan_transaksiBtn;
    private JButton keluarBtn;
    private JTextField tfsubtotal_brg;
    private JPanel transaksi;
    private JLabel totalLb;
    private JButton transaksiButton;
    private JButton inventarisButton;
    private JPanel jpTgl_transaksi;

    connectorDB connectorDB = new connectorDB();

    DefaultTableModel model;

    JDateChooser dateChooser = new JDateChooser();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    Integer id_transaksi,total=0, kembalian;

    int rowtb;

    String id_barang, bayarTemp;

    public transaksi() {
        JFrame frame = new JFrame("Toko Buku dan Alat Tulis");
        frame.setContentPane(transaksi);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //set combobox barang
        setCbid_barang();

        //setting kalender
        dateChooser.setDateFormatString("yyyy-MM-dd");
        jpTgl_transaksi.add(dateChooser);

        //setting tabel detail barang transaksi
        String [] judul = {"ID Barang","Nama Barang","Harga","Jumlah","Subtotal Harga"};
        model = new DefaultTableModel(judul,0);
        tbdetail_transaksi.setModel(model);

        //proses cek id transaksi
        tfid_transaksi.setText(cekId_transaksi());

        transaksiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new transaksi();
            }
        });
        inventarisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new inventaris();
            }
        });
        baruBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfid_transaksi.setText(cekId_transaksi());
                resetData();
            }
        });
        tambahbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer subtotal = Integer.parseInt(getJumlah()) * Integer.parseInt(getHarga_brg());
                total = total + subtotal;
                String data[] = {getid_barang(),getNama_brg(),getHarga_brg(),getJumlah(),subtotal.toString()};
                model.addRow(data);
                totalLb.setText(total.toString());
                tfTotal.setText(total.toString());
                resetDataBarang();
            }
        });
        hapusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                total = total - Integer.parseInt(getSubtotal());
                totalLb.setText(total.toString());
                tfTotal.setText(total.toString());
                resetDataBarang();
                model.removeRow(rowtb);
            }
        });
        simpan_transaksiBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    kembalian = total - Integer.parseInt(getBayar());
                    tfKembalian.setText(kembalian.toString());

                    if (dateChooser.equals("")||getTotal().equals("")||getBayar().equals("")){
                        JOptionPane.showMessageDialog(null,"Data Tidak Boleh Kosong!", "Peringatan",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        connectorDB.statement = connectorDB.koneksi.createStatement();
                        String query;

                        query = ("INSERT INTO transaksi VALUES ('"+id_transaksi+"','"+getTgl_transaksi()+"','"+getTotal()+"','"+getBayar()+"')");
                        connectorDB.statement.executeUpdate(query);

                        int row = tbdetail_transaksi.getRowCount();
                        for (int a = 0; a < row; a++){
                            query = ("INSERT INTO detail_transaksi VALUES (null,'"+getId_transaksi()+"','"+model.getValueAt(a,0).toString()+"','"+model.getValueAt(a,3).toString()+"','"+model.getValueAt(a,4).toString()+"')");
                            connectorDB.statement.executeUpdate(query);
                        }

                        JOptionPane.showMessageDialog(null,"Berhasil Disimpan!");

                    }

                } catch (SQLException er){
                    JOptionPane.showMessageDialog(null, "Data Gagal Disimpan!" + er, "Hasil", JOptionPane.ERROR_MESSAGE );
                }

            }
        });
        keluarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        tbdetail_transaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rowtb = tbdetail_transaksi.getSelectedRow();
                if(rowtb>-1){
                    cbid_barang.setSelectedItem(model.getValueAt(rowtb,0).toString());
                    tfnama_barang.setText(model.getValueAt(rowtb,1).toString());
                    tfharga_barang.setText(model.getValueAt(rowtb,2).toString());
                    tfjumlah_barang.setText(model.getValueAt(rowtb,3).toString());
                    tfsubtotal_brg.setText(model.getValueAt(rowtb,4).toString());
                }
                id_barang = getid_barang();
            }
        });
        cbid_barang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                id_barang = cbid_barang.getSelectedItem().toString();
                try{
                    connectorDB.statement = connectorDB.koneksi.createStatement();
                    String query = ("SELECT * FROM barang WHERE id_barang = '"+id_barang+"'");
                    ResultSet resultSet = connectorDB.statement.executeQuery(query);
                    while (resultSet.next()){
                        tfnama_barang.setText(resultSet.getString(2));
                        tfharga_barang.setText(resultSet.getString(4));
                    }
                }catch (SQLException er){
                    JOptionPane.showMessageDialog(null, "Gagal Terhubung Databse, Periksa Query!");
                }
            }
        });

        tfBayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                    String bayar = Character.toString(e.getKeyChar());
                    if(bayarTemp == null){
                        bayarTemp = bayar;
                    }else{
                        bayarTemp = bayarTemp+bayar;
                    }

                int kembalian =  Integer.parseInt(bayarTemp) - Integer.parseInt(getTotal());
                tfKembalian.setText(String.valueOf(kembalian));
            }
        });
    }

    private void setCbid_barang(){
        try{
            connectorDB.statement = connectorDB.koneksi.createStatement();

            String query = ("SELECT * FROM barang");
            ResultSet resultSet = connectorDB.statement.executeQuery(query);
            while (resultSet.next()){
                cbid_barang.addItem(resultSet.getString(1));
            }
        }catch (SQLException er){
            JOptionPane.showMessageDialog(null, "Gagal Terhubung Databse, Periksa Query!");
        }
    }

    private void resetData(){
        tfnama_barang.setText(null);
        tfharga_barang.setText(null);
        tfjumlah_barang.setText(null);
        tfsubtotal_brg.setText(null);
        tfTotal.setText(null);
        tfBayar.setText(null);
        tfKembalian.setText(null);
        dateChooser.setDate(null);
        totalLb.setText(null);
        bayarTemp = null;
        total = 0;
        rowtb = -1;

        int row = tbdetail_transaksi.getRowCount();
        for (int a = 0; a < row; a++){
            model.removeRow(a);
        }
    }

    private void resetDataBarang(){
        tfnama_barang.setText(null);
        tfharga_barang.setText(null);
        tfjumlah_barang.setText(null);
        tfsubtotal_brg.setText(null);
    }

    private String cekId_transaksi(){
        try{
            connectorDB.statement = connectorDB.koneksi.createStatement();
            String query = ("SELECT * FROM transaksi ");
            ResultSet resultSet = connectorDB.statement.executeQuery(query);
            while (resultSet.next()){
                id_transaksi = Integer.parseInt(resultSet.getString(1));
            }
            if(id_transaksi == null){
                id_transaksi = 1;
            } else {
                id_transaksi = id_transaksi + 1;
            }


        }catch (SQLException er){
            JOptionPane.showMessageDialog(null, "Gagal Terhubung Databse, Periksa Query!");
        }

        return id_transaksi.toString();
    }

    private String getId_transaksi(){
        return tfid_transaksi.getText().toString();
    }
    private String getTgl_transaksi(){
        return df.format(dateChooser.getDate());
    }
    private String getid_barang(){
        return cbid_barang.getSelectedItem().toString();
    }
    private String getNama_brg(){
        return tfnama_barang.getText().toString();
    }
    private String getHarga_brg(){
        return tfharga_barang.getText().toString();
    }
    private String getJumlah(){
        return tfjumlah_barang.getText().toString();
    }
    private String getSubtotal(){
        return tfsubtotal_brg.getText().toString();
    }
    private String getTotal(){
        return tfTotal.getText().toString();
    }
    private String getBayar(){
        return tfBayar.getText().toString();
    }
}
