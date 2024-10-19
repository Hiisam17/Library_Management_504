package org.example.menubar;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Library {

    // Danh sách tài liệu
    private static List<Document> documents;

    // Hàm khởi tạo
    public Library() {
        documents = new ArrayList<>();
    }

    // Thêm tài liệu vào danh sách
    public void addDocument(Document document) {
        documents.add(document);
    }

    // Xóa tài liệu theo ID
    public static boolean deleteDocumentById(String id) {
        return documents.removeIf(doc -> doc.getId().equals(id));
    }

    // Tìm kiếm tài liệu theo ID
    public Optional<Document> findDocumentById(String id) {
        return documents.stream()
                .filter(doc -> doc.getId().equals(id))
                .findFirst();
    }

    // Sửa thông tin tài liệu theo ID
    public boolean editDocument(String id, String newTitle, String newAuthor, String newPublishedDate) {
        Optional<Document> documentOpt = findDocumentById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            document.setTitle(newTitle);
            document.setAuthor(newAuthor);
            document.setPublishedDate(newPublishedDate);
            return true;
        }
        return false;
    }

    // Tìm kiếm tài liệu theo tiêu đề
    public List<Document> searchDocumentsByTitle(String title) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(doc);
            }
        }
        return result;
    }

    // Lấy toàn bộ danh sách tài liệu
    public List<Document> getAllDocuments() {
        return new ArrayList<>(documents); // Trả về bản sao để tránh thay đổi trực tiếp
    }
}

