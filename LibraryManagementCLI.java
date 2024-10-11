
import java.util.List;
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
            System.out.println("1. Tìm kiếm tài liệu theo ISBN");
            System.out.println("2. Xóa tài liệu");
            System.out.println("3. Sửa tài liệu");
            System.out.println("4. Tìm kiếm tài liệu");
            System.out.println("5. Hiển thị tất cả tài liệu");
            System.out.println("6. Thêm người dùng");
            System.out.println("7. Xóa người dùng");
            System.out.println("8. Cập nhật thông tin người dùng");
            System.out.println("9. Mượn tài liệu");
            System.out.println("10. Trả tài liệu");
            System.out.println("11. Hiển thị thông tin người dùng");
            System.out.println("12. Thoát");
            System.out.print("Chọn một tùy chọn: ");

            int choice = scanner.nextInt();  // Đọc tùy chọn từ người dùng
            scanner.nextLine();  // Xóa bộ đệm

            // Xử lý tùy chọn của người dùng
            switch (choice) {
                case 1:
                    System.out.print("Nhập ISBN: ");
                    String isbn = scanner.nextLine();
                    String isbnResult = APIIntegration.getBookInfoByISBN(isbn);
                    APIIntegration.parseBookInfo(isbnResult);
                    break;
                    // no error
                case 2:
                    // Xóa tài liệu
                    System.out.print("Nhập ID tài liệu để xóa: ");
                    int removeId = scanner.nextInt();
                    library.removeDocument(removeId);  // Xóa tài liệu khỏi thư viện
                    break;
                    // no error
                case 3:
                    // Sửa tài liệu
                    System.out.println("Chọn phương thức tìm kiếm để sửa tài liệu:");
                    System.out.println("1. Tìm kiếm theo tên tài liệu");
                    System.out.println("2. Tìm kiếm theo ID tài liệu");
                    int searchChoiceToEdit = scanner.nextInt();
                    scanner.nextLine();  // Xóa bộ đệm

                    Document documentToEdit = null;

                    switch (searchChoiceToEdit) {
                        case 1:
                            // Tìm kiếm theo tên tài liệu
                            System.out.print("Nhập tên tài liệu: ");
                            String searchTitle = scanner.nextLine();
                            documentToEdit = library.searchDocument(searchTitle);
                            break;
                        case 2:
                            // Tìm kiếm theo ID tài liệu
                            System.out.print("Nhập ID tài liệu: ");
                            int searchId = scanner.nextInt();
                            scanner.nextLine();  // Xóa bộ đệm
                            documentToEdit = library.searchDocument(searchId);
                            break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ.");
                            break;
                    }

                    if (documentToEdit != null) {
                        // Nếu tìm thấy tài liệu, yêu cầu người dùng nhập thông tin mới
                        System.out.print("Nhập tiêu đề mới: ");
                        String newTitle = scanner.nextLine();
                        System.out.print("Nhập tác giả mới: ");
                        String newAuthor = scanner.nextLine();
                        System.out.print("Nhập năm xuất bản mới: ");
                        int newYear = scanner.nextInt();

                        // Cập nhật thông tin cho tài liệu
                        documentToEdit.setTitle(newTitle);
                        documentToEdit.setAuthor(newAuthor);
                        documentToEdit.setYear(newYear);

                        System.out.println("Tài liệu đã được cập nhật.");
                    } else {
                        System.out.println("Không tìm thấy tài liệu.");
                    }
                    break;
                    // no error    
                case 4:
                    // Tìm kiếm tài liệu
                    System.out.println("Chọn phương thức tìm kiếm:");
                    System.out.println("1. Tìm kiếm theo tên tài liệu");
                    System.out.println("2. Tìm kiếm theo ID tài liệu");
                    System.out.println("3. Tìm kiếm theo tên tác giả");
                    System.out.println("4. Tìm kiếm theo năm xuất bản");
                    int searchChoice = scanner.nextInt();
                    scanner.nextLine();  // Xóa bộ đệm
                    Document found = null;  // Biến lưu kết quả tìm kiếm (nếu là một tài liệu)

                    switch (searchChoice) {
                        case 1:
                            // Tìm kiếm theo tên tài liệu (trả về một tài liệu duy nhất)
                            System.out.print("Nhập tên tài liệu: ");
                            String searchTitle = scanner.nextLine();
                            found = library.searchDocument(searchTitle);
                            if (found != null) {
                                System.out.println("Tài liệu tìm thấy: " + found);
                            } else {
                                System.out.println("Không tìm thấy tài liệu với tên: " + searchTitle);
                            }
                            break;
                        case 2:
                            // Tìm kiếm theo ID tài liệu (trả về một tài liệu duy nhất)
                            System.out.print("Nhập ID tài liệu: ");
                            int searchId = scanner.nextInt();
                            scanner.nextLine();  // Xóa bộ đệm
                            found = library.searchDocument(searchId);
                            if (found != null) {
                                System.out.println("Tài liệu tìm thấy: " + found);
                            } else {
                                System.out.println("Không tìm thấy tài liệu với ID: " + searchId);
                            }
                            break;
                        case 3:
                            // Tìm kiếm theo tên tác giả (trả về danh sách tài liệu)
                            System.out.print("Nhập tên tác giả: ");
                            String searchAuthor = scanner.nextLine();
                            List<Document> authorDocs = library.getDocumentsByAuthor(searchAuthor);

                            // Kiểm tra nếu có tài liệu nào được tìm thấy
                            if (!authorDocs.isEmpty()) {
                                System.out.println("Các tài liệu của tác giả " + searchAuthor + " là: ");
                                for (Document doc1 : authorDocs) {
                                    System.out.println(doc1);  // In ra thông tin của từng tài liệu
                                }
                            } else {
                                System.out.println("Không tìm thấy tài liệu nào của tác giả: " + searchAuthor);
                            }
                            break;
                        case 4:
                            // Tìm kiếm theo năm xuất bản (trả về danh sách tài liệu)
                            System.out.print("Nhập năm xuất bản: ");
                            int searchYear = scanner.nextInt();
                            scanner.nextLine();  // Xóa bộ đệm

                            List<Document> yearDocs = library.getDocumentsByYear(searchYear);

                            // Kiểm tra nếu có tài liệu nào được tìm thấy
                            if (!yearDocs.isEmpty()) {
                                System.out.println("Các tài liệu được xuất bản trong năm " + searchYear + " là:");
                                for (Document doc2 : yearDocs) {
                                    System.out.println(doc2);  // In ra thông tin của từng tài liệu
                                }
                            } else {
                                System.out.println("Không tìm thấy tài liệu nào xuất bản trong năm: " + searchYear);
                            }
                            break;

                        default:
                            System.out.println("Lựa chọn không hợp lệ.");
                            break;
                    }
                    break;
                    //no error
                case 5:
                    // Hiển thị tất cả tài liệu
                    library.displayDocuments();
                    break;
                //no error
                case 6:
                    System.out.print("Nhập ID người dùng: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nhập tên người dùng: ");
                    String userName = scanner.nextLine();
                    User user = new User(userId, userName);
                    library.addUser(user);
                    break;
                //no error
                case 7:
                    System.out.print("Nhập ID người dùng để xóa: ");
                    int removeUserId = scanner.nextInt();
                    library.removeUser(removeUserId);
                    break;
                //no error
                case 8:
                    System.out.print("Nhập ID người dùng để cập nhật: ");
                    int updateUserId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nhập tên mới: ");
                    String newUserName = scanner.nextLine();
                    System.out.print("Người dùng có thể mượn tài liệu (true/false): ");
                    boolean canBorrow = scanner.nextBoolean();
                    library.updateUser(updateUserId, newUserName, canBorrow);
                    break;
                //no error
                case 9:
                    System.out.print("Nhập ID người dùng: ");
                    int borrowUserId = scanner.nextInt();
                    System.out.print("Nhập ID tài liệu: ");
                    int borrowDocumentId = scanner.nextInt();
                    library.borrowDocument(borrowUserId, borrowDocumentId);
                    break;
                case 10:
                    System.out.print("Nhập ID tài liệu để trả: ");
                    int returnDocumentId = scanner.nextInt();
                    library.returnDocument(returnDocumentId);
                    break;
                case 11:
                    System.out.print("Nhập ID người dùng để hiển thị thông tin: ");
                    int displayUserId = scanner.nextInt();
                    library.displayUserInfo(displayUserId);
                    break;
                case 12:
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

