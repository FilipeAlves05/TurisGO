import { api } from "../lib/api";
import type { Itinerary } from "../types";

export const itineraryService = {
  list: () => api.get<Itinerary[]>("/itineraries").then((r) => r.data),
  get: (id: number) => api.get<Itinerary>(`/itineraries/${id}`).then((r) => r.data),
  listByTourist: (touristId: number) => api.get<Itinerary[]>(`/tourists/${touristId}/itineraries`).then((r) => r.data),
  create: (payload: Itinerary) => api.post<Itinerary>("/itineraries", payload).then((r) => r.data)
};
