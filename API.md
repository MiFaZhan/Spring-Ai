# Spring AI 聊天应用 API 文档

本文档详细描述了 Spring AI 聊天应用的所有 API 接口。

## 基础信息

- **基础 URL**: `http://localhost:8080`
- **内容类型**: `application/json`
- **字符编码**: `UTF-8`

## ⚠️ 重要配置说明

**使用本API前，必须正确配置应用！**

项目使用环境变量配置，克隆后需要修改 `application.yml` 中的默认值：

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:mysql://localhost:3306/spring_ai}
    username: ${DATABASE_USERNAME:root}
    password: ${DATABASE_PASSWORD:your_real_password}  # 修改为您的真实密码
  
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your_real_api_key}      # 修改为您的真实API密钥
```

**配置验证**：修改配置后运行 `mvn spring-boot:run` 检查是否配置正确。

## 统一响应格式

所有 API 接口都返回统一的响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**字段说明**:
- `code`: 状态码（200=成功，其他=错误）
- `message`: 响应消息
- `data`: 响应数据

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 聊天接口

### 1. 流式聊天

发送消息并获取流式响应。

**接口**: `POST /api/chat/stream`

**Content-Type**: `application/json`
**Accept**: `text/event-stream`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |
| content | String | 是 | 消息内容 |

**请求示例**:
```json
{
  "sessionId": 1,
  "content": "你好，请介绍一下自己"
}
```

**响应格式**: Server-Sent Events (SSE)

**事件类型**:
- `data`: 包含 AI 回复的文本块
- `complete`: 表示流式响应结束

**响应示例**:
```
event: data
data: {"content": "你好！我是"}

event: data
data: {"content": "一个智能助手"}

event: complete
data: {}
```

**注意事项**:
- 客户端需要实现 SSE 事件监听
- 连接超时时间为无限（0L）
- 使用独立的线程池处理，避免阻塞

## 会话管理接口

### 1. 创建会话

创建一个新的聊天会话。

**接口**: `POST /api/session`

**请求参数**: 无

**响应示例**:
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

### 2. 获取会话列表

获取所有会话的列表。

**接口**: `GET /api/session`

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sessionId": 1,
      "title": "关于人工智能的讨论",
      "creationTime": "2024-01-01 10:00:00",
      "updateTime": "2024-01-01 10:30:00"
    },
    {
      "sessionId": 2,
      "title": "技术问题咨询",
      "creationTime": "2024-01-01 11:00:00",
      "updateTime": "2024-01-01 11:15:00"
    }
  ]
}
```

### 3. 删除会话

删除指定的会话（逻辑删除）。

**接口**: `DELETE /api/session/{sessionId}`

**路径参数**:
- `sessionId`: 会话ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

## 消息管理接口

### 1. 获取会话消息

获取指定会话的所有消息。

**接口**: `GET /api/message/{sessionId}`

**路径参数**:
- `sessionId`: 会话ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "sessionId": 1,
      "role": "user",
      "content": "你好，请介绍一下自己",
      "creationTime": "2024-01-01 10:00:00"
    },
    {
      "id": 2,
      "sessionId": 1,
      "role": "assistant",
      "content": "你好！我是一个智能助手，可以回答各种问题。",
      "creationTime": "2024-01-01 10:00:01"
    }
  ]
}
```

## 数据模型

### SessionDTO (会话数据传输对象)

```json
{
  "sessionId": 1,
  "title": "会话标题",
  "creationTime": "2024-01-01 10:00:00",
  "updateTime": "2024-01-01 10:30:00"
}
```

### ChatMessageDTO (聊天消息数据传输对象)

```json
{
  "sessionId": 1,
  "content": "消息内容"
}
```

### MessageDTO (消息数据传输对象)

```json
{
  "id": 1,
  "sessionId": 1,
  "role": "user",
  "content": "消息内容",
  "creationTime": "2024-01-01 10:00:00"
}
```

## 客户端实现示例

### JavaScript SSE 客户端

```javascript
// 流式聊天
function chatStream(sessionId, content, onData, onComplete, onError) {
  const eventSource = new EventSource(`/api/chat/stream`);
  
  eventSource.onmessage = function(event) {
    const data = JSON.parse(event.data);
    if (data.type === 'data') {
      onData(data.content);
    } else if (data.type === 'complete') {
      onComplete();
      eventSource.close();
    }
  };
  
  eventSource.onerror = function(error) {
    onError(error);
    eventSource.close();
  };
  
  // 发送消息
  fetch('/api/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      sessionId: sessionId,
      content: content
    })
  });
}
```

### 使用示例

```javascript
chatStream(
  1, 
  "你好，请介绍一下自己",
  (chunk) => {
    console.log('收到数据:', chunk);
    // 更新 UI
  },
  () => {
    console.log('对话完成');
  },
  (error) => {
    console.error('发生错误:', error);
  }
);
```

## 错误处理

### 常见错误

1. **会话不存在**: 返回 400 错误
2. **API 密钥错误**: 返回 500 错误
3. **网络连接超时**: 检查 AI 服务提供商状态

### 异常响应格式

```json
{
  "code": 400,
  "message": "会话不存在",
  "data": null
}
```

## 性能建议

1. **流式响应**: 使用 SSE 而非传统 HTTP 请求以获得更好的用户体验
2. **连接复用**: 客户端应复用 SSE 连接
3. **错误重试**: 实现适当的错误重试机制
4. **超时处理**: 设置合理的超时时间