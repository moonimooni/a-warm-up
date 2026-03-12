import { useState, useEffect } from 'react'
import './Onboarding.css'

interface RefrigeratorModel {
  model: string
  name: string
  icon: string
  description: string
}

interface OnboardingStep2Props {
  onBack: () => void
  onComplete: (data: { nickname: string; model: string }) => void
}

const REFRIGERATOR_MODELS: RefrigeratorModel[] = [
  {
    model: 'FOUR_DOOR',
    name: '4도어 냉장고',
    icon: '🚪',
    description: '냉장·냉동·야채칸이 분리된 대용량'
  },
  {
    model: 'SIDE_BY_SIDE',
    name: '양문형 냉장고',
    icon: '🚪🚪',
    description: '좌우로 나뉜 넓은 공간'
  },
  {
    model: 'TWO_DOOR',
    name: '2도어 냉장고',
    icon: '🧊',
    description: '상하 구분된 기본형'
  },
  {
    model: 'ONE_DOOR',
    name: '1도어 냉장고',
    icon: '📦',
    description: '작은 공간에 적합한 미니 냉장고'
  }
]

export default function OnboardingStep2({ onBack, onComplete }: OnboardingStep2Props) {
  const [nickname, setNickname] = useState('우리집 냉장고')
  const [selectedModel, setSelectedModel] = useState<string>('')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (nickname.trim() && selectedModel) {
      onComplete({ nickname: nickname.trim(), model: selectedModel })
    }
  }

  const isValid = nickname.trim() && selectedModel

  return (
    <div className="onboarding-container">
      <button type="button" className="btn-back" onClick={onBack}>
        ← 이전
      </button>

      <div className="onboarding-header">
        <div className="onboarding-step">2/2</div>
        <h1 className="onboarding-title">냉장고 등록하기</h1>
        <p className="onboarding-subtitle">사용 중인 냉장고 타입을 선택해주세요</p>
      </div>

      <form className="onboarding-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">냉장고 이름</label>
          <input
            type="text"
            className="form-input"
            placeholder="예: 주방 냉장고"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            maxLength={30}
          />
        </div>

        <div className="form-group">
          <label className="form-label">냉장고 타입</label>
          <div className="refrigerator-models">
            {REFRIGERATOR_MODELS.map((model) => (
              <div
                key={model.model}
                className={`refrigerator-card ${selectedModel === model.model ? 'selected' : ''}`}
                onClick={() => setSelectedModel(model.model)}
                role="button"
                tabIndex={0}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    setSelectedModel(model.model)
                  }
                }}
              >
                <div className="refrigerator-icon">{model.icon}</div>
                <div className="refrigerator-info">
                  <div className="refrigerator-name">{model.name}</div>
                  <div className="refrigerator-desc">{model.description}</div>
                </div>
                <div className="refrigerator-check"></div>
              </div>
            ))}
          </div>
        </div>

        <button
          type="submit"
          className="btn-primary btn-submit"
          disabled={!isValid}
        >
          완료
        </button>
      </form>
    </div>
  )
}
