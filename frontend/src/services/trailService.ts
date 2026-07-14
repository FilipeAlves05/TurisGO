import { api } from "../lib/api";
import type { TouristAttraction, Trail } from "../types";

export const trailService = {
  list: () => api.get<Trail[]>("/trails").then((r) => r.data),
  get: (id: number) => api.get<Trail>(`/trails/${id}`).then((r) => r.data),
  listByItinerary: (itineraryId: number) => api.get<Trail[]>(`/itineraries/${itineraryId}/trails`).then((r) => r.data),
  create: (payload: Trail) => api.post<Trail>("/trails", payload).then((r) => r.data),
  linkAttraction: (trailId: number, attractionId: number) =>
    api.post(`/trails/${trailId}/attractions/${attractionId}`).then((r) => r.data),
  unlinkAttraction: (trailId: number, attractionId: number) =>
    api.delete(`/trails/${trailId}/attractions/${attractionId}`).then((r) => r.data),
  listAttractions: (trailId: number) =>
    api.get<TouristAttraction[]>(`/trails/${trailId}/attractions`).then((r) => r.data),
  listByAttraction: (attractionId: number) => api.get<Trail[]>(`/attractions/${attractionId}/trails`).then((r) => r.data)
};
