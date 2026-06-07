import request from '@/utils/request';
import type { PositionVO } from '@/types';

export function getPositions() {
  return request.get<PositionVO[]>('/system/positions');
}

export function createPosition(data: Partial<PositionVO>) {
  return request.post('/system/positions', data);
}

export function updatePosition(id: number, data: Partial<PositionVO>) {
  return request.put(`/system/positions/${id}`, data);
}

export function deletePosition(id: number) {
  return request.delete(`/system/positions/${id}`);
}

export function getUserPositionIds(userId: number) {
  return request.get<number[]>(`/system/positions/user/${userId}`);
}

export function assignUserPositions(userId: number, positionIds: number[]) {
  return request.put(`/system/positions/user/${userId}`, { positionIds });
}
