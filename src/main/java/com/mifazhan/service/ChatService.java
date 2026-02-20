package com.mifazhan.service;

/**
 * AI 聊天服务
 */
public interface ChatService {

    /**
     * 流式对话
     *
     * @param sessionId    会话ID，新会话时可以为 null
     * @param userMessage  用户输入消息内容
     * @param chunkCallback 流式片段回调
     * @param doneCallback  完成回调
     */
    void chatStream(Long sessionId, String userMessage, ChunkCallback chunkCallback, DoneCallback doneCallback, ErrorCallback errorCallback);

    /**
     * 流式片段回调接口
     */
    @FunctionalInterface
    interface ChunkCallback {
        /**
         * 处理 AI 返回的单个片段
         *
         * @param chunk 片段内容
         */
        void onChunk(String chunk);
    }

    /**
     * 完成回调接口
     */
    @FunctionalInterface
    interface DoneCallback {
        /**
         * 当本轮对话的 AI 回复全部完成时调用
         */
        void onDone();
    }

    @FunctionalInterface
    interface ErrorCallback {
        /**
         * 当对话过程中发生错误时调用
         * @param error 错误信息
         */
        void onError(String error);
    }
}

