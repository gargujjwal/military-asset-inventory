export function capitalize(s: string) {
  return s.charAt(0).toUpperCase() + s.slice(1);
}

export const getInitials = (firstName: string, lastName: string) => {
  return `${firstName?.[0] || ""}${lastName?.[0] || ""}`;
};

export function buildSearchParams(
  params: Record<string, string | number | boolean | undefined | null>
): string {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      searchParams.append(key, value.toString());
    }
  });

  const query = searchParams.toString();
  return query ? `?${query}` : "";
}
