import { Routes, Route, Navigate } from 'react-router-dom'
import './index.css'
import './App.css'
import Home from './pages/Home'
import Onboarding from './pages/Onboarding'
import RefrigeratorSettings from './pages/RefrigeratorSettings'

function App() {
  // Check if onboarding is complete
  const isOnboardingComplete = localStorage.getItem('onboarding_complete') === 'true'

  return (
    <Routes>
      <Route path="/" element={isOnboardingComplete ? <Home /> : <Navigate to="/onboarding" replace />} />
      <Route path="/onboarding" element={<Onboarding />} />
      <Route path="/settings/refrigerators" element={<RefrigeratorSettings />} />
    </Routes>
  )
}

export default App
