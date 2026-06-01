import request from '@/utils/request'

export interface ChatMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
}

export interface ChatRequest {
  conversationId?: string
  message: string
  model?: string
}

export function chat(data: ChatRequest) {
  return request.post<any, any>('/ai/chat', data)
}

export function chatStream(data: ChatRequest) {
  return request.post<any, any>('/ai/chat/stream', data, {
    responseType: 'stream',
    adapter: 'fetch',
  })
}

export function getConversations() {
  return request.get<any, any>('/ai/conversations')
}

export function getConversationHistory(conversationId: string) {
  return request.get<any, any>(`/ai/conversations/${conversationId}/messages`)
}

export function deleteConversation(conversationId: string) {
  return request.delete<any, any>(`/ai/conversations/${conversationId}`)
}

export function clearConversations() {
  return request.delete<any, any>('/ai/conversations')
}
