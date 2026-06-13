export interface SearchQuery {
  query: string;
}

export interface Search {
  id: number;
  query: string;
  updatedDate: string; // date
  lastSeenAt: string; // date-time
  isExpired: boolean;
}
