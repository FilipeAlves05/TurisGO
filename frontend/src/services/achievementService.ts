import { api } from "../lib/api";
import type { Achievement } from "../types";

export const achievementService = {
  list: () => api.get<Achievement[]>("/achievements").then((r) => r.data),
  get: (id: number) => api.get<Achievement>(`/achievements/${id}`).then((r) => r.data),
  create: (payload: Achievement) => api.post<Achievement>("/achievements", payload).then((r) => r.data),
  grant: (touristId: number, achievementId: number) =>
    api.post(`/tourists/${touristId}/achievements/${achievementId}`).then((r) => r.data),
  listByTourist: (touristId: number) => api.get<Achievement[]>(`/tourists/${touristId}/achievements`).then((r) => r.data)
};
