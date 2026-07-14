import { api } from "../lib/api";
import type { CheckIn, CheckInRequest, CheckInResult } from "../types";

export const checkinService = {
  perform: (payload: CheckInRequest) => api.post<CheckInResult>("/checkins", payload).then((r) => r.data),
  list: () => api.get<CheckIn[]>("/checkins").then((r) => r.data),
  get: (id: number) => api.get<CheckIn>(`/checkins/${id}`).then((r) => r.data),
  listByTourist: (touristId: number) => api.get<CheckIn[]>(`/checkins/tourists/${touristId}`).then((r) => r.data),
  listByAttraction: (attractionId: number) => api.get<CheckIn[]>(`/checkins/attractions/${attractionId}`).then((r) => r.data)
};
