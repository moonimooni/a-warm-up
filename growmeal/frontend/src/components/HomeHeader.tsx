import './HomeHeader.css'

interface HomeHeaderProps {
  babyName: string
  date: string
  profileEmoji: string
  profileLabel: string
}

export default function HomeHeader({ babyName, date, profileEmoji, profileLabel }: HomeHeaderProps) {
  return (
    <header className="home-header">
      <div className="home-header__brand">
        <span className="home-header__logo">🌱</span>
        <span className="home-header__name">GrowMeal</span>
      </div>
      <button className="home-header__profile" aria-label="프로필 보기">
        <span className="home-header__profile-emoji">{profileEmoji}</span>
        <span className="home-header__profile-label">{profileLabel}</span>
      </button>
      <div className="home-header__baby">
        <span className="home-header__baby-name">{babyName}</span>
        <span className="home-header__baby-date">{date}</span>
      </div>
    </header>
  )
}
