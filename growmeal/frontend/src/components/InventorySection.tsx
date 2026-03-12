import './InventorySection.css'

export interface InventoryItem {
  id: string
  name: string
  location: string
  locationColor?: string
  warning?: string
}

interface InventorySectionProps {
  items: InventoryItem[]
  onItemClick: (id: string) => void
  onAddClick: () => void
  onViewAll: () => void
}

export default function InventorySection({ items, onItemClick, onAddClick, onViewAll }: InventorySectionProps) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-title">인벤토리</span>
        <a className="card-link" onClick={onViewAll}>전체 보기</a>
      </div>
      <div className="inventory-grid">
        {items.map((item) => (
          <button
            key={item.id}
            className="inv-slot"
            onClick={() => onItemClick(item.id)}
            aria-label={item.name}
          >
            {item.warning && (
              <span className={`badge ${item.warning === '유통임박' ? 'badge-warn' : 'badge-ok'} inv-slot__badge`}>
                {item.warning}
              </span>
            )}
            <span className="inv-slot__name">{item.name}</span>
            <span
              className="inv-slot__location"
              style={item.locationColor ? { background: item.locationColor + '22', color: item.locationColor } : {}}
            >
              {item.location}
            </span>
          </button>
        ))}
        <button className="inv-slot inv-slot--add" onClick={onAddClick} aria-label="반찬 추가">
          <span className="inv-slot__add-icon">+</span>
          <span className="inv-slot__name">추가</span>
        </button>
      </div>
    </div>
  )
}
