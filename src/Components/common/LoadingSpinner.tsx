interface LoadingSpinnerProps {
  message?: string;
  size?: "sm" | "md" | "lg";
}

const sizeClasses = {
  sm: "h-6 w-6 border-2",
  md: "h-12 w-12 border-b-2",
  lg: "h-16 w-16 border-b-3",
};

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  message = "로딩 중...",
  size = "md",
}) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div
          className={`animate-spin rounded-full border-[#8E573E] mx-auto ${sizeClasses[size]}`}
        />
        {message && <p className="mt-4 text-[#8E573E]">{message}</p>}
      </div>
    </div>
  );
};

