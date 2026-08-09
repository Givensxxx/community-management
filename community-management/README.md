# 社区便民维护管理系统

## 项目简介

社区便民维护管理系统是一个基于 Spring Boot + Vue3 的社区物业维修管理平台。
系统包含居民报修、维修工单管理、公告通知、数据统计等功能模块，
支持居民、维修工、管理员三种角色。

## 技术栈

### 后端

- Java 8
- Spring Boot 2.7.18
- Spring Security
- MyBatis Plus
- MySQL 8.0
- JWT 认证
- Maven

### 前端

- Vue 3
- Vite
- Element Plus
- Vue Router
- Axios

## 本地运行步骤

### 1. 数据库初始化

```sql
CREATE DATABASE community_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行项目根目录下的 init.sql 文件创建数据表和初始数据。

### 2. 后端启动

```bash
cd community-management
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 3. 前端启动

```bash
cd community-management/frontend
npm install
npm run dev
```

前端服务默认运行在 `http://localhost:3000`

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 维修工 | maintainer | 123456 |

## 部署到 Linux + Nginx

### 1. 打包后端

```bash
cd community-management
mvn clean package -DskipTests
```

### 2. 打包前端

```bash
cd community-management/frontend
npm install
npm run build
```

### 3. 部署到 Nginx

将前端 dist 目录复制到服务器，配置 nginx.conf（参考项目根目录 nginx.conf）。

### 4. 启动后端

```bash
java -jar community-management-1.0.0.jar
```

或使用 systemd 服务管理。
