interface MetricsCardProps {
  title: string;
  value: number;
  subtitle?: string;
  color?:
    | "primary"
    | "secondary"
    | "accent"
    | "success"
    | "warning"
    | "error"
    | "info";
  onClick?: () => void;
  clickable?: boolean;
}

export default function MetricsCard({
  title,
  value,
  subtitle,
  color = "primary",
  onClick,
  clickable = false,
}: MetricsCardProps) {
  const cardClasses = `
    card bg-base-100 shadow-lg border border-base-300 
    ${
      clickable
        ? "cursor-pointer hover:shadow-xl hover:scale-105 transition-all duration-200"
        : ""
    }
  `;

  const colorClasses = {
    primary: "text-primary",
    secondary: "text-secondary",
    accent: "text-accent",
    success: "text-success",
    warning: "text-warning",
    error: "text-error",
    info: "text-info",
  };

  return (
    <div className={cardClasses} onClick={onClick}>
      <div className="card-body p-6">
        <div className="flex flex-col items-center text-center">
          <h3 className="text-sm font-medium text-base-content/70 mb-2">
            {title}
          </h3>
          <div className={`text-3xl font-bold ${colorClasses[color]} mb-1`}>
            {value.toLocaleString()}
          </div>
          {subtitle && (
            <p className="text-xs text-base-content/60">{subtitle}</p>
          )}
          {clickable && (
            <div className="text-xs text-base-content/50 mt-2">
              Click for details
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
