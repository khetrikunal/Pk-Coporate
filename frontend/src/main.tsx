import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import App from './App'
import './index.css'
import { warmUpBackend } from './services/apiClient'

// Wake up the backend immediately (Render free-tier cold start mitigation)
warmUpBackend()

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#fff',
            color: '#111',
            border: '1px solid #f0f0f0',
            borderRadius: '12px',
            fontSize: '14px',
            fontWeight: '500',
            boxShadow: '0 10px 40px rgba(0,0,0,0.12)',
          },
          success: {
            iconTheme: { primary: '#E10600', secondary: '#fff' },
          },
          error: {
            iconTheme: { primary: '#E10600', secondary: '#fff' },
          },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>
)
