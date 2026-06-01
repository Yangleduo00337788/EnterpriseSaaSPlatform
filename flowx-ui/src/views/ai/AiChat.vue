<template>
  <div class="ai-chat-container">
    <!-- Conversation Sidebar -->
    <div class="conversation-sidebar">
      <div class="sidebar-header">
        <n-button type="primary" block @click="handleNewConversation">新对话</n-button>
      </div>
      <div class="conversation-list">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          class="conversation-item"
          :class="{ active: currentConversationId === conv.id }"
          @click="switchConversation(conv.id)"
        >
          <div class="conv-title">{{ conv.title || '新对话' }}</div>
          <div class="conv-time">{{ conv.createTime }}</div>
          <n-button class="conv-delete" text type="error" size="tiny" @click.stop="handleDeleteConversation(conv.id)">
            删除
          </n-button>
        </div>
        <n-empty v-if="conversations.length === 0" description="暂无对话" style="margin-top: 40px;" />
      </div>
    </div>

    <!-- Chat Area -->
    <div class="chat-area">
      <!-- Messages -->
      <div class="messages-container" ref="messagesRef">
        <div v-if="messages.length === 0" class="welcome-screen">
          <div class="welcome-icon">AI</div>
          <h2>FlowX AI 助手</h2>
          <p>有什么我可以帮助你的吗？</p>
          <div class="suggestion-chips">
            <n-button v-for="suggestion in suggestions" :key="suggestion" size="small" round @click="sendMessage(suggestion)">
              {{ suggestion }}
            </n-button>
          </div>
        </div>

        <div v-for="(msg, index) in messages" :key="index" class="message-item" :class="msg.role">
          <div class="message-avatar">
            <n-avatar v-if="msg.role === 'user'" :size="36" round style="background-color: #2080f0;">
              {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </n-avatar>
            <n-avatar v-else :size="36" round style="background-color: #18a058;">AI</n-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div v-html="renderMarkdown(msg.content)"></div>
            </div>
          </div>
        </div>

        <div v-if="streaming" class="message-item assistant">
          <div class="message-avatar">
            <n-avatar :size="36" round style="background-color: #18a058;">AI</n-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div v-html="renderMarkdown(streamingContent)"></div>
              <span class="typing-cursor">|</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-area">
        <div class="input-wrapper">
          <n-input
            v-model:value="inputMessage"
            type="textarea"
            placeholder="输入你的问题... (Ctrl+Enter 发送)"
            :autosize="{ minRows: 1, maxRows: 4 }"
            @keydown="handleKeydown"
          />
          <n-button
            type="primary"
            :disabled="!inputMessage.trim() || streaming"
            :loading="streaming"
            @click="sendMessage(inputMessage)"
            style="flex-shrink: 0;"
          >
            发送
          </n-button>
        </div>
        <div class="input-footer">
          <span>AI 生成的内容可能不准确，请注意甄别</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useMessage } from 'naive-ui'
import { chat, getConversations, getConversationHistory, deleteConversation } from '@/api/ai'

const userStore = useUserStore()
const message = useMessage()

interface ChatMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
}

const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const streaming = ref(false)
const streamingContent = ref('')
const currentConversationId = ref('')
const conversations = ref<any[]>([])
const messagesRef = ref<HTMLElement | null>(null)

const suggestions = [
  '如何创建审批流程？',
  '帮我写一个请假申请',
  '查看我的待办任务',
  '系统管理有哪些功能？',
]

function renderMarkdown(text: string): string {
  if (!text) return ''
  return text
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br>')
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

async function sendMessage(content: string) {
  if (!content.trim() || streaming.value) return

  const userMsg: ChatMessage = { role: 'user', content: content.trim() }
  messages.value.push(userMsg)
  inputMessage.value = ''
  scrollToBottom()

  streaming.value = true
  streamingContent.value = ''

  try {
    const res = await chat({
      conversationId: currentConversationId.value || undefined,
      message: content.trim(),
    })

    const assistantMsg: ChatMessage = { role: 'assistant', content: res.data?.content || res.data?.message || '抱歉，我无法处理您的请求。' }
    messages.value.push(assistantMsg)

    if (res.data?.conversationId && !currentConversationId.value) {
      currentConversationId.value = res.data.conversationId
      fetchConversations()
    }
  } catch (e: any) {
    messages.value.push({ role: 'assistant', content: '请求失败，请稍后重试。' })
  } finally {
    streaming.value = false
    streamingContent.value = ''
    scrollToBottom()
  }
}

function handleKeydown(e: KeyboardEvent) {
  if (e.ctrlKey && e.key === 'Enter') {
    e.preventDefault()
    sendMessage(inputMessage.value)
  }
}

function handleNewConversation() {
  currentConversationId.value = ''
  messages.value = []
}

async function switchConversation(id: string) {
  currentConversationId.value = id
  try {
    const res = await getConversationHistory(id)
    messages.value = (res.data || []).map((m: any) => ({ role: m.role, content: m.content }))
    scrollToBottom()
  } catch (e) { /* handled */ }
}

async function fetchConversations() {
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch (e) { /* handled */ }
}

async function handleDeleteConversation(id: string) {
  try {
    await deleteConversation(id)
    if (currentConversationId.value === id) {
      handleNewConversation()
    }
    fetchConversations()
  } catch (e) { /* handled */ }
}

onMounted(() => {
  fetchConversations()
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  height: calc(100vh - 88px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.conversation-sidebar {
  width: 260px;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.sidebar-header {
  padding: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.conversation-item {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 4px;
  position: relative;
  transition: background 0.2s;
}

.conversation-item:hover {
  background: #e8e8e8;
}

.conversation-item.active {
  background: #d0f0d0;
}

.conv-title {
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.conv-delete {
  position: absolute;
  right: 8px;
  top: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.conversation-item:hover .conv-delete {
  opacity: 1;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.welcome-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.welcome-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #18a058, #36ad6a);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

.welcome-screen h2 {
  margin: 0 0 8px;
  color: #333;
}

.welcome-screen p {
  margin: 0 0 20px;
}

.suggestion-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message-item.assistant .message-bubble {
  background: #f5f5f5;
  color: #333;
}

.message-item.user .message-bubble {
  background: #18a058;
  color: white;
}

.message-bubble :deep(code) {
  background: rgba(0, 0, 0, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
}

.typing-cursor {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.input-area {
  border-top: 1px solid #e8e8e8;
  padding: 16px 20px;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-footer {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
  text-align: center;
}
</style>
