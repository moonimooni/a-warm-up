import { useState } from 'react'
import './MealSection.css'

export type MealStatus = 'done' | 'active' | 'empty'

export interface MealSlot {
  id: string
  label: string
  status: MealStatus
  note?: string
}

export interface SnackSlot {
  id: string
  label?: string
  filled: boolean
}

interface MealSectionProps {
  meals: MealSlot[]
  snacks: SnackSlot[]
  onRecordClick: () => void
  onAddSnack: () => void
  onMealClick: (id: string) => void
  onSnackClick: (id: string) => void
}

function statusIcon(status: MealStatus) {
  if (status === 'done') return <span className="meal-slot__icon meal-slot__icon--done">✓</span>
  if (status === 'active') return <span className="meal-slot__icon meal-slot__icon--active">⏳</span>
  return <span className="meal-slot__icon meal-slot__icon--empty">─</span>
}

export default function MealSection({
  meals,
  snacks,
  onRecordClick,
  onAddSnack,
  onMealClick,
  onSnackClick,
}: MealSectionProps) {
  const [pressed, setPressed] = useState(false)

  return (
    <div className="card">
      <div className="card-header">
        <span className="card-title">오늘의 끼니</span>
        <a className="card-link">전체 보기</a>
      </div>

      {/* Main meals row */}
      <div className="meal-row">
        {meals.map((m) => (
          <button
            key={m.id}
            className={`meal-slot meal-slot--${m.status}`}
            onClick={() => onMealClick(m.id)}
            aria-label={m.label}
          >
            {statusIcon(m.status)}
            <span className="meal-slot__label">{m.label}</span>
          </button>
        ))}
      </div>

      {/* Snacks */}
      <div className="snack-section">
        <span className="snack-section__title">간식</span>
        <div className="snack-row">
          {snacks.map((s) => (
            <button
              key={s.id}
              className={`snack-slot ${s.filled ? 'snack-slot--filled' : 'snack-slot--empty'}`}
              onClick={() => onSnackClick(s.id)}
              aria-label={s.label || '간식'}
            >
              {s.filled ? (
                <>
                  {s.label && <span className="snack-slot__label">{s.label}</span>}
                </>
              ) : (
                <span className="snack-slot__empty-icon">─</span>
              )}
            </button>
          ))}
          <button className="snack-slot snack-slot--add" onClick={onAddSnack} aria-label="간식 추가">
            <span className="snack-slot__add-icon">+</span>
            <span className="snack-slot__label">간식 추가</span>
          </button>
        </div>
      </div>

      {/* CTA */}
      <button
        className={`record-btn ${pressed ? 'record-btn--pressed' : ''}`}
        onMouseDown={() => setPressed(true)}
        onMouseUp={() => setPressed(false)}
        onMouseLeave={() => setPressed(false)}
        onClick={onRecordClick}
      >
        + 지금 밥 기록하기
      </button>
    </div>
  )
}
