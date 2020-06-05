package umn.ac.id.mahasiswaque;

public class Mahasiswa {
    private String nama;
    private String nim;
    private String prodi;
    private Long angkatan;
    private String biografi;
    private String profilePhoto;

    public Mahasiswa(String nama, String nim, String prodi, Long angkatan, String biografi, String profilePhoto) {
        this.nama = nama;
        this.nim = nim;
        this.prodi = prodi;
        this.angkatan = angkatan;
        this.biografi = biografi;
        this.profilePhoto = profilePhoto;
    }

    public Mahasiswa() {

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) {
        this.prodi = prodi;
    }

    public Long getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(Long angkatan) {
        this.angkatan = angkatan;
    }

    public String getBiografi() {
        return biografi;
    }

    public void setBiografi(String biografi) {
        this.biografi = biografi;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
