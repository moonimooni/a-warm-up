import '../App.css'
import HomeHeader from '../components/HomeHeader'
import MealSection, { MealSlot, SnackSlot } from '../components/MealSection'
import InventorySection, { InventoryItem } from '../components/InventorySection'
import RecentLog, { LogEntry } from '../components/RecentLog'

/* ── Mock Data ─────────────────────────────────────────── */
const meals: MealSlot[] = [
  { id: 'breakfast', label: '아침', status: 'done' },
  { id: 'lunch',     label: '점심', status: 'active' },
  { id: 'dinner',    label: '저녁', status: 'empty' },
]

const snacks: SnackSlot[] = [
  { id: 's1', label: '쌀과자', filled: true },
  { id: 's2', label: '바나나', filled: true },
  { id: 's3', filled: false },
  { id: 's4', filled: false },
]

const inventory: InventoryItem[] = [
  { id: 'i1', name: '당근볶음',   location: '냉장 하단' },
  { id: 'i2', name: '계란찜',     location: '냉장 하단', warning: '유통임박' },
  { id: 'i3', name: '생선구이',   location: '냉동',      locationColor: '#5b8fd4' },
  { id: 'i4', name: '브로콜리',   location: '야채칸',    locationColor: '#5cac7a' },
  { id: 'i5', name: '고구마조림', location: '냉장 하단' },
]

const recentLog: LogEntry[] = [
  {
    id: 'log-yesterday',
    dayLabel: '어제',
    foods: [
      { name: '밥' },
      { name: '당근볶음' },
      { name: '계란찜' },
    ],
    liked: true,
  },
  {
    id: 'log-2days',
    dayLabel: '그제',
    foods: [
      { name: '국수' },
      { name: '생선구이' },
    ],
    liked: false,
  },
]

/* ── Component ─────────────────────────────────────────── */
export default function Home() {
  const today = new Date().toLocaleDateString('ko-KR', {
    year: 'numeric', month: '2-digit', day: '2-digit',
  }).replace(/\. /g, '.').replace('.', '.')

  return (
    <div className="app-shell">
      <HomeHeader
        babyName="하율이"
        date={today}
        profileEmoji="👩"
        profileLabel="엄마"
      />

      <div style={{ padding: '0 14px' }}>
        <MealSection
          meals={meals}
          snacks={snacks}
          onRecordClick={() => alert('기록 화면으로 이동')}
          onAddSnack={() => alert('간식 추가')}
          onMealClick={(id) => alert(`${id} 클릭`)}
          onSnackClick={(id) => alert(`${id} 클릭`)}
        />

        <InventorySection
          items={inventory}
          onItemClick={(id) => alert(`${id} 클릭`)}
          onAddClick={() => alert('반찬 추가')}
          onViewAll={() => alert('인벤토리 전체 보기')}
        />

        <RecentLog
          entries={recentLog}
          onEntryClick={(id) => alert(`${id} 클릭`)}
        />
      </div>
    </div>
  )
}
