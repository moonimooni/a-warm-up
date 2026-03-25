import { Routes, Route, Navigate } from 'react-router-dom'
import './index.css'
import './App.css'
import Home from './pages/Home'
import Login from './pages/Login'
import Onboarding from './pages/Onboarding'
import RefrigeratorSettings from './pages/RefrigeratorSettings'

function App() {
  const hasToken = !!localStorage.getItem('accessToken')
  const isOnboardingComplete = localStorage.getItem('onboarding_complete') === 'true'

  const getHomeElement = () => {
    if (!hasToken) return <Navigate to="/login" replace />
    if (!isOnboardingComplete) return <Navigate to="/onboarding" replace />
    return <Home />
  }

  return (
    <Routes>
      <Route path="/" element={getHomeElement()} />
      <Route path="/login" element={<Login />} />
      <Route path="/onboarding" element={<Onboarding />} />
      <Route path="/settings/refrigerators" element={<RefrigeratorSettings />} />
    </Routes>
  )
}

export default App
