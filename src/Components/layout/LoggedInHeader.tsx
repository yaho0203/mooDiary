import { useState } from "react";
import { NavLink } from "react-router-dom";
import { Button } from "../ui/button";
import { cn } from "@/lib/utils";
import { Menu, X, PenSquare } from "lucide-react";
import { NAV_ITEMS } from "@/constants/navigation";

export default function LoggedInHeader() {
  const [open, setOpen] = useState(false);

  return (
    <header className="sticky top-0 z-40 w-full border-b bg-white/80 backdrop-blur supports-[backdrop-filter]:bg-white/60">
      <div className="container max-w-[1440px] ml-4 flex h-16 items-center px-4 sm:px-6">
        <a href="/" className="flex items-center gap-2 font-semibold text-gray-900 ml-8">
          <PenSquare className="h-8 w-8" />
          <span className="text-3xl">mooDiary</span>
        </a>

        {/* Desktop nav */}
        <nav className="hidden md:flex items-center gap-20 text-base ml-52">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  "whitespace-nowrap transition-colors hover:text-gray-900",
                  isActive ? "text-gray-900" : "text-gray-500",
                )
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="hidden md:flex items-center ml-52">
          <Button className="px-5 w-14 h-14">
            <img className="w-14 h-14 outline outline-1" src="https://placehold.co/56x56" />
          </Button>
        </div>

        <div className="hidden md:flex items-center w-12 h-12 ml-20">
              <button className="flex items-center justify-center w-12 h-12">
                <img src="/sidebar.svg" alt="hamburgerbar" className="w-12 h-12 object-contain" />
              </button>
        </div>

        {/* Mobile */}
        <button
          className="md:hidden inline-flex items-center justify-center rounded-md border px-2.5 py-2 text-gray-700"
          onClick={() => setOpen((v) => !v)}
          aria-label="Toggle menu"
        >
          {open ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
        </button>
      </div>

      {/* Mobile menu panel */}
      {open && (
        <div className="md:hidden border-t bg-white">
          <div className="container mx-auto px-4 py-3 flex flex-col gap-3">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => setOpen(false)}
                className={({ isActive }) =>
                  cn(
                    "rounded-md px-2 py-2 text-sm transition-colors hover:bg-gray-50",
                    isActive ? "text-gray-900" : "text-gray-600",
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
            <div className="flex pt-2 w-12 h-12 gap-2">
              <button className="flex items-center justify-center">
                <PenSquare className="h-8 w-8" />              
              </button>
              <button className="flex items-center justify-center">
                <img src="/sidebar.svg" alt="hamburgerbar" className="w-12 h-12 object-contain" />
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}