import request from '@/utils/request'

export interface WorkflowQuery {
  pageNum?: number
  pageSize?: number
  name?: string
  key?: string
  category?: string
}

export function getDefinitions(params: WorkflowQuery) {
  return request.get<any, any>('/workflow/definition/list', { params })
}

export function deployWorkflow(data: FormData) {
  return request.post<any, any>('/workflow/definition/deploy', data, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteDeployment(deploymentId: string) {
  return request.delete<any, any>(`/workflow/definition/${deploymentId}`)
}

export function getDefinitionXml(definitionId: string) {
  return request.get<any, any>(`/workflow/definition/${definitionId}/xml`)
}

export function getInstances(params: WorkflowQuery) {
  return request.get<any, any>('/workflow/instance/list', { params })
}

export function getMyTasks(params: WorkflowQuery) {
  return request.get<any, any>('/workflow/task/mine', { params })
}

export function completeTask(taskId: string, data: { variables?: Record<string, any>; comment?: string }) {
  return request.post<any, any>(`/workflow/task/${taskId}/complete`, data)
}

export function claimTask(taskId: string) {
  return request.post<any, any>(`/workflow/task/${taskId}/claim`)
}

export function unclaimTask(taskId: string) {
  return request.post<any, any>(`/workflow/task/${taskId}/unclaim`)
}

export function getProcessImage(definitionId: string) {
  return request.get<any, any>(`/workflow/definition/${definitionId}/image`, { responseType: 'blob' })
}

export function suspendInstance(instanceId: string) {
  return request.post<any, any>(`/workflow/instance/${instanceId}/suspend`)
}

export function activateInstance(instanceId: string) {
  return request.post<any, any>(`/workflow/instance/${instanceId}/activate`)
}

export function deleteInstance(instanceId: string) {
  return request.delete<any, any>(`/workflow/instance/${instanceId}`)
}
