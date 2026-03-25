import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Login.css'

export default function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const isValid = email.trim() && password

  const handleLogin = async () => {
    if (!isValid) return
    setError('')
    setIsLoading(true)

    try {
      const res = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email.trim(), password })
      })
      const data = await res.json()

      if (res.ok && data.data) {
        localStorage.setItem('accessToken', data.data.accessToken)
        localStorage.setItem('refreshToken', data.data.refreshToken)
        window.location.href = '/'
      } else {
        setError(data.error?.message || '로그인에 실패했습니다.')
      }
    } catch {
      setError('네트워크 오류가 발생했습니다.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && isValid && !isLoading) {
      handleLogin()
    }
  }

  return (
    <div className="login-container">
      <div className="login-header">
        <div className="login-logo">🍚</div>
        <h1 className="login-title">Growmeal</h1>
        <p className="login-subtitle">아기 식사 관리</p>
      </div>

      <div className="login-form">
        {error && <div className="login-error">{error}</div>}

        <div className="form-group">
          <label className="form-label">이메일</label>
          <input
            type="email"
            className="form-input"
            placeholder="example@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyDown={handleKeyDown}
            autoComplete="email"
          />
        </div>

        <div className="form-group">
          <label className="form-label">비밀번호</label>
          <input
            type="password"
            className="form-input"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={handleKeyDown}
            autoComplete="current-password"
          />
        </div>

        <button
          className="btn-primary"
          onClick={handleLogin}
          disabled={!isValid || isLoading}
        >
          {isLoading ? '로그인 중...' : '로그인'}
        </button>
      </div>

      <div className="login-footer">
        <span className="login-footer-text">
          계정이 없으신가요?
          <a className="login-footer-link" onClick={() => navigate('/onboarding')}>
            회원가입
          </a>
        </span>
      </div>
    </div>
  )
}
