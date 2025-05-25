import type { ErrorResponse } from "~/types/backend-stubs";

type Props = {
  error: ErrorResponse;
};
export default function FormError({ error }: Props) {
  return (
    <div className="flex flex-col items-center gap-3 rounded-md border-2 border-red-600 p-4">
      <div className="space-y-2">
        <details open>
          <summary className="font-semibold">Error: {error.message}</summary>
          <ul>
            {error.errors.map((e, i) => (
              <li key={i}>{e}</li>
            ))}
          </ul>
        </details>
      </div>
    </div>
  );
}
