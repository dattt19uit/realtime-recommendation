type Props = {
  recentItems: string[];
  recommendations: string[];
};

export default function Recommendation({
  recentItems,
  recommendations,
}: Props) {
  return (
    <div>
      <h2 className="font-semibold">Recent Items</h2>
      <ul>
        {recentItems.map((id, index) => (
          <li key={`${id}-${index}`}>{id}</li>
        ))}
      </ul>

      <h2 className="font-semibold mt-4">Recommendations</h2>
      <ul>
        {recommendations.map((id) => (
          <li key={id}>{id}</li>
        ))}
      </ul>
    </div>
  );
}
