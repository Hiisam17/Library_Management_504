import java.util.ArrayList;
import java.util.List;

// Lớp đại diện cho một người dùng trong hệ thống thư viện
class User {
  private int id; // Mã định danh duy nhất cho người dùng
  private String name; // Tên của người dùng
  private boolean canBorrow; // Biến chỉ định người dùng có thể mượn tài liệu hay không
    private List<Document> borrowedDocuments = new ArrayList<>(); // Danh sách tài liệu mà người dùng đã mượn

  public User(int id, String name) {
    this.id = id;
    this.name = name;
    this.canBorrow = true; // Mặc định người dùng có thể mượn tài liệu
  }

  // Getter cho ID người dùng
  public int getId() {
    return id;
  }

  // Getter cho tên người dùng
  public String getName() {
    return name;
  }

  // Getter để kiểm tra người dùng có thể mượn tài liệu hay không
  public boolean canBorrow() {
    return canBorrow;
  }

  // Setter để cập nhật tên người dùng
  public void setName(String name) {
    this.name = name;
  }

  // Setter để cập nhật quyền mượn tài liệu của người dùng
  public void setCanBorrow(boolean canBorrow) {
    this.canBorrow = canBorrow;
  }

  public void displayInfo() {
    System.out.println("User ID: " + id);
    System.out.println("Name: " + name);
    System.out.println("Borrowed Documents:");
    for (Document doc : borrowedDocuments) {
      System.out.println("- " + doc.getTitle());
    }
  }

  // Ghi
  @Override
  public String toString() {
    return "ID: " + id + ", Name: " + name + ", Can Borrow: " + canBorrow;
  }
}