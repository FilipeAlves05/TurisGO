import { api } from "../lib/api";
import type { AuthResponse, Institution, LoginRequest, Tourist } from "../types";

export const authService = {
  login: (payload: LoginRequest) => api.post<AuthResponse>("/auth/login", payload).then((r) => r.data),
  logout: () => api.post("/auth/logout").then((r) => r.data),
  me: () => api.get<AuthResponse>("/auth/me").then((r) => r.data),
  registerTourist: (payload: Tourist) => api.post<Tourist>("/auth/registro/turista", payload).then((r) => r.data),
  registerInstitution: (payload: Institution) =>
    api.post<Institution>("/auth/registro/instituicao", payload).then((r) => r.data)
};
