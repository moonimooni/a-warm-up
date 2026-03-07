import './RecentLog.css'

export interface LogEntry {
  id: string
  dayLabel: string
  foods: { emoji: string; name: string }[]
  liked: boolean | null
}

interface RecentLogProps {
  entries: LogEntry[]
  onEntryClick: (id: string) => void
}

export default function RecentLog({ entries, onEntryClick }: RecentLogProps) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-title">최근 기록</span>
        <a className="card-link">더 보기</a>
      </div>
      <div className="recent-log__list">
        {entries.map((entry) => (
          <button
            key={entry.id}
            className="log-row"
            onClick={() => onEntryClick(entry.id)}
            aria-label={`${entry.dayLabel} 기록 보기`}
          >
            <span className="log-row__day">{entry.dayLabel}</span>
            <div className="log-row__foods">
              {entry.foods.map((f, i) => (
                <span key={i} className="log-row__food-pill" title={f.name}>
                  {f.emoji}
                </span>
              ))}
            </div>
            <div className="log-row__reaction">
              {entry.liked === true && (
                <><span className="log-row__thumb log-row__thumb--up">👍</span><span className="log-row__thumb log-row__thumb--down-muted">👎</span></>
              )}
              {entry.liked === false && (
                <><span className="log-row__thumb log-row__thumb--up-muted">👍</span><span className="log-row__thumb log-row__thumb--down">👎</span></>
              )}
              {entry.liked === null && (
                <><span className="log-row__thumb log-row__thumb--up-muted">👍</span><span className="log-row__thumb log-row__thumb--down-muted">👎</span></>
              )}
            </div>
          </button>
        ))}
      </div>
    </div>
  )
}
