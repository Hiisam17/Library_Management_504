import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Library để quản lý danh sách các tài liệu.
 * Cung cấp các chức năng thêm, xóa, sửa, tìm kiếm, và lọc tài liệu theo các tiêu chí khác nhau.
 */
class Library {

    // Danh sách lưu trữ tất cả tài liệu trong thư viện
    private List<Document> documents;

    /**
     * Khởi tạo một thư viện mới với danh sách tài liệu rỗng.
     */
    public Library() {
        documents = new ArrayList<>();
    }

    /**
     * Thêm một tài liệu vào thư viện nếu không có tài liệu nào có ID hoặc tiêu đề trùng.
     *
     * @param document Tài liệu cần thêm vào.
     */
    public void addDocument(Document document) {
        for (Document doc : documents) {
            if (doc.getId() == document.getId() || doc.getTitle().equalsIgnoreCase(document.getTitle())) {
                System.out.println("Tài liệu có ID hoặc tiêu đề trùng đã tồn tại.");
                return;
            }
        }
        documents.add(document);
        System.out.println("Thêm tài liệu thành công.");
    }

    /**
     * Xóa một tài liệu khỏi thư viện dựa trên ID của tài liệu.
     *
     * @param documentId ID của tài liệu cần xóa.
     */
    public void removeDocument(int documentId) {
        Document toRemove = null;
        for (Document doc : documents) {
            if (doc.getId() == documentId) {
                toRemove = doc;
                break;
            }
        }
        if (toRemove != null) {
            documents.remove(toRemove);
            System.out.println("Xóa tài liệu thành công.");
        } else {
            System.out.println("Không tìm thấy tài liệu.");
        }
    }

    /**
     * Sửa tiêu đề, tác giả, và năm xuất bản của một tài liệu dựa trên ID của tài liệu.
     *
     * @param documentId ID của tài liệu cần sửa.
     * @param newTitle   Tiêu đề mới của tài liệu.
     * @param newAuthor  Tác giả mới của tài liệu.
     * @param newYear    Năm xuất bản mới của tài liệu.
     */
    public void editDocument(int documentId, String newTitle, String newAuthor, int newYear) {
        for (Document doc : documents) {
            if (doc.getId() == documentId) {
                doc.setTitle(newTitle);
                doc.setAuthor(newAuthor);
                doc.setYear(newYear);
                System.out.println("Cập nhật tài liệu thành công.");
                return;
            }
        }
        System.out.println("Không tìm thấy tài liệu.");
    }

    /**
     * Tìm kiếm một tài liệu dựa trên tiêu đề (không phân biệt hoa thường).
     *
     * @param title Tiêu đề của tài liệu cần tìm.
     * @return Trả về tài liệu nếu tìm thấy, ngược lại trả về null.
     */
    public Document searchDocument(String title) {
        for (Document doc : documents) {
            if (doc.getTitle().equalsIgnoreCase(title)) {
                return doc;
            }
        }
        System.out.println("Không tìm thấy tài liệu.");
        return null;
    }

    /**
     * Tìm kiếm một tài liệu dựa trên ID.
     *
     * @param id ID của tài liệu cần tìm.
     * @return Trả về tài liệu nếu tìm thấy, ngược lại trả về null.
     */
    public Document searchDocument(int id) {
        for (Document doc : documents) {
            if (doc.getId() == id) {
                return doc;
            }
        }
        System.out.println("Không tìm thấy tài liệu.");
        return null;
    }

    /**
     * Lấy danh sách các tài liệu của một tác giả cụ thể.
     *
     * @param author Tên tác giả của các tài liệu cần lấy.
     * @return Trả về danh sách các tài liệu của tác giả chỉ định, hoặc danh sách rỗng nếu không tìm thấy.
     */
    public List<Document> getDocumentsByAuthor(String author) {
        List<Document> result = new ArrayList<>();

        for (Document doc : documents) {
            if (doc.getAuthor().equalsIgnoreCase(author)) {
                result.add(doc);
            }
        }

        if (result.isEmpty()) {
            System.out.println("Không tìm thấy tài liệu cho tác giả: " + author);
        }

        return result;
    }

    /**
     * Lấy danh sách các tài liệu xuất bản trong một năm cụ thể.
     *
     * @param year Năm xuất bản của các tài liệu cần lấy.
     * @return Trả về danh sách các tài liệu xuất bản trong năm chỉ định, hoặc danh sách rỗng nếu không tìm thấy.
     */
    public List<Document> getDocumentsByYear(int year) {
        List<Document> result = new ArrayList<>();

        for (Document doc : documents) {
            if (doc.getYear() == year) {
                result.add(doc);
            }
        }

        if (result.isEmpty()) {
            System.out.println("Không tìm thấy tài liệu cho năm: " + year);
        }

        return result;
    }

    /**
     * Hiển thị tất cả các tài liệu hiện có trong thư viện.
     * In ra thông báo nếu không có tài liệu nào.
     */
    public void displayDocuments() {
        if (documents.isEmpty()) {
            System.out.println("Không có tài liệu nào trong thư viện.");
            return;
        }
        for (Document doc : documents) {
            System.out.println(doc);
        }
    }
}