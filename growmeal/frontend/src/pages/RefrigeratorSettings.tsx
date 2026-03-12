import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import './RefrigeratorSettings.css'

interface Refrigerator {
  refrigeratorId: string
  nickname: string
  model: string
  memberCount: number
  itemCount: number
  createdAt: string
}

interface RefrigeratorModel {
  model: string
  name: string
  icon: string
  description: string
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

const getModelInfo = (modelCode: string) => {
  return REFRIGERATOR_MODELS.find(m => m.model === modelCode) || REFRIGERATOR_MODELS[0]
}

export default function RefrigeratorSettings() {
  const navigate = useNavigate()
  const [refrigerators, setRefrigerators] = useState<Refrigerator[]>([])
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [newNickname, setNewNickname] = useState('')
  const [selectedModel, setSelectedModel] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    loadRefrigerators()
  }, [])

  const loadRefrigerators = async () => {
    // TODO: Replace with actual API call
    // const response = await fetch('/api/v1/refrigerators', {
    //   headers: { 'Authorization': `Bearer ${token}` }
    // })
    // const data = await response.json()
    // setRefrigerators(data.refrigerators)

    // Mock data for now
    setRefrigerators([
      {
        refrigeratorId: '1',
        nickname: '주방 냉장고',
        model: 'FOUR_DOOR',
        memberCount: 2,
        itemCount: 5,
        createdAt: '2026-03-01T00:00:00Z'
      }
    ])
  }

  const handleAddRefrigerator = async () => {
    if (!newNickname.trim() || !selectedModel) return

    setIsLoading(true)

    try {
      // TODO: Replace with actual API call
      // const response = await fetch('/api/v1/refrigerators', {
      //   method: 'POST',
      //   headers: {
      //     'Content-Type': 'application/json',
      //     'Authorization': `Bearer ${token}`
      //   },
      //   body: JSON.stringify({ nickname: newNickname, model: selectedModel })
      // })
      // const newFridge = await response.json()

      console.log('Adding refrigerator:', { nickname: newNickname, model: selectedModel })

      // Mock: Add to list
      setTimeout(() => {
        setRefrigerators([
          ...refrigerators,
          {
            refrigeratorId: Date.now().toString(),
            nickname: newNickname,
            model: selectedModel,
            memberCount: 1,
            itemCount: 0,
            createdAt: new Date().toISOString()
          }
        ])
        setIsModalOpen(false)
        setNewNickname('')
        setSelectedModel('')
        setIsLoading(false)
      }, 300)
    } catch (error) {
      console.error('Failed to add refrigerator:', error)
      alert('냉장고 추가 중 오류가 발생했습니다.')
      setIsLoading(false)
    }
  }

  const handleDeleteRefrigerator = async (id: string, nickname: string) => {
    if (!confirm(`"${nickname}"을(를) 삭제하시겠습니까?`)) return

    try {
      // TODO: Replace with actual API call
      // await fetch(`/api/v1/refrigerators/${id}`, {
      //   method: 'DELETE',
      //   headers: { 'Authorization': `Bearer ${token}` }
      // })

      console.log('Deleting refrigerator:', id)
      setRefrigerators(refrigerators.filter(r => r.refrigeratorId !== id))
    } catch (error) {
      console.error('Failed to delete refrigerator:', error)
      alert('냉장고 삭제 중 오류가 발생했습니다.')
    }
  }

  return (
    <div className="settings-container">
      <div className="settings-header">
        <button className="btn-back" onClick={() => navigate('/')}>
          ← 뒤로
        </button>
        <h1 className="settings-title">냉장고 관리</h1>
      </div>

      <div className="settings-content">
        {refrigerators.map((fridge) => {
          const modelInfo = getModelInfo(fridge.model)
          return (
            <div key={fridge.refrigeratorId} className="fridge-card">
              <div className="fridge-card-icon">{modelInfo.icon}</div>
              <div className="fridge-card-content">
                <div className="fridge-card-title">{fridge.nickname}</div>
                <div className="fridge-card-meta">
                  <span>{modelInfo.name}</span>
                  <span>•</span>
                  <span>반찬 {fridge.itemCount}개</span>
                  <span>•</span>
                  <span>멤버 {fridge.memberCount}명</span>
                </div>
              </div>
              <button
                className="btn-delete"
                onClick={() => handleDeleteRefrigerator(fridge.refrigeratorId, fridge.nickname)}
                aria-label="삭제"
              >
                🗑️
              </button>
            </div>
          )
        })}

        <button className="btn-add-fridge" onClick={() => setIsModalOpen(true)}>
          + 냉장고 추가
        </button>
      </div>

      {/* Add Refrigerator Modal */}
      {isModalOpen && (
        <div className="modal-overlay" onClick={() => !isLoading && setIsModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">냉장고 추가</h2>

            <div className="form-group">
              <label className="form-label">냉장고 이름</label>
              <input
                type="text"
                className="form-input"
                placeholder="예: 주방 냉장고"
                value={newNickname}
                onChange={(e) => setNewNickname(e.target.value)}
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

            <div className="modal-actions">
              <button
                className="btn-secondary"
                onClick={() => setIsModalOpen(false)}
                disabled={isLoading}
              >
                취소
              </button>
              <button
                className="btn-primary"
                onClick={handleAddRefrigerator}
                disabled={!newNickname.trim() || !selectedModel || isLoading}
              >
                {isLoading ? '추가 중...' : '추가'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
