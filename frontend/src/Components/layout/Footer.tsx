import { ArrowRight, Facebook, Github, Linkedin } from "lucide-react";
import { Button } from "../ui/button";

export default function Footer() {
  return (
    <footer className="mt-24 bg-primary text-primary-foreground">
      <div className="container mx-auto px-4 sm:px-6 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-10">
          <div>
            <h3 className="text-lg font-semibold mb-4">Product</h3>
            <ul className="space-y-2 text-sm/6 opacity-90">
              <li>
                <a href="/results" className="hover:underline">
                  Recents
                </a>
              </li>
              <li>
                <a href="/bookmark" className="hover:underline">
                  Bookmark
                </a>
              </li>
              <li>
                <a href="/write" className="hover:underline">
                  Write
                </a>
              </li>
              <li>
                <a href="/write" className="hover:underline">
                  Edit
                </a>
              </li>
              <li>
                <a href="/results" className="hover:underline">
                  Today’s emotion
                </a>
              </li>
              <li>
                <a href="/recommendation" className="hover:underline">
                  Recommendation
                </a>
              </li>
            </ul>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4">Information</h3>
            <ul className="space-y-2 text-sm/6 opacity-90">
              <li>
                <a href="/results" className="hover:underline">
                  Information
                </a>
              </li>
              <li>
                <a href="/profile" className="hover:underline">
                  Profile
                </a>
              </li>
              <li>
                <a href="/" className="hover:underline">
                  Developers
                </a>
              </li>
            </ul>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4">Company</h3>
            <ul className="space-y-2 text-sm/6 opacity-90">
              <li>
                <a href="/" className="hover:underline">
                  Moodiary
                </a>
              </li>
            </ul>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4">Subscribe</h3>
            <form className="flex overflow-hidden rounded-md bg-white text-gray-900">
              <input
                type="email"
                required
                placeholder="Email address"
                className="w-full px-3 py-2 outline-none"
              />
              <Button type="submit" className="rounded-none px-4">
                <ArrowRight className="h-4 w-4" />
              </Button>
            </form>
            <a
              href="#"
              className="mt-2 block text-xs text-primary-foreground/80 hover:underline"
            >
              more details
            </a>
          </div>
        </div>

        <div className="mt-10 border-t border-primary-foreground/20 pt-6 w-full flex flex-col sm:flex-row items-center sm:items-start">
          {/* 왼쪽 */}
          <div className="w-full sm:w-1/3 flex justify-start">
            <p className="text-xs text-primary-foreground/80">
              © {new Date().getFullYear()} Moodiary
            </p>
          </div>

          {/* 중앙 */}
          <div className="w-full sm:w-1/3 flex justify-center mt-4 sm:mt-0">
            <div className="flex items-center gap-4">
              <a
                href="#"
                aria-label="LinkedIn"
                className="opacity-90 hover:opacity-100"
              >
                <Linkedin className="h-4 w-4" />
              </a>
              <a
                href="#"
                aria-label="Facebook"
                className="opacity-90 hover:opacity-100"
              >
                <Facebook className="h-4 w-4" />
              </a>
              <a
                href="#"
                aria-label="GitHub"
                className="opacity-90 hover:opacity-100"
              >
                <Github className="h-4 w-4" />
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
