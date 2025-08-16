import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码重置测试工具
 * 用于生成BCrypt加密密码
 */
@SpringBootTest
public class PasswordResetTest {

    @Test
    public void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成常用密码的加密值
        String[] passwords = {"123456", "admin", "password", "123123"};
        
        System.out.println("=== 密码加密结果 ===");
        for (String password : passwords) {
            String encoded = encoder.encode(password);
            System.out.println("原密码: " + password);
            System.out.println("加密后: " + encoded);
            System.out.println("验证结果: " + encoder.matches(password, encoded));
            System.out.println("---");
        }
        
        // 特别生成123456的加密值用于重置
        String resetPassword = "123456";
        String encoded = encoder.encode(resetPassword);
        System.out.println("=== 管理员密码重置 ===");
        System.out.println("重置密码: " + resetPassword);
        System.out.println("SQL更新语句:");
        System.out.println("UPDATE t_user SET password = '" + encoded + "' WHERE username = 'admin';");
    }
    
    @Test 
    public void verifyExistingPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 验证数据库中现有的加密密码
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIy9TgfpK9pnhKJnEgqVKHNE.2";
        String[] testPasswords = {"123456", "admin", "password", "123123"};
        
        System.out.println("=== 验证现有密码 ===");
        System.out.println("数据库中的密码哈希: " + existingHash);
        
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, existingHash);
            System.out.println("密码 '" + password + "' 匹配结果: " + matches);
            if (matches) {
                System.out.println("✅ 找到了！当前管理员密码是: " + password);
            }
        }
    }
}