# Spring AI

一个基于 Spring Boot 和 Spring AI 框架构建的接入大模型API应用（仅后端），支持多轮对话、会话管理和流式响应。

## 项目概述

Spring AI 聊天应用是一个现代化的 AI 对话系统，具有以下特性：

- **流式响应**: 使用 Server-Sent Events (SSE) 实现实时流式对话
- **会话管理**: 支持多轮对话和会话历史记录
- **多模型支持**: 可配置支持多种 AI 模型
- **RESTful API**: 提供完整的 REST API 接口
- **数据库持久化**: 使用 MySQL 存储会话和消息数据

## 技术栈

- **后端框架**: Spring Boot 3.5.9
- **AI 框架**: Spring AI 1.1.2
- **数据库**: MySQL + MyBatis-Plus
- **Java 版本**: 21
- **构建工具**: Maven

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.6+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE spring_ai;
```

2. 执行初始化脚本（位于 `sql/spring_ai.sql`）

### 应用配置

#### ⚠️ 重要配置说明

**克隆项目后必须修改配置才能正常运行！**

本项目使用环境变量配置，所有敏感信息都通过环境变量注入。克隆项目后，您需要：

1. **修改 `application.yml` 中的默认值**（推荐新手）
2. **或设置环境变量**（推荐有经验的用户）

#### 方式一：直接修改配置文件（最简单）

**这是最直接的方式，适合初次使用的用户**

编辑 `src/main/resources/application.yml` 文件，修改以下配置项的默认值：

```yaml
spring:
  datasource:
    # 修改为您的数据库连接信息
    url: ${DATABASE_URL:jdbc:mysql://localhost:3306/spring_ai}
    username: ${DATABASE_USERNAME:root}
    password: ${DATABASE_PASSWORD:your_password}
  
  ai:
    openai:
      # 修改为您的AI服务API密钥
      api-key: ${OPENAI_API_KEY:your_api_key_here}
      base-url: ${OPENAI_BASE_URL:https://open.bigmodel.cn/api/paas}
      chat:
        options:
          model: ${OPENAI_MODEL:glm-4.5-flash}
```
##### 不同 AI 厂商配置差异

**重要**：根据选择的 AI 服务提供商，可能需要修改 `completions-path`：

- **智谱AI (GLM模型)**：`/v4/chat/completions`（默认）
- **硅基流动 (DeepSeek模型)**：`/v1/chat/completions`
- **OpenAI官方API**：`/v1/chat/completions`

修改方法：在 `application.yml` 中调整 `completions-path` 的默认值：
```yaml
spring:
  ai:
    openai:
      chat:
        completions-path: ${OPENAI_COMPLETIONS_PATH:/v1/chat/completions}
```

#### 方式二：使用环境变量（更安全）

**适合生产环境和有经验的开发者**

1. 复制环境变量模板：
```bash
cp .env .env
```

2. 编辑 `.env` 文件，设置真实的环境变量：
```bash
# 数据库配置
DATABASE_URL=jdbc:mysql://localhost:3306/spring_ai
DATABASE_USERNAME=root
DATABASE_PASSWORD=your_real_password

# AI服务配置
OPENAI_API_KEY=your_real_api_key
OPENAI_BASE_URL=https://open.bigmodel.cn/api/paas
OPENAI_MODEL=glm-4.5-flash
OPENAI_COMPLETIONS_PATH=/v4/chat/completions
```

3. 设置环境变量：

**命令行方式**：
- **Windows (PowerShell)**: `Get-Content .env | ForEach-Object { if ($_ -match "^([^#].*?)=(.*)") { [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process") } }`
- **Linux/Mac**: `export $(grep -v '^#' .env | xargs)`

**IntelliJ IDEA 方式**：
1. 打开 "Run/Debug Configurations"
2. 选择你的 Spring Boot 应用配置
3. 在 "Environment variables" 字段中添加：
   ```
   DATABASE_URL=jdbc:mysql://localhost:3306/spring_ai
   DATABASE_USERNAME=root
   DATABASE_PASSWORD=your_real_password
   OPENAI_API_KEY=your_real_api_key
   OPENAI_BASE_URL=https://open.bigmodel.cn/api/paas
   OPENAI_COMPLETIONS_PATH=/v4/chat/completions
   OPENAI_MODEL=glm-4.5-flash
   ```
4. 或者点击 "..." 按钮，从 `.env` 文件导入

#### 配置验证

修改配置后，运行应用检查是否配置正确：
```bash
mvn spring-boot:run
```

如果应用正常启动且没有配置错误，说明配置正确。

### 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动。

## API 文档

### 聊天接口

#### 流式聊天

```http
POST /api/chat/stream
Content-Type: application/json
```

**请求体**:
```json
{
  "sessionId": 1,
  "content": "你好，请介绍一下自己"
}
```

**响应**: Server-Sent Events 流式响应

### 会话管理

#### 创建会话

```http
POST /api/session
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": 1,
    "title": "新会话",
    "creationTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-01 10:00:00"
  }
}
```

#### 获取会话列表

```http
GET /api/session
```

#### 删除会话

```http
DELETE /api/session/{sessionId}
```

### 消息管理

#### 获取会话消息

```http
GET /api/message/{sessionId}
```

## 项目结构

```
src/main/java/com/mifazhan/
├── config/           # 配置类
├── controller/       # 控制器层
├── domain/          # 领域模型
│   ├── convert/     # 转换器
│   ├── dto/         # 数据传输对象
│   ├── entity/      # 实体类
│   └── vo/          # 视图对象
├── exception/       # 异常处理
├── mapper/          # 数据访问层
└── service/         # 业务逻辑层
```

## 核心功能

### 1. 流式对话

应用使用 SSE 技术实现实时流式对话，提供更好的用户体验。

### 2. 会话管理

- 自动生成会话标题（基于首轮对话内容）
- 支持会话的增删改查
- 逻辑删除机制

### 3. 消息持久化

- 存储完整的对话历史
- 支持角色区分（user/assistant/system）
- 消息时间戳记录

## 开发指南

### 添加新的 AI 模型

1. 在 `application.yml` 中配置新的模型参数
2. 更新 `ChatController` 中的模型名称常量
3. 重新配置 API 密钥和基础 URL

### 扩展功能

- 添加用户认证和授权
- 实现文件上传和文档分析
- 添加多模态支持（图像、语音）
- 集成更多 AI 模型提供商

## 部署

### 打包应用

```bash
mvn clean package -DskipTests
```

### Docker 部署

创建 Dockerfile：

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/Spring-Ai-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。

## 许可证

本项目采用 MIT 许可证。