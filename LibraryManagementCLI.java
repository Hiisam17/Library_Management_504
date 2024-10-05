import java.util.Scanner;

/**
 * Lớp LibraryManagementCLI đại diện cho giao diện dòng lệnh của hệ thống quản lý thư viện.
 * Cung cấp các chức năng để thêm, xóa, sửa, tìm kiếm và hiển thị tài liệu.
 */
public class LibraryManagementCLI {

    /**
     * Phương thức chính để khởi chạy hệ thống quản lý thư viện.
     *
     * @param args Tham số đầu vào từ dòng lệnh (không sử dụng trong trường hợp này).
     */
    public static void main(String[] args) {
        Library library = new Library();  // Khởi tạo một thư viện mới
        Scanner scanner = new Scanner(System.in);  // Tạo đối tượng Scanner để đọc đầu vào từ người dùng
        boolean exit = false;  // Biến điều kiện để thoát khỏi vòng lặp

        while (!exit) {
            // Hiển thị menu lựa chọn
            System.out.println("Hệ thống quản lý thư viện");
            System.out.println("1. Thêm tài liệu");
            System.out.println("2. Xóa tài liệu");
            System.out.println("3. Sửa tài liệu");
            System.out.println("4. Tìm kiếm tài liệu");
            System.out.println("5. Hiển thị tất cả tài liệu");
            System.out.println("6. Thoát");
            System.out.print("Chọn một tùy chọn: ");

            int choice = scanner.nextInt();  // Đọc tùy chọn từ người dùng
            scanner.nextLine();  // Xóa bộ đệm

            // Xử lý tùy chọn của người dùng
            switch (choice) {
                case 1:
                    // Thêm tài liệu
                    System.out.print("Nhập ID tài liệu: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();  // Xóa bộ đệm
                    System.out.print("Nhập tiêu đề tài liệu: ");
                    String title = scanner.nextLine();
                    System.out.print("Nhập tác giả tài liệu: ");
                    String author = scanner.nextLine();
                    System.out.print("Nhập năm xuất bản tài liệu: ");
                    int year = scanner.nextInt();
                    Document doc = new Document(id, title, author, year);  // Tạo tài liệu mới
                    library.addDocument(doc);  // Thêm tài liệu vào thư viện
                    break;
                case 2:
                    // Xóa tài liệu
                    System.out.print("Nhập ID tài liệu để xóa: ");
                    int removeId = scanner.nextInt();
                    library.removeDocument(removeId);  // Xóa tài liệu khỏi thư viện
                    break;
                case 3:
                    // Sửa tài liệu
                    System.out.print("Nhập ID tài liệu để sửa: ");
                    int editId = scanner.nextInt();
                    scanner.nextLine();  // Xóa bộ đệm
                    System.out.print("Nhập tiêu đề mới: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Nhập tác giả mới: ");
                    String newAuthor = scanner.nextLine();
                    System.out.print("Nhập năm xuất bản mới: ");
                    int newYear = scanner.nextInt();
                    library.editDocument(editId, newTitle, newAuthor, newYear);  // Cập nhật tài liệu
                    break;
                case 4:
                    // Tìm kiếm tài liệu
                    System.out.print("Nhập tiêu đề tài liệu để tìm kiếm: ");
                    String searchTitle = scanner.nextLine();
                    Document found = library.searchDocument(searchTitle);  // Tìm kiếm tài liệu
                    if (found != null) {
                        System.out.println("Tài liệu được tìm thấy: " + found);
                    }
                    break;
                case 5:
                    // Hiển thị tất cả tài liệu
                    library.displayDocuments();
                    break;
                case 6:
                    // Thoát khỏi chương trình
                    exit = true;
                    break;
                default:
                    // Tùy chọn không hợp lệ
                    System.out.println("Tùy chọn không hợp lệ. Vui lòng thử lại.");
            }
            System.out.println();  // Xuống dòng
        }

        scanner.close();  // Đóng scanner khi kết thúc
    }
}

