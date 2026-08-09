# 社区便民维护管理系统 - 免费线上部署指南

> 部署方案：Vercel（前端）+ Render（后端）+ TiDB Cloud（数据库）+ Upstash（Redis）

---

## 目录

1. [准备工作](#第一步准备工作)
2. [推送到 GitHub](#第二步推送到-github)
3. [创建 TiDB Cloud 数据库](#第三步创建-tidb-cloud-数据库)
4. [创建 Upstash Redis](#第四步创建-upstash-redis)
5. [部署后端到 Render](#第五步部署后端到-render)
6. [部署前端到 Vercel](#第六步部署前端到-vercel)
7. [修改前端 API 地址](#第七步修改前端-api-地址并重新部署)
8. [验证部署](#第八步验证部署)
9. [常见问题](#常见问题)

---

## 第一步：准备工作

### 需要注册的账号

| 平台 | 网址 | 用途 |
|------|------|------|
| GitHub | https://github.com | 代码仓库 |
| TiDB Cloud | https://tidbcloud.com | 免费 MySQL 数据库 |
| Upstash | https://upstash.com | 免费 Redis |
| Render | https://render.com | 免费后端部署 |
| Vercel | https://vercel.com | 免费前端部署 |

### 本机需要安装的软件

- **Git**（用于推送代码到 GitHub）
  - 下载：https://git-scm.com/downloads
  - 安装后验证：`git --version`

---

## 第二步：推送到 GitHub

### 2.1 在 GitHub 创建仓库

1. 打开 https://github.com，登录账号
2. 点击右上角 **+** 号 → **New repository**
3. 填写信息：
   - **Repository name**: `community-management`
   - **Description**: 社区便民维护管理系统
   - **Public**（公开）或 **Private**（私有）
   - 不要勾选 "Add a README file"
4. 点击 **Create repository**
5. 创建后，页面会显示类似下面的命令（**先不要关闭这个页面**）：

```bash
git remote add origin https://github.com/你的用户名/community-management.git
git branch -M main
git push -u origin main
```

### 2.2 把本地代码推上去

1. 打开项目文件夹 `C:\Users\sunnyching\Desktop\community-management`
2. 在文件夹内右键 → **Git Bash Here**（如果没有，打开 CMD 或 PowerShell，cd 到项目目录）
3. 按顺序执行以下命令：

```bash
# 初始化 Git 仓库
git init

# 添加所有文件
git add .

# 提交（备注可以改）
git commit -m "初始化项目，适配线上部署"

# 关联远程仓库（把下面的 URL 换成你 GitHub 仓库的真实地址）
git remote add origin https://github.com/你的用户名/community-management.git

# 推送到 main 分支
git branch -M main
git push -u origin main
```

4. 刷新 GitHub 页面，确认代码已上传

---

## 第三步：创建 TiDB Cloud 数据库

### 3.1 注册并创建集群

1. 访问 https://tidbcloud.com
2. 点击 **Sign Up**，用邮箱注册（推荐用 Gmail 或企业邮箱）
3. 注册完成后，点击 **Create Cluster**
4. 选择方案：
   - **Serverless**（免费，5GB 存储）
5. 配置集群：
   - **Cluster Name**: `community-db`
   - **Region**: 选择 `Singapore (AWS)` 或 `Tokyo (AWS)`（离你最近的）
   - **Engine**: `TiDB Serverless`
6. 点击 **Create**
7. 等待集群状态变为 **Available**（约 1-2 分钟）

### 3.2 设置连接密码

1. 在集群列表中，点击你创建的集群
2. 点击 **Connect** 按钮
3. 选择 **Public** 标签
4. 在 **Password** 栏，点击 **Set Password**，设置一个密码（记下来！）

### 3.3 开放 IP 白名单

1. 点击左侧 **Security** → **Network**
2. 点击 **Add IP Address**
3. 输入：`0.0.0.0/0`
4. 点击 **Confirm**

> 这表示允许任何 IP 连接。生产环境建议只添加 Render 的 IP。

### 3.4 获取连接信息

在 **Connect** 页面，复制以下信息（记下来，等下要用）：

- **Host**: 类似 `gateway01.ap-southeast-1.prod.aws.tidbcloud.com`
- **Port**: `4000`
- **User**: `root`
- **Password**: 你刚才设置的密码

完整连接字符串格式：
```
jdbc:mysql://gateway01.xxx.tidbcloud.com:4000/community_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=true
```

### 3.5 初始化数据库表

1. 下载 MySQL 客户端（如 DBeaver：https://dbeaver.io/download/）
2. 新建连接：
   - **Host**: TiDB 的 Host
   - **Port**: `4000`
   - **Database**: `community_db`
   - **User**: `root`
   - **Password**: 你设置的密码
3. 连接成功后，打开 `init.sql` 文件，全选执行
4. 确认表已创建：`sys_user`、`repair_category`、`repair_order` 等

---

## 第四步：创建 Upstash Redis

1. 访问 https://upstash.com，用 GitHub 账号登录
2. 点击 **Create Database**
3. 配置：
   - **Database Name**: `community-redis`
   - **Region**: 选择和 TiDB 相同的区域（如 Singapore）
   - **Plan**: 选择 **Free**
4. 点击 **Create**
5. 进入数据库详情页，复制以下信息（记下来）：
   - **Endpoint**（Host）: 类似 `faithful-wren-12345.upstash.io`
   - **Port**: `6379`
   - **Password**: 点击 **Show** 查看

---

## 第五步：部署后端到 Render

### 5.1 创建 Web Service

1. 访问 https://render.com，用 GitHub 登录
2. 点击 Dashboard 上的 **New +** → **Web Service**
3. 找到你的 GitHub 仓库 `community-management`，点击 **Connect**
4. 填写配置：

| 配置项 | 值 |
|--------|-----|
| **Name** | `community-api` |
| **Region** | `Singapore` |
| **Branch** | `main` |
| **Runtime** | `Java` |
| **Build Command** | `mvn clean package -DskipTests` |
| **Start Command** | `java -jar target/community-management-1.0.0.jar` |
| **Plan** | `Free` |

5. 点击 **Advanced** 展开高级选项
6. 添加环境变量（**Environment Variables**）：

点击 **Add Environment Variable**，逐条添加：

| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://[你的TiDB Host]:4000/community_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=true&requireSSL=true` |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | `[你的TiDB密码]` |
| `SPRING_REDIS_HOST` | `[你的Upstash Host]` |
| `SPRING_REDIS_PORT` | `6379` |
| `SPRING_REDIS_PASSWORD` | `[你的Upstash密码]` |
| `JWT_SECRET` | `communitySecretKey2024communitySecretKey2024` |
| `JWT_EXPIRATION` | `86400000` |

> 把方括号内容替换成你的真实值

7. 点击 **Create Web Service**
8. 等待构建完成（约 3-5 分钟）
9. 构建成功后，页面会显示类似 `https://community-api.onrender.com` 的 URL

### 5.2 测试后端是否正常运行

在浏览器访问：
```
https://community-api.onrender.com/api/health
```

如果返回 JSON 数据（如 `{"code":200}`），说明部署成功。

> 如果报 404，说明没有 health 接口，可以尝试访问登录接口看是否报错。

---

## 第六步：部署前端到 Vercel

### 6.1 创建项目

1. 访问 https://vercel.com，用 GitHub 登录
2. 点击 **Add New...** → **Project**
3. 在 **Import Git Repository** 中，找到 `community-management`，点击 **Import**
4. 配置项目：

| 配置项 | 值 |
|--------|-----|
| **Project Name** | `community-web` |
| **Framework Preset** | `Vite` |
| **Root Directory** | `frontend` |
| **Build Command** | `npm run build` |
| **Output Directory** | `dist` |

5. 点击 **Environment Variables**，添加：

| Key | Value |
|-----|-------|
| `VITE_API_BASE_URL` | `https://[你的Render域名]/api` |

> 例如：`https://community-api.onrender.com/api`

6. 点击 **Deploy**
7. 等待构建完成（约 1-2 分钟）
8. 构建成功后，会显示类似 `https://community-web.vercel.app` 的域名

---

## 第七步：修改前端 API 地址并重新部署

如果你第六步已经填对了 API 地址，这一步可以跳过。

如果后来 Render 的域名变了，或者地址填错了：

1. 修改 `frontend/.env.production` 文件：
```
VITE_API_BASE_URL=https://你的真实Render域名/api
```

2. 提交并推送：
```bash
git add .
git commit -m "更新 API 地址"
git push
```

3. Vercel 会自动重新部署（因为绑定了 GitHub 仓库）

---

## 第八步：验证部署

### 8.1 前端页面检查

1. 打开 Vercel 分配的域名（如 `https://community-web.vercel.app`）
2. 确认页面正常显示
3. 按 F12 打开开发者工具 → **Network** 标签
4. 尝试登录，查看 API 请求是否成功（Status 200）

### 8.2 后端 API 检查

1. 打开 Render 分配的域名 + `/api`
2. 或者直接用 Postman/Apifox 测试接口

### 8.3 常见问题排查

| 现象 | 原因 | 解决 |
|------|------|------|
| 页面空白 | 前端构建失败 | 查看 Vercel Build Logs |
| 登录报 Network Error | 后端没启动或 CORS 问题 | 检查 Render 状态 + 确认 CORS 配置 |
| 数据库连接失败 | TiDB 密码错误或 IP 未开放 | 检查环境变量 + TiDB Network 设置 |
| 第一次访问很慢（30秒+）| Render 休眠了 | 正常现象，等待唤醒 |

---

## 常见问题

### Q1: Render 免费版有什么限制？

- 15 分钟无访问会自动休眠
- 下次访问需要等待 30 秒左右唤醒
- 每月 750 小时免费

**解决方法**：用 [UptimeRobot](https://uptimerobot.com) 每 5 分钟 Ping 一次，保持活跃。

### Q2: 图片上传后不见了？

Render 免费版的磁盘不是持久化的，重启后文件会丢失。

**解决方法**：
- 使用 Cloudinary（免费 25GB）
- 或阿里云 OSS、腾讯云 COS

### Q3: 我想用自定义域名？

- **Vercel**: Settings → Domains → 添加你的域名
- **Render**: Settings → Custom Domains → 添加你的域名

### Q4: 如何查看日志？

- **Render**: 进入 Web Service → Logs 标签
- **Vercel**: 进入 Project → Deployments → 点击部署记录 → View Logs

### Q5: 如何更新代码？

1. 本地修改代码
2. `git add .` → `git commit -m "xxx"` → `git push`
3. Render 和 Vercel 会自动重新部署

---

## 部署完成后的文件结构

```
community-management/
├── src/main/resources/
│   └── application-prod.yml    # 已修改为支持环境变量
├── src/main/java/com/community/config/
│   └── WebConfig.java            # 已修复 CORS
├── src/main/java/com/community/security/
│   └── SecurityConfig.java       # 已修复 CORS
├── frontend/
│   ├── src/api/request.js        # 已支持环境变量
│   ├── .env                     # 本地开发 API 地址
│   └── .env.production          # 生产环境 API 地址
├── DEPLOYMENT_GUIDE.md          # 本文件
└── ...
```

---

## 需要帮助？

如果在某一步卡住了，告诉我：
1. 你正在做哪一步？
2. 遇到了什么错误？（截图或复制错误信息）
3. 你期望的结果是什么？

我可以帮你继续排查。
