import com.giao.devlogpulse.utils.JwtTokenUtil;

public class JwtTokenTest {
    public static void main(String[] args) {
        JwtTokenUtil jwtUtil = new JwtTokenUtil();
        
        // 测试生成token
        String username = "testuser";
        Long userId = 123L;
        
        System.out.println("=== JWT Token 测试 ===");
        System.out.println("输入 - Username: " + username + ", UserId: " + userId);
        
        // 生成token
        String token = jwtUtil.generateToken(username, userId);
        System.out.println("生成的Token: " + token);
        
        // 解析token
        String parsedUsername = jwtUtil.getUsernameFromToken(token);
        Long parsedUserId = jwtUtil.getUserIdFromToken(token);
        
        System.out.println("解析结果 - Username: " + parsedUsername + ", UserId: " + parsedUserId);
        
        // 验证结果
        boolean usernameMatch = username.equals(parsedUsername);
        boolean userIdMatch = userId.equals(parsedUserId);
        
        System.out.println("Username匹配: " + usernameMatch);
        System.out.println("UserId匹配: " + userIdMatch);
        
        if (usernameMatch && userIdMatch) {
            System.out.println("✅ JWT测试通过");
        } else {
            System.out.println("❌ JWT测试失败");
        }
        
        System.out.println("===================");
    }
}