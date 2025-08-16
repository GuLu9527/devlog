import request from "@/utils/request";

/**
 * 用户登录
 * @param {Object} loginForm - 登录表单数据
 * @param {string} loginForm.username - 用户名
 * @param {string} loginForm.password - 密码
 */
export const loginApi = (loginForm) => request.post("/auth/login", loginForm);

/**
 * 用户注册
 * @param {Object} registerForm - 注册表单数据
 */
export const registerApi = (registerForm) => request.post("/auth/register", registerForm);

/**
 * 用户登出
 */
export const logoutApi = () => request.post("/auth/logout");
