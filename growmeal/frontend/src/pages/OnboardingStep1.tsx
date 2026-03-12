import { useState } from 'react'
import './Onboarding.css'

interface OnboardingStep1Props {
  onNext: (data: { name: string; birthDate: string; allergies: string[] }) => void
}

export default function OnboardingStep1({ onNext }: OnboardingStep1Props) {
  const [name, setName] = useState('')
  const [birthDate, setBirthDate] = useState('')
  const [allergyInput, setAllergyInput] = useState('')
  const [allergies, setAllergies] = useState<string[]>([])

  const handleAddAllergy = () => {
    if (allergyInput.trim() && !allergies.includes(allergyInput.trim())) {
      setAllergies([...allergies, allergyInput.trim()])
      setAllergyInput('')
    }
  }

  const handleRemoveAllergy = (allergy: string) => {
    setAllergies(allergies.filter(a => a !== allergy))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (name.trim() && birthDate) {
      onNext({ name: name.trim(), birthDate, allergies })
    }
  }

  const isValid = name.trim() && birthDate

  return (
    <div className="onboarding-container">
      <div className="onboarding-header">
        <div className="onboarding-step">1/2</div>
        <h1 className="onboarding-title">아기 프로필 만들기</h1>
        <p className="onboarding-subtitle">우리 아이의 정보를 입력해주세요</p>
      </div>

      <form className="onboarding-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">이름</label>
          <input
            type="text"
            className="form-input"
            placeholder="예: 하율"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={20}
          />
        </div>

        <div className="form-group">
          <label className="form-label">생년월일</label>
          <input
            type="date"
            className="form-input"
            value={birthDate}
            onChange={(e) => setBirthDate(e.target.value)}
            max={new Date().toISOString().split('T')[0]}
          />
        </div>

        <div className="form-group">
          <label className="form-label">알레르기 식품 (선택)</label>
          <div className="allergy-input-group">
            <input
              type="text"
              className="form-input"
              placeholder="예: 땅콩"
              value={allergyInput}
              onChange={(e) => setAllergyInput(e.target.value)}
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  e.preventDefault()
                  handleAddAllergy()
                }
              }}
            />
            <button
              type="button"
              className="btn-add-allergy"
              onClick={handleAddAllergy}
              disabled={!allergyInput.trim()}
            >
              추가
            </button>
          </div>

          {allergies.length > 0 && (
            <div className="allergy-tags">
              {allergies.map((allergy) => (
                <span key={allergy} className="allergy-tag">
                  {allergy}
                  <button
                    type="button"
                    className="allergy-tag-remove"
                    onClick={() => handleRemoveAllergy(allergy)}
                    aria-label={`${allergy} 제거`}
                  >
                    ×
                  </button>
                </span>
              ))}
            </div>
          )}
        </div>

        <button
          type="submit"
          className="btn-primary btn-submit"
          disabled={!isValid}
        >
          다음 단계로
        </button>
      </form>
    </div>
  )
}
