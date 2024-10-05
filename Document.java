/**
 * Lớp Document đại diện cho một tài liệu trong thư viện.
 * Bao gồm thông tin về ID, tiêu đề, tác giả, năm xuất bản và tình trạng khả dụng của tài liệu.
 */
class Document {

    private int id;               // ID của tài liệu
    private String title;         // Tiêu đề của tài liệu
    private String author;        // Tác giả của tài liệu
    private int year;             // Năm xuất bản của tài liệu
    private boolean isAvailable;   // Tình trạng khả dụng của tài liệu

    /**
     * Khởi tạo một tài liệu mới với thông tin cơ bản.
     *
     * @param id     ID của tài liệu.
     * @param title  Tiêu đề của tài liệu.
     * @param author Tác giả của tài liệu.
     * @param year   Năm xuất bản của tài liệu.
     */
    public Document(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.isAvailable = true;  // Mặc định tài liệu luôn khả dụng khi được tạo
    }

    // Phương thức truy cập (getter) cho các thuộc tính
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Đặt tình trạng khả dụng của tài liệu.
     *
     * @param isAvailable Tình trạng mới của tài liệu (khả dụng hay không).
     */
    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // Phương thức thiết lập (setter) cho các thuộc tính
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Trả về thông tin của tài liệu dưới dạng chuỗi.
     *
     * @return Thông tin của tài liệu.
     */
    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + title + ", Author: " + author + ", Year: " + year + ", Available: " + isAvailable;
    }
}
