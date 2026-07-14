import { api } from "../lib/api";
import type { TouristAttraction } from "../types";

export const attractionService = {
  list: () => api.get<TouristAttraction[]>("/attractions").then((r) => r.data),
  get: (id: number) => api.get<TouristAttraction>(`/attractions/${id}`).then((r) => r.data),
  listByInstitution: (institutionId: number) =>
    api.get<TouristAttraction[]>(`/institutions/${institutionId}/attractions`).then((r) => r.data),
  create: (payload: TouristAttraction) => api.post<TouristAttraction>("/attractions", payload).then((r) => r.data),
  update: (id: number, payload: TouristAttraction) => api.put(`/attractions/${id}`, payload).then((r) => r.data)
};
