type Props = { title: string; description?: string };

export default function Placeholder({ title, description }: Props) {
  return (
    <section className="container mx-auto px-4 sm:px-6 py-16">
      <div className="mx-auto max-w-3xl text-center">
        <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-gray-900">{title}</h1>
        <p className="mt-4 text-gray-600">
          {description ?? "이 페이지의 구체적인 내용은 아직 준비되지 않았습니다. 계속해서 원하는 내용을 알려주시면 이 섹션을 채워드릴게요."}
        </p>
      </div>
    </section>
  );
}
