import org.example.controller.review.UserInfoController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class UserValidatorTest {

    private final UserInfoController validator = new UserInfoController();

    @Test
    void testValidName() {
        assertTrue(validator.isValidName("Nguyễn Văn A"), "Tên hợp lệ nhưng bị từ chối");
        assertFalse(validator.isValidName("123 John"), "Tên không hợp lệ nhưng được chấp nhận");
        assertFalse(validator.isValidName(null), "Tên null không nên hợp lệ");
    }

    @Test
    void testValidEmail() {
        assertTrue(validator.isValidEmail("test@example.com"), "Email hợp lệ nhưng bị từ chối");
        assertFalse(validator.isValidEmail("test@@example..com"), "Email không hợp lệ nhưng được chấp nhận");
        assertFalse(validator.isValidEmail(null), "Email null không nên hợp lệ");
    }

    @Test
    void testValidAge() {
        assertTrue(validator.isValidAge(25), "Tuổi hợp lệ nhưng bị từ chối");
        assertFalse(validator.isValidAge(-5), "Tuổi âm không nên hợp lệ");
        assertFalse(validator.isValidAge(201), "Tuổi vượt giới hạn không nên hợp lệ");
    }
}
