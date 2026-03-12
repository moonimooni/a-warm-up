import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import OnboardingStep1 from './OnboardingStep1'
import OnboardingStep2 from './OnboardingStep2'

interface BabyData {
  name: string
  birthDate: string
  allergies: string[]
}

interface RefrigeratorData {
  nickname: string
  model: string
}

export default function Onboarding() {
  const navigate = useNavigate()
  const [currentStep, setCurrentStep] = useState(1)
  const [babyData, setBabyData] = useState<BabyData | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const handleStep1Complete = (data: BabyData) => {
    setBabyData(data)
    setCurrentStep(2)
  }

  const handleStep2Complete = async (refrigeratorData: RefrigeratorData) => {
    if (!babyData) return

    setIsLoading(true)

    try {
      // TODO: Replace with actual API calls
      // Step 1: Create baby profile
      console.log('Creating baby:', babyData)
      // const babyResponse = await fetch('/api/v1/babies', {
      //   method: 'POST',
      //   headers: {
      //     'Content-Type': 'application/json',
      //     'Authorization': `Bearer ${token}`
      //   },
      //   body: JSON.stringify(babyData)
      // })
      // const baby = await babyResponse.json()

      // Step 2: Create refrigerator
      console.log('Creating refrigerator:', refrigeratorData)
      // const fridgeResponse = await fetch('/api/v1/refrigerators', {
      //   method: 'POST',
      //   headers: {
      //     'Content-Type': 'application/json',
      //     'Authorization': `Bearer ${token}`
      //   },
      //   body: JSON.stringify(refrigeratorData)
      // })
      // const fridge = await fridgeResponse.json()

      // For now, simulate success
      setTimeout(() => {
        // Store completion flag in localStorage
        localStorage.setItem('onboarding_complete', 'true')
        // Force reload to update the isOnboardingComplete check
        window.location.href = '/'
      }, 500)
    } catch (error) {
      console.error('Onboarding failed:', error)
      alert('등록 중 오류가 발생했습니다. 다시 시도해주세요.')
      setIsLoading(false)
    }
  }

  const handleBack = () => {
    setCurrentStep(1)
  }

  if (isLoading) {
    return (
      <div className="onboarding-container">
        <div style={{ textAlign: 'center', padding: '60px 20px' }}>
          <div style={{ fontSize: '48px', marginBottom: '20px' }}>🎉</div>
          <h2 style={{ fontSize: '20px', fontWeight: '700', color: 'var(--color-text-main)' }}>
            설정 중입니다...
          </h2>
        </div>
      </div>
    )
  }

  return (
    <>
      {currentStep === 1 && (
        <OnboardingStep1 onNext={handleStep1Complete} />
      )}
      {currentStep === 2 && (
        <OnboardingStep2 onBack={handleBack} onComplete={handleStep2Complete} />
      )}
    </>
  )
}
