export type UserType = "TOURIST" | "INSTITUTION";

export interface User {
  id?: number;
  name: string;
  email: string;
  password?: string | null;
  registrationDate?: string;
  contacts?: string[];
}

export interface Tourist extends User {
  birthDate: string;
  totalPoints?: number;
  level?: number;
}

export interface Institution extends User {
  cnpj: string;
}

export interface AuthResponse {
  id: number;
  name: string;
  email: string;
  type: UserType;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TouristAttraction {
  id?: number;
  name: string;
  description: string;
  address: string;
  contact?: string;
  status?: string;
  operatingHours?: string;
  gallery?: string;
  profileImage?: string;
  institutionId?: number;
}

export interface Trail {
  id?: number;
  name: string;
  description: string;
  difficulty: string;
  category: string;
  estimatedTime: string;
  rewardPoints?: number;
  itineraryId?: number;
}

export interface Itinerary {
  id?: number;
  name: string;
  startDate?: string;
  endDate?: string;
  touristId?: number;
}

export interface PointOfInterest {
  id?: number;
  location: string;
  description: string;
  attractionId?: number;
}

export interface Review {
  id?: number;
  touristId?: number;
  attractionId?: number;
  comment: string;
  rating: number;
  reviewDate?: string;
  imageUrl?: string;
}

export interface CheckIn {
  id?: number;
  geolocation?: string;
  dateTime?: string;
  touristId?: number;
  attractionId?: number;
  validatorInstituionId?: number;
}

export interface CheckInRequest {
  touristId: number;
  attractionId: number;
  geolocation?: string;
  amount?: number;
}

export interface CheckInResult {
  checkIn: CheckIn;
  pointsAwarded: number;
  totalPoints: number;
  level: number;
}

export interface Achievement {
  id?: number;
  name: string;
  description: string;
  icon?: string;
}

export interface AverageRating {
  attractionId: number;
  averageRating: number;
}

export interface ToggleFavoriteResponse {
  favorited: boolean;
}
