import { ReactNode } from "react";
import Frame from "@/components/ui/frame";
import Header from "@/components/layout/Header";

interface PageLayoutProps {
  children: ReactNode;
}

export const PageLayout: React.FC<PageLayoutProps> = ({ children }) => {
  return (
    <div className="flex justify-center bg-gradient-to-b from-light-bg-start to-light-bg-end w-full" style={{ fontFamily: "Inter, sans-serif" }}>
      <div className="w-[1217px] h-[1980px] flex flex-col">
        <section className="flex flex-1 h-full">
          <Frame />
          <div
            className="mt-16 flex flex-col flex-1 h-[1900px]"
            style={{ background: "linear-gradient(90deg, #FFEAB1 7.55%, #FFDED3 121.31%)" }}
          >
            <Header />
            {children}
          </div>
        </section>
      </div>
    </div>
  );
};

