import { api } from "../lib/api";
import type { PointOfInterest } from "../types";

export const pointOfInterestService = {
  get: (id: number) => api.get<PointOfInterest>(`/points-of-interest/${id}`).then((r) => r.data),
  create: (payload: PointOfInterest) => api.post<PointOfInterest>("/points-of-interest", payload).then((r) => r.data),
  listByAttraction: (attractionId: number) =>
    api.get<PointOfInterest[]>(`/attractions/${attractionId}/points-of-interest`).then((r) => r.data)
};
