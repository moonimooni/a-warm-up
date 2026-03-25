import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { FRIDGE_COMPARTMENT_MAPS } from '../config/fridgeCompartmentMaps'
import './AddInventoryModal.css'

interface Refrigerator {
  refrigeratorId: number
  nickname: string
  model: string
  itemCount: number
  createdAt: string
}

interface RefrigeratorModel {
  model: string
  name: string
  imageUrl: string
}

interface Compartment {
  compartmentId: string
  name: string
}

interface AddInventoryModalProps {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
}

export default function AddInventoryModal({ isOpen, onClose, onSuccess }: AddInventoryModalProps) {
  const navigate = useNavigate()

  // Form fields
  const [name, setName] = useState('')
  const [type, setType] = useState<'MEAL' | 'INGREDIENT'>('MEAL')
  const [expiresAt, setExpiresAt] = useState('')

  // Refrigerator data
  const [refrigerators, setRefrigerators] = useState<Refrigerator[]>([])
  const [models, setModels] = useState<RefrigeratorModel[]>([])
  const [selectedFridgeId, setSelectedFridgeId] = useState<number | null>(null)
  const [selectedFridgeModel, setSelectedFridgeModel] = useState<string | null>(null)

  // Compartment data
  const [compartments, setCompartments] = useState<Compartment[]>([])
  const [selectedCompartmentId, setSelectedCompartmentId] = useState<string | null>(null)
  const [fridgeImageUrl, setFridgeImageUrl] = useState<string | null>(null)

  // UI state
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [loadingFridges, setLoadingFridges] = useState(false)
  const [loadingCompartments, setLoadingCompartments] = useState(false)

  const token = localStorage.getItem('accessToken')

  // Fetch refrigerators + models when modal opens
  useEffect(() => {
    if (!isOpen) return
    resetForm()
    fetchInitialData()
  }, [isOpen])

  // Auto-select if only one refrigerator
  useEffect(() => {
    if (refrigerators.length === 1) {
      handleFridgeSelect(refrigerators[0].refrigeratorId, refrigerators[0].model)
    }
  }, [refrigerators])

  const resetForm = () => {
    setName('')
    setType('MEAL')
    setExpiresAt('')
    setSelectedFridgeId(null)
    setSelectedFridgeModel(null)
    setSelectedCompartmentId(null)
    setCompartments([])
    setFridgeImageUrl(null)
  }

  const fetchInitialData = async () => {
    setLoadingFridges(true)
    try {
      const [fridgeRes, modelRes] = await Promise.all([
        fetch('/api/v1/refrigerators', {
          headers: { 'Authorization': `Bearer ${token}` }
        }),
        fetch('/api/v1/refrigerator-models')
      ])
      const fridgeData = await fridgeRes.json()
      const modelData = await modelRes.json()

      if (fridgeData.data) {
        setRefrigerators(fridgeData.data.refrigerators || [])
      }
      if (modelData.data) {
        setModels(modelData.data.models || [])
      }
    } catch {
      console.error('Failed to fetch refrigerator data')
    } finally {
      setLoadingFridges(false)
    }
  }

  const handleFridgeSelect = async (fridgeId: number, fridgeModel: string) => {
    setSelectedFridgeId(fridgeId)
    setSelectedFridgeModel(fridgeModel)
    setSelectedCompartmentId(null)
    setCompartments([])

    // Find model image
    const model = models.find(m => m.model === fridgeModel)
    setFridgeImageUrl(model?.imageUrl || null)

    // Fetch compartments
    setLoadingCompartments(true)
    try {
      const res = await fetch(`/api/v1/refrigerators/${fridgeId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.data?.compartments) {
        setCompartments(data.data.compartments)
      }
    } catch {
      console.error('Failed to fetch compartments')
    } finally {
      setLoadingCompartments(false)
    }
  }

  const groupCompartments = (items: Compartment[]) => {
    const groups: Record<string, Compartment[]> = {}
    for (const c of items) {
      const zone = c.compartmentId.startsWith('냉동') ? '냉동' : '냉장'
      if (!groups[zone]) groups[zone] = []
      groups[zone].push(c)
    }
    return groups
  }

  const isValid = name.trim() && expiresAt && selectedFridgeId !== null && selectedCompartmentId !== null

  const handleSubmit = async () => {
    if (!isValid) return
    setIsSubmitting(true)
    try {
      const res = await fetch('/api/v1/inventory', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          name: name.trim(),
          type,
          refrigeratorId: selectedFridgeId,
          compartmentId: selectedCompartmentId,
          expiresAt
        })
      })
      const data = await res.json()
      if (data.success) {
        onSuccess()
      } else {
        alert(data.error?.message || '추가에 실패했습니다.')
      }
    } catch {
      alert('네트워크 오류가 발생했습니다.')
    } finally {
      setIsSubmitting(false)
    }
  }

  if (!isOpen) return null

  const grouped = groupCompartments(compartments)
  const overlayMap = selectedFridgeModel ? FRIDGE_COMPARTMENT_MAPS[selectedFridgeModel] : undefined
  const hasOverlay = overlayMap && fridgeImageUrl

  return (
    <div className="modal-overlay" onClick={() => !isSubmitting && onClose()}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal-title">반찬/재료 추가</h2>

        {/* Name */}
        <div className="form-group">
          <label className="form-label">이름</label>
          <input
            type="text"
            className="form-input"
            placeholder="예: 당근, 계란찜"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={50}
          />
        </div>

        {/* Type */}
        <div className="form-group">
          <label className="form-label">종류</label>
          <div className="type-toggle">
            <button
              className={`type-toggle-btn ${type === 'MEAL' ? 'active' : ''}`}
              onClick={() => setType('MEAL')}
              type="button"
            >
              반찬
            </button>
            <button
              className={`type-toggle-btn ${type === 'INGREDIENT' ? 'active' : ''}`}
              onClick={() => setType('INGREDIENT')}
              type="button"
            >
              재료
            </button>
          </div>
        </div>

        {/* Expiration */}
        <div className="form-group">
          <label className="form-label">유통기한</label>
          <input
            type="date"
            className="form-input"
            value={expiresAt}
            onChange={(e) => setExpiresAt(e.target.value)}
          />
        </div>

        {/* Refrigerator */}
        <div className="form-group">
          <label className="form-label">냉장고</label>
          {loadingFridges ? (
            <div className="modal-loading">불러오는 중...</div>
          ) : refrigerators.length === 0 ? (
            <div className="modal-empty">
              등록된 냉장고가 없습니다.{' '}
              <a onClick={() => { onClose(); navigate('/settings/refrigerators') }}>
                냉장고 등록하기
              </a>
            </div>
          ) : (
            <div className="fridge-selector">
              {refrigerators.map((fridge) => (
                <button
                  key={fridge.refrigeratorId}
                  className={`fridge-selector-btn ${selectedFridgeId === fridge.refrigeratorId ? 'selected' : ''}`}
                  onClick={() => handleFridgeSelect(fridge.refrigeratorId, fridge.model)}
                  type="button"
                >
                  {fridge.nickname}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Compartments */}
        {selectedFridgeId !== null && (
          <div className="form-group">
            <label className="form-label">보관 위치</label>
            {loadingCompartments ? (
              <div className="modal-loading">칸 정보 불러오는 중...</div>
            ) : hasOverlay ? (
              <div className="compartment-section">
                <div className="compartment-image-container">
                  <img src={fridgeImageUrl} alt="냉장고" className="compartment-image-base" />
                  {overlayMap.map((overlay) => {
                    const comp = compartments.find(c => c.compartmentId === overlay.compartmentId)
                    if (!comp) return null
                    const isSelected = selectedCompartmentId === overlay.compartmentId
                    return (
                      <div
                        key={overlay.compartmentId}
                        className={`compartment-overlay ${isSelected ? 'selected' : ''}`}
                        style={{
                          top: `${overlay.top}%`,
                          left: `${overlay.left}%`,
                          width: `${overlay.width}%`,
                          height: `${overlay.height}%`,
                        }}
                        onClick={() => setSelectedCompartmentId(overlay.compartmentId)}
                        role="button"
                        aria-label={comp.name}
                      />
                    )
                  })}
                </div>
                {selectedCompartmentId && (
                  <div className="compartment-selected-label">
                    {compartments.find(c => c.compartmentId === selectedCompartmentId)?.name}
                  </div>
                )}
              </div>
            ) : (
              <div className="compartment-section">
                {fridgeImageUrl && (
                  <img src={fridgeImageUrl} alt="냉장고" className="compartment-image" />
                )}
                {Object.entries(grouped).map(([zone, items]) => (
                  <div key={zone}>
                    <div className="compartment-zone">{zone}</div>
                    <div className="compartment-grid">
                      {items.map((c) => (
                        <button
                          key={c.compartmentId}
                          className={`compartment-btn ${selectedCompartmentId === c.compartmentId ? 'selected' : ''}`}
                          onClick={() => setSelectedCompartmentId(c.compartmentId)}
                          type="button"
                        >
                          {c.name}
                        </button>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Actions */}
        <div className="modal-actions">
          <button
            className="btn-secondary"
            onClick={onClose}
            disabled={isSubmitting}
          >
            취소
          </button>
          <button
            className="btn-primary"
            onClick={handleSubmit}
            disabled={!isValid || isSubmitting}
          >
            {isSubmitting ? '추가 중...' : '추가'}
          </button>
        </div>
      </div>
    </div>
  )
}
