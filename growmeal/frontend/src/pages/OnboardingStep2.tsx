import { useState, useEffect } from 'react'
import './Onboarding.css'

interface RefrigeratorModel {
  model: string
  name: string
  imageUrl: string
}

interface OnboardingStep2Props {
  onBack: () => void
  onComplete: (data: { nickname: string; model: string }) => void
}

export default function OnboardingStep2({ onBack, onComplete }: OnboardingStep2Props) {
  const [nickname, setNickname] = useState('우리집 냉장고')
  const [selectedModel, setSelectedModel] = useState<string>('')
  const [models, setModels] = useState<RefrigeratorModel[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/api/v1/refrigerator-models', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          setModels(data.data.models)
        }
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

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
          <label className="form-label">냉장고 모델</label>
          {loading ? (
            <p>모델 목록을 불러오는 중...</p>
          ) : (
            <div className="refrigerator-models">
              {models.map((model) => (
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
                  <img className="refrigerator-icon" src={model.imageUrl} alt={model.name} />
                  <div className="refrigerator-info">
                    <div className="refrigerator-name">{model.name}</div>
                  </div>
                  <div className="refrigerator-check"></div>
                </div>
              ))}
            </div>
          )}
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
