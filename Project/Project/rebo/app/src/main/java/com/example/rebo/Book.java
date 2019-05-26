package com.example.rebo;

import java.util.HashMap;

// Tạo đối tượng Sách để add vào List View
public class Book {
    private String biaSach;
    private String tenSach;
    private String tacGia;
    private String ngayXuatBan;
    private String nhaXuatBan;
    private String noiDung;
    private String theLoai;
    private String soLanDoc;

    public Book(){

    }
    public String getBiaSach() {
        return biaSach;
    }

    public void setBiaSach(String biaSach) {
        this.biaSach = biaSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getNgayXuatBan() {
        return ngayXuatBan;
    }

    public void setNgayXuatBan(String ngayXuatBan) {
        this.ngayXuatBan = ngayXuatBan;
    }

    public String getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(String nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }

    public String getSoLanDoc() {
        return soLanDoc;
    }

    public void setSoLanDoc(String soLanDoc) {
        this.soLanDoc = soLanDoc;
    }
}