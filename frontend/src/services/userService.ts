import { api } from "../lib/api";
import type { Tourist, User } from "../types";

export const userService = {
  list: () => api.get<User[]>("/users").then((r) => r.data),
  get: (id: number) => api.get<User>(`/users/${id}`).then((r) => r.data),
  getTourist: (id: number) => api.get<Tourist>(`/tourists/${id}`).then((r) => r.data),
  listTourists: () => api.get<Tourist[]>("/tourists").then((r) => r.data),
  contacts: (id: number) => api.get<string[]>(`/users/${id}/contacts`).then((r) => r.data),
  addContact: (id: number, contact: string) => api.post(`/users/${id}/contacts`, { contact }).then((r) => r.data),
  removeContact: (id: number, contact: string) => api.delete(`/users/${id}/contacts/${contact}`).then((r) => r.data)
};
