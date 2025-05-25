const { VITE_API_BASE_URL, ...otherViteConfig } = import.meta.env;

export const env = {
  API_BASE_URL: VITE_API_BASE_URL as string,
  __vite__: otherViteConfig,
};

// This is one of the few places where I recommend adding a `console.log` statement
// To make it easy to figure out the frontend environment config at any moment
if (env.__vite__.MODE === "development") {
  console.dir(env);
}
